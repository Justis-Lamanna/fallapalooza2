package com.github.lucbui.birb;

import com.github.lucbui.birb.builder.RoundsBuilder;
import com.github.lucbui.birb.builder.TeamBuilder;
import com.github.lucbui.birb.config.*;
import com.github.lucbui.birb.obj.*;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Parser {
    private final FallapaloozaConfig config;

    public Parser(FallapaloozaConfig config) {
        this.config = config;
    }

    private BatchGetValuesResponse getFromSpreadsheet(Sheets sheets, List<String> ranges) throws IOException {
        return sheets.spreadsheets()
                .values()
                .batchGet(config.getSpreadsheetId())
                .setRanges(ranges)
                .execute();
    }

    public List<Team> getTeams(Sheets sheets) throws IOException {
        List<String> ranges = IntStream.range(0, config.getNumberOfTeams())
                .mapToObj(this::getTeamRanges)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        BatchGetValuesResponse response = getFromSpreadsheet(sheets, ranges);

        int window = ranges.size() / config.getNumberOfTeams();
        return IntStream.iterate(0, i -> i += window)
                .limit(config.getNumberOfTeams())
                .mapToObj(start -> TeamBuilder.fromExcel(response.getValueRanges().subList(start, start + window)))
                .collect(Collectors.toList());
    }

    public List<TournamentRound> getTournamentRounds(Sheets sheets) throws IOException {
        List<String> ranges = config.getBracket().getCells()
                .stream()
                .map(this::getRangesForRound)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        BatchGetValuesResponse response = getFromSpreadsheet(sheets, ranges);

        List<TournamentRound> rounds = new ArrayList<>();
        int start = 0;
        for(BracketCellConfig cellConfig : config.getBracket().getCells()) {
            int entries = cellConfig.getRows().length;
            rounds.add(RoundsBuilder.fromExcel(response.getValueRanges()
                            .subList(start, start + entries)));
            start += entries;
        }

        return rounds;
    }

    private List<String> getTeamRanges(int teamNumber) {
        TeamConfig team = config.getTeamCard().getTeam();
        TeamPlayerConfig player = config.getTeamCard().getPlayer();
        TeamRoundConfig round = config.getTeamCard().getRound();

        String scorecardSheetName = config.getTeamCard().getSheetName();
        Point origin = config.getTeamCard().getOrigin().toPoint()
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
                        .toRelative(config.getEpisodeCountForRound(roundNumber) - 1,
                                round.getScore().getDirection());
                ranges.add(scoreRange.toExcelWithSheet(scorecardSheetName));
            }
            ranges.add(origin.add(round.getTotal().toPoint())
                    .addCol(columnModifier)
                    .toExcelWithSheet(scorecardSheetName));
            ranges.add(origin.add(round.getStatus().toPoint())
                    .addCol(columnModifier)
                    .toExcelWithSheet(scorecardSheetName));
        }
        return ranges;
    }

    private List<String> getRangesForRound(BracketCellConfig cell) {
        return IntStream.of(cell.getRows())
                .mapToObj(row -> new Point(row, cell.getCol()))
                .map(point -> point.toExcelWithSheet(config.getBracket().getSheetName()))
                .collect(Collectors.toList());
    }
}
