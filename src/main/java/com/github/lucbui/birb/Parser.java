package com.github.lucbui.birb;

import com.github.lucbui.birb.obj.*;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Parser {
    public static final String SHEET = "Scorecard";
    public static final String BRACKET_SHEET = "Bracket";

    private static final int NUMBER_OF_TEAMS = 32;
    private static final int PLAYERS_PER_TEAM = 2;
    private static final int NUMBER_OF_EPISODES = 5;
    private static final int NUMBER_OF_ROUNDS = 5;

    private static final int RESULTS_WINDOW_SIZE = 5 + (PLAYERS_PER_TEAM * NUMBER_OF_EPISODES) + (2 * NUMBER_OF_ROUNDS);

    private static final Point TEAM_CELL_START = new Point(11, 2); // To the top-left corner of the first team's table
    private static final Point NAME_CELL = new Point(0, 1); //From top-left corner to name cell
    private static final Point SEED_CELL = new Point(7, 1); //From tlc to seed cell
    private static final Point DISPLAY_CELL = new Point(7, 3); //From tlc to display cell
    private static final Point PLAYER_NAME_CELL = new Point(2, 1); //From tlc to Player 1 name cell
    private static final Point PLAYER_NUMBER_CELL = new Point(2, 2); //From tlc to Player 1 number cell
    private static final Point ROUND_1_ORIGIN = new Point(0, 5); //From tlc to First cell of scores

    private final String spreadsheetId;

    public Parser(String spreadsheetId) {
        this.spreadsheetId = spreadsheetId;
    }

    public List<Team> getTeams(Sheets sheets) throws IOException {
        List<Team> list = new ArrayList<>();
        List<String> ranges = new ArrayList<>();
        for(int teamNum = 0; teamNum < NUMBER_OF_TEAMS; teamNum++) {
            ranges.addAll(getRanges(teamNum));
        }
        BatchGetValuesResponse response = sheets.spreadsheets().values().batchGet(spreadsheetId).setRanges(ranges).execute();
        for(int teamNum = 0; teamNum < NUMBER_OF_TEAMS; teamNum++) {
            try {
                list.add(getTeam(response, teamNum));
            } catch (RuntimeException ex) {
                throw new RuntimeException("Error parsing team " + (teamNum + 1) + ". Parsing cancelled", ex);
            }
        }
        return list;
    }

    private Team getTeam(BatchGetValuesResponse response, int teamNum) {
        int start = teamNum * RESULTS_WINDOW_SIZE;
        int end = start + RESULTS_WINDOW_SIZE;
        List<ValueRange> values = response.getValueRanges().subList(start, end);
        String name = ParserUtils.getSingleValue(values.get(0));
        Integer seed = ParserUtils.getSingleValueInteger(values.get(1), "seed");
        Integer display = ParserUtils.getSingleValueInteger(values.get(2), "display");
        return new Team(name, display, seed, getPlayers(values), getRounds(values));
    }

    private List<Player> getPlayers(List<ValueRange> values) {
        List<String> names = ParserUtils.getMultiValue(values.get(3), PLAYERS_PER_TEAM);
        List<String> nameWithPronouns = ParserUtils.getMultiValue(values.get(4), PLAYERS_PER_TEAM);
        List<Player> players = new ArrayList<>();
        for(int pNum = 0; pNum < PLAYERS_PER_TEAM; pNum++) {
            players.add(new Player(names.get(pNum), nameWithPronouns.get(pNum)));
        }
        return players;
    }

    private List<Round> getRounds(List<ValueRange> values) {
        List<Round> rounds = new ArrayList<>();
        int start = 5;
        for(int rNum = 0; rNum < NUMBER_OF_ROUNDS; rNum++) {
            List<Score> scores = new ArrayList<>();
            for(int pNum = 0; pNum < PLAYERS_PER_TEAM; pNum++) {
                //P1: 5, 9, 13, 17, 21
                //P2: 6, 10, 14, 18, 22
                List<Integer> scoreRaw = ParserUtils.getMultiValueInteger(
                        values.get(5 + pNum + (rNum * (PLAYERS_PER_TEAM + 2))),
                        NUMBER_OF_EPISODES,
                        "round " + (rNum + 1) + " player " + (pNum + 1));
                scores.add(new Score(scoreRaw.toArray(new Integer[0])));
            }
            //7, 11, 15, 19, 23
            Integer totalOrNull = ParserUtils.getSingleValueInteger(values.get(7 + (rNum * (PLAYERS_PER_TEAM + 2))), "round " + (rNum + 1) + "total");
            int total = totalOrNull == null ? 0 : totalOrNull;
            //8, 12, 16, 20, 24
            RoundState roundState = RoundState.parse(ParserUtils.getSingleValue(values.get(8 + (rNum * (PLAYERS_PER_TEAM + 2)))));
            rounds.add(new Round(rNum, total, roundState, scores));
        }
        return rounds;
    }

    public List<TournamentRound> getTournamentRounds(Sheets sheets) throws IOException {
        List<String> ranges = new ArrayList<>();
        List<TournamentRound> rounds = new ArrayList<>();
        for(int round = 0; round < NUMBER_OF_ROUNDS + 1; round++) {
            ranges.addAll(getRangesForRound(round));
        }
        BatchGetValuesResponse response = sheets.spreadsheets().values().batchGet(spreadsheetId).setRanges(ranges).execute();
        int start = 0;
        for(int round = 0; round < NUMBER_OF_ROUNDS + 1; round++) {
            int windowSize = getWindowSize(round);
            List<ValueRange> values = response.getValueRanges().subList(start, start + windowSize);
            start += windowSize;
            List<TournamentMatchup> scores = new ArrayList<>();
            TournamentMatchup.Score prevScore = null;
            for(ValueRange range : values) {
                List<String> extracts = ParserUtils.getMultiValueHorizontal(range, 2);
                String team = extracts.get(0);
                Integer score;
                try {
                    score = Integer.parseInt(extracts.get(1));
                } catch (NumberFormatException ex) {
                    score = null;
                }
                if(prevScore == null) {
                    prevScore = new TournamentMatchup.Score(team, score);
                } else {
                    scores.add(new TournamentMatchup(prevScore, new TournamentMatchup.Score(team, score)));
                    prevScore = null;
                }
            }
            rounds.add(new TournamentRound(RoundUtils.getRoundName(round), scores));
        }
        return rounds;
    }

    private List<String> getRanges(int teamNumber) {
        Point origin = TEAM_CELL_START.addRow(teamNumber * 9);
        List<String> ranges = new ArrayList<>();
        //Constants
        ranges.add(origin.add(NAME_CELL).toExcelWithSheet(SHEET));
        ranges.add(origin.add(SEED_CELL).toExcelWithSheet(SHEET));
        ranges.add(origin.add(DISPLAY_CELL).toExcelWithSheet(SHEET));
        ranges.add(origin.add(PLAYER_NAME_CELL).toRow(PLAYERS_PER_TEAM - 1).toExcelWithSheet(SHEET));
        ranges.add(origin.add(PLAYER_NUMBER_CELL).toRow(PLAYERS_PER_TEAM - 1).toExcelWithSheet(SHEET));

        for(int round = 0; round < NUMBER_OF_ROUNDS; round++) {
            Point roundOrigin = origin.add(ROUND_1_ORIGIN).addCol(PLAYERS_PER_TEAM * round);
            for(int player = 0; player < PLAYERS_PER_TEAM; player++) {
                Point playerOrigin = roundOrigin.addCol(player).addRow(2);
                ranges.add(playerOrigin.toRow(NUMBER_OF_EPISODES - 1).toExcelWithSheet(SHEET));
            }
            Point bottom = roundOrigin.addRow(NUMBER_OF_EPISODES + 2);
            ranges.add(bottom.toExcelWithSheet(SHEET));
            ranges.add(bottom.addCol(1).toExcelWithSheet(SHEET));
        }

        return ranges;
    }

    private List<String> getRangesForRound(int round) {
        switch (round) {
            case 0: return getLazyRanges(new Point(2, 2), 2, 32);
            case 1: return getLazyRanges(new Point(3, 5), 4, 16);
            case 2: return getLazyRanges(new Point(5, 8), 8, 8);
            case 3: return getLazyRanges(new Point(9, 11), 16, 4);
            case 4: return getLessLazyRanges(14,17, 49, 59, 63);
            case 5: return getLessLazyRanges(17,33, 35, 61, 63);
            default: throw new IllegalStateException("Round # " + round + " is unexpected");
        }
    }

    private int getWindowSize(int round) {
        switch (round) {
            case 0: return 32;
            case 1: return 16;
            case 2: return 8;
            case 3:
            case 4:
            case 5: return 4;
            default: throw new IllegalStateException("Round # " + round + " is unexpected");
        }
    }

    private List<String> getLazyRanges(Point start, int rowsToSkip, int total) {
        return IntStream.range(0, total)
                .mapToObj(ctr -> start.addRow(rowsToSkip * ctr).toCol(1))
                .map(point -> point.toExcelWithSheet(BRACKET_SHEET))
                .collect(Collectors.toList());
    }

    private List<String> getLessLazyRanges(int column, int... rows) {
        return IntStream.of(rows)
                .mapToObj(row -> new Point(row, column).toCol(1))
                .map(point -> point.toExcelWithSheet(BRACKET_SHEET))
                .collect(Collectors.toList());
    }
}
