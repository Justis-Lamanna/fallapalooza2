package com.github.lucbui.birb.builder;

import com.github.lucbui.birb.ParserUtils;
import com.github.lucbui.birb.config.ConfigService;
import com.github.lucbui.birb.config.FallapaloozaConfig;
import com.github.lucbui.birb.obj.*;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TeamBuilder {
    public static Team fromExcel(List<ValueRange> ranges) {
        Iterator<ValueRange> iter = ranges.iterator();
        String name = ParserUtils.getSingleValue(iter.next());
        Integer seed = ParserUtils.getSingleValueInteger(iter.next());
        Integer display = ParserUtils.getSingleValueInteger(iter.next());
        List<Player> players = parsePlayers(iter.next(), iter.next());

        List<Round> rounds = parseRounds(iter);

        return new Team(name, display, seed, players, rounds);
    }

    private static List<Player> parsePlayers(ValueRange nameCells, ValueRange pronounCells) {
        int playersPerTeam = ConfigService.getConfig().getPlayersPerTeam();
        List<String> names = ParserUtils.pad(ParserUtils.getMultiValue(nameCells), playersPerTeam, "");
        List<String> pronouns = ParserUtils.pad(ParserUtils.getMultiValue(pronounCells), playersPerTeam, "");

        return IntStream.range(0, names.size())
                .mapToObj(idx -> new Player(names.get(idx), pronouns.get(idx)))
                .collect(Collectors.toList());
    }

    private static List<Round> parseRounds(Iterator<ValueRange> iter) {
        FallapaloozaConfig config = ConfigService.getConfig();
        List<Round> rounds = new ArrayList<>();
        for(int roundIdx = 0; roundIdx < config.getNumberOfRounds(); roundIdx++) {
            List<Score> scores = new ArrayList<>();
            int numberOfScores = config.getTeamCard().getRound().getEpisodeCount().get(roundIdx);
            for(int playerIdx = 0; playerIdx < config.getPlayersPerTeam(); playerIdx++) {
                List<Integer> indivScores = ParserUtils.pad(ParserUtils.getMultiValueInteger(iter.next()), numberOfScores, 0);
                scores.add(new Score(indivScores.toArray(new Integer[0])));
            }
            Integer total = ParserUtils.getSingleValueInteger(iter.next());
            RoundState roundState = RoundState.parse(ParserUtils.getSingleValue(iter.next()));
            rounds.add(new Round(roundIdx, total == null ? 0 : total, roundState, scores));
        }
        return rounds;
    }
}
