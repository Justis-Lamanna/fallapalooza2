package com.github.lucbui.birb;

import com.github.lucbui.birb.config.*;
import com.github.lucbui.birb.obj.*;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Parser {

    private final FallapaloozaConfig config;

    public Parser(FallapaloozaConfig config) {
        this.config = config;
    }

    public List<Team> getTeams(Sheets sheets) throws IOException {
        List<Team> list = new ArrayList<>();
        List<String> ranges = new ArrayList<>();
        for(int teamNum = 0; teamNum < config.getNumberOfTeams(); teamNum++) {
            List<String> teamRanges = getTeamRanges(teamNum);
            ranges.addAll(teamRanges);
        }
        BatchGetValuesResponse response = sheets.spreadsheets()
                .values()
                .batchGet(config.getSpreadsheetId())
                .setRanges(ranges)
                .execute();
//
//        for(int teamNum = 0; teamNum < config.getNumberOfTeams(); teamNum++) {
//            try {
//                list.add(getTeam(response, teamNum));
//            } catch (RuntimeException ex) {
//                throw new RuntimeException("Error parsing team " + (teamNum + 1) + ". Parsing cancelled", ex);
//            }
//        }

        int window = ranges.size() / config.getNumberOfTeams();
        for(int windowIdx = 0; windowIdx < config.getNumberOfTeams(); windowIdx++) {
            String s = response.getValueRanges()
                    .subList(windowIdx * window, (windowIdx + 1) * window)
                    .stream()
                    .map(ParserUtils::flatten)
                    .map(Objects::toString)
                    .collect(Collectors.joining(","));
            System.out.println("Team " + windowIdx + ":" + s);
        }

        return list;
    }

//    private Team getTeam(BatchGetValuesResponse response, int teamNum) {
//        int start = teamNum * getWindowSize();
//        int end = start + getWindowSize();
//        List<ValueRange> values = response.getValueRanges().subList(start, end);
//        String name = ParserUtils.getSingleValue(values.get(0));
//        Integer seed = ParserUtils.getSingleValueInteger(values.get(1), "seed");
//        Integer display = ParserUtils.getSingleValueInteger(values.get(2), "display");
//        return new Team(name, display, seed, getPlayers(values), getRounds(values));
//    }
//
//    private List<Player> getPlayers(List<ValueRange> values) {
//        List<String> names = ParserUtils.getMultiValue(values.get(3), config.getPlayersPerTeam());
//        List<String> nameWithPronouns = ParserUtils.getMultiValue(values.get(4), config.getPlayersPerTeam());
//        List<Player> players = new ArrayList<>();
//        for(int pNum = 0; pNum < config.getPlayersPerTeam(); pNum++) {
//            players.add(new Player(names.get(pNum), nameWithPronouns.get(pNum)));
//        }
//        return players;
//    }
//
//    private List<Round> getRounds(List<ValueRange> values) {
//        List<Round> rounds = new ArrayList<>();
//        int start = 5;
//        for(int rNum = 0; rNum < config.getNumberOfRounds(); rNum++) {
//            List<Score> scores = new ArrayList<>();
//            for(int pNum = 0; pNum < config.getPlayersPerTeam(); pNum++) {
//                //P1: 5, 9, 13, 17, 21
//                //P2: 6, 10, 14, 18, 22
//                List<Integer> scoreRaw = ParserUtils.getMultiValueInteger(
//                        values.get(5 + pNum + (rNum * (config.getPlayersPerTeam() + 2))),
//                        config.get,
//                        "round " + (rNum + 1) + " player " + (pNum + 1));
//                scores.add(new Score(scoreRaw.toArray(new Integer[0])));
//            }
//            //7, 11, 15, 19, 23
//            Integer totalOrNull = ParserUtils.getSingleValueInteger(values.get(7 + (rNum * (PLAYERS_PER_TEAM + 2))), "round " + (rNum + 1) + "total");
//            int total = totalOrNull == null ? 0 : totalOrNull;
//            //8, 12, 16, 20, 24
//            RoundState roundState = RoundState.parse(ParserUtils.getSingleValue(values.get(8 + (rNum * (PLAYERS_PER_TEAM + 2)))));
//            rounds.add(new Round(rNum, total, roundState, scores));
//        }
//        return rounds;
//    }

    public List<TournamentRound> getTournamentRounds(Sheets sheets) throws IOException {
        List<String> ranges = new ArrayList<>();
        List<TournamentRound> rounds = new ArrayList<>();
//        for(int round = 0; round < NUMBER_OF_ROUNDS + 1; round++) {
//            ranges.addAll(getRangesForRound(round));
//        }
//        BatchGetValuesResponse response = sheets.spreadsheets().values().batchGet(spreadsheetId).setRanges(ranges).execute();
//        int start = 0;
//        for(int round = 0; round < NUMBER_OF_ROUNDS + 1; round++) {
//            int windowSize = getWindowSize(round);
//            List<ValueRange> values = response.getValueRanges().subList(start, start + windowSize);
//            start += windowSize;
//            List<TournamentMatchup> scores = new ArrayList<>();
//            TournamentMatchup.Score prevScore = null;
//            for(ValueRange range : values) {
//                List<String> extracts = ParserUtils.getMultiValueHorizontal(range, 2);
//                String team = extracts.get(0);
//                Integer score;
//                try {
//                    score = Integer.parseInt(extracts.get(1));
//                } catch (NumberFormatException ex) {
//                    score = null;
//                }
//                if(prevScore == null) {
//                    prevScore = new TournamentMatchup.Score(team, score);
//                } else {
//                    scores.add(new TournamentMatchup(prevScore, new TournamentMatchup.Score(team, score)));
//                    prevScore = null;
//                }
//            }
//            rounds.add(new TournamentRound(RoundUtils.getRoundName(round), scores));
//        }
        return rounds;
    }

    private List<String> getTeamRanges(int teamNumber) {
        TeamConfig team = config.getTeamCard().getTeam();
        TeamPlayerConfig player = config.getTeamCard().getPlayer();
        TeamRoundConfig round = config.getTeamCard().getRound();

        String scorecardSheetName = config.getScorecardSheetName();
        Point origin = config.getOrigin().toPoint()
                .addRow(teamNumber * config.getTeamCard().getHeight());

        //Begin fields
        List<String> ranges = new ArrayList<>();
        //Team Name
        ranges.add(origin.add(team.getName().toPoint())
                .toExcelWithSheet(scorecardSheetName));
        //Team Seed #
        ranges.add(origin.add(team.getSeed().toPoint())
                .toExcelWithSheet(scorecardSheetName));
        //Team Display #
        ranges.add(origin.add(team.getDisplay().toPoint())
                .toExcelWithSheet(scorecardSheetName));
        //Player Names
        ranges.add(origin.add(player.getName().toPoint())
                .toRelative(config.getPlayersPerTeam() - 1,
                        player.getName().getDirection())
                .toExcelWithSheet(scorecardSheetName));
        //Player Pronouns
        ranges.add(origin.add(player.getPronouns().toPoint())
                .toRelative(config.getPlayersPerTeam() - 1,
                        player.getPronouns().getDirection())
                .toExcelWithSheet(scorecardSheetName));

        for(int roundNumber = 0; roundNumber < config.getNumberOfRounds(); roundNumber++) {
            int columnModifier = config.getPlayersPerTeam() * roundNumber;
            for(int playerNumber = 0; playerNumber < config.getPlayersPerTeam(); playerNumber++) {
                Range scoreRange = origin.add(round.getScore().toPoint())
                        .addCol(columnModifier)
                        .addCol(playerNumber)
                        .toRelative(config.getRoundConfig(roundNumber).getNumberOfEpisodes() - 1,
                                round.getScore().getDirection());
                ranges.add(scoreRange.toExcelWithSheet(config.getScorecardSheetName()));
            }
            ranges.add(origin.add(round.getTotal().toPoint())
                    .addCol(columnModifier)
                    .toExcelWithSheet(config.getScorecardSheetName()));
            ranges.add(origin.add(round.getStatus().toPoint())
                    .addCol(columnModifier)
                    .toExcelWithSheet(config.getScorecardSheetName()));
        }
        return ranges;
    }
//
//    private List<String> getRangesForRound(int round) {
//        switch (round) {
//            case 0: return getLazyRanges(new Point(2, 2), 2, 32);
//            case 1: return getLazyRanges(new Point(3, 5), 4, 16);
//            case 2: return getLazyRanges(new Point(5, 8), 8, 8);
//            case 3: return getLazyRanges(new Point(9, 11), 16, 4);
//            case 4: return getLessLazyRanges(14,17, 49, 59, 63);
//            case 5: return getLessLazyRanges(17,33, 35, 61, 63);
//            default: throw new IllegalStateException("Round # " + round + " is unexpected");
//        }
//    }
//
//    private int getWindowSize(int round) {
//        switch (round) {
//            case 0: return 32;
//            case 1: return 16;
//            case 2: return 8;
//            case 3:
//            case 4:
//            case 5: return 4;
//            default: throw new IllegalStateException("Round # " + round + " is unexpected");
//        }
//    }
//
//    private List<String> getLazyRanges(Point start, int rowsToSkip, int total) {
//        return IntStream.range(0, total)
//                .mapToObj(ctr -> start.addRow(rowsToSkip * ctr).toCol(1))
//                .map(point -> point.toExcelWithSheet(BRACKET_SHEET))
//                .collect(Collectors.toList());
//    }
//
//    private List<String> getLessLazyRanges(int column, int... rows) {
//        return IntStream.of(rows)
//                .mapToObj(row -> new Point(row, column).toCol(1))
//                .map(point -> point.toExcelWithSheet(BRACKET_SHEET))
//                .collect(Collectors.toList());
//    }
}
