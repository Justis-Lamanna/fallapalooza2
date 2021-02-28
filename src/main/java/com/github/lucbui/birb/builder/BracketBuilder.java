package com.github.lucbui.birb.builder;

import com.github.lucbui.birb.config.BracketCellConfig;
import com.github.lucbui.birb.config.ConfigService;
import com.github.lucbui.birb.obj.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BracketBuilder {
    public static Bracket process(List<Team> teams, List<TournamentRound> rounds) {
        Map<String, Team> teamsByName = teams.stream()
                .collect(Collectors.toMap(Team::getName, Function.identity()));

        List<BracketRound> bracketRounds = new ArrayList<>();
        for(int roundId = 0; roundId < rounds.size(); roundId++) {
            BracketCellConfig config = getConfigForRound(roundId);
            List<BracketMatchup> bracketMatchups = rounds.get(roundId).getTournamentMatchups().stream()
                    .map(matchup -> {
                        int roundIdx = config.getTeamCardRoundIdx();
                        Team teamOne = teamsByName.get(matchup.getTeamOne());
                        Team teamTwo = teamsByName.get(matchup.getTeamTwo());
                        return new BracketMatchup(parseTeam(teamOne, roundIdx), parseTeam(teamTwo, roundIdx));
                    })
                    .collect(Collectors.toList());
            bracketRounds.add(new BracketRound(config.getName(), bracketMatchups));
        }
        return new Bracket(bracketRounds);
    }

    private static BracketCellConfig getConfigForRound(int roundId) {
        return ConfigService.getConfig()
                .getBracket()
                .getCells()
                .get(roundId);
    }

    private static BracketMatchup.Team parseTeam(Team team, int roundIdx) {
        if(team == null) return BracketMatchup.Team.EMPTY_TEAM;
        if(roundIdx == -1) {
            roundIdx = team.getRounds().size() - 1;
        }
        return new BracketMatchup.Team(team.getName(), team.getPlayers(), team.getRounds().get(roundIdx));
    }
}
