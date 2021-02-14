package com.github.lucbui.birb;

import com.github.lucbui.birb.config.BracketCellConfig;
import com.github.lucbui.birb.config.ConfigService;
import com.github.lucbui.birb.obj.Bracket;
import com.github.lucbui.birb.obj.BracketMatchup;
import com.github.lucbui.birb.obj.BracketRound;
import com.github.lucbui.birb.obj.Score;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Saver {
    private static final Pattern REPLACE_WITH_SPACE = Pattern.compile("\\s+");
    private static final File BASE = new File("output");

    public void save(Bracket bracket) {
        bracket.getRounds().parallelStream()
                .forEach(this::save);
    }

    public void save(BracketRound round) {
        String filename = REPLACE_WITH_SPACE.matcher(round.getName().toLowerCase())
                .replaceAll("_");
        File bracketDirectory = new File(BASE, "bracket");
        File roundDirectory = new File(bracketDirectory, filename);
        roundDirectory.mkdirs();
        IntStream.range(0, round.getMatchups().size())
                .parallel()
                .forEach(matchupIdx -> {
                    File matchupDirectory = new File(roundDirectory, "matchup_" + (matchupIdx + 1));
                    matchupDirectory.mkdir();
                    BracketMatchup matchup = round.getMatchups().get(matchupIdx);
                    IntStream.rangeClosed(1, 2)
                            .parallel()
                            .forEach(i -> {
                                save(matchupDirectory, i == 1 ? matchup.getTeamOne() : matchup.getTeamTwo(), i);
                            });
                });
    }

    public void save(File directory, BracketMatchup.Team team, int teamNumber) {
        File teamDirectory = new File(directory, "team_" + teamNumber);
        teamDirectory.mkdir();
        outputToFile(teamDirectory, "name", team.getName());
        outputToFile(teamDirectory, "total", team.getThisRound().getTotal());
        if(team.getThisRound().getNumber() >= 0) {
            outputToFile(teamDirectory, "current_episode", (team.getThisRound().getCurrentEpisode() + 1) + "/" +
                    ConfigService.getConfig().getEpisodeCountForRound(team.getThisRound().getNumber()));
        }
        IntStream.range(0, team.getPlayers().size())
                .parallel()
                .forEach(i -> {
                    outputToFile(teamDirectory, "player_" + (i + 1), team.getPlayers().get(i).getName());
                    outputToFile(teamDirectory, "player_pronouns_" + (i + 1), team.getPlayers().get(i).getNameWithPronouns());

                    Score score = team.getThisRound().getScores().get(i);
                    IntStream.range(0, score.getScore().length)
                            .parallel()
                            .forEach(episodeIdx -> {
                                outputToFile(teamDirectory, "player_" + (i + 1) + "_episode_" + (episodeIdx + 1), score.getScore()[episodeIdx]);
                            });
                });
    }

    private void outputToFile(File directory, String field, Object output) {
        File file = new File(directory, field + ".txt");
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            if(Objects.equals(output, 0)) {
                output = "";
            }
            writer.write(Objects.toString(output + " ", ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BracketCellConfig getConfigForRound(int roundId) {
        return ConfigService.getConfig()
                .getBracket()
                .getCells()
                .get(roundId);
    }
}
