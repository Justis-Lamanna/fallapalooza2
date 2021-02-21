package com.github.lucbui.birb;

import com.github.lucbui.birb.config.BracketCellConfig;
import com.github.lucbui.birb.config.ConfigService;
import com.github.lucbui.birb.obj.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Saver {
    private static final Pattern REPLACE_WITH_SPACE = Pattern.compile("\\s+");
    private static final File BASE = new File("output");
    private static final File BRACKET = new File(BASE, "tournament");
    private static final File DISPLAY = new File(BASE, "display");
    private static final File TEAM = new File(BASE, "matches");

    public void initialize() {
        if(System.getProperty("clean") != null) {
            System.out.println("Cleaning files...");
            clean(BASE);
            System.out.println("Complete");
        }
        BASE.mkdir();
        BRACKET.mkdir();
        DISPLAY.mkdir();
        TEAM.mkdir();
    }

    private boolean clean(File directory) {
        File[] allContents = directory.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                clean(file);
            }
        }
        return directory.delete();
    }

    public void save(Bracket bracket) {
        bracket.getRounds().parallelStream()
                .forEach(this::save);
    }

    public void save(BracketRound round) {
        String filename = round.getName().toLowerCase();
        File roundDirectory = new File(BRACKET, filename);
        roundDirectory.mkdir();
        if(round.isWinnerRound()) {
            BracketMatchup.Team team = round.getMatchups().get(0).getTeamOne();
            outputToFile(roundDirectory, "name", team.getName());
            outputToFile(roundDirectory, "total", team.getThisRound().getTotal());
            IntStream.range(0, team.getPlayers().size())
                    .parallel()
                    .forEach(i -> outputPlayer(roundDirectory, team.getPlayers().get(i), i));
        }
        else {
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
    }

    private void save(File directory, BracketMatchup.Team team, int teamNumber) {
        File teamDirectory = new File(directory, "team_" + teamNumber);
        teamDirectory.mkdir();
        outputToFile(teamDirectory, "name", team.getName());
        outputToFile(teamDirectory, "total", team.getThisRound().getTotal());
        IntStream.range(0, team.getPlayers().size())
                .parallel()
                .forEach(i -> {
                    outputPlayer(teamDirectory, team.getPlayers().get(i), i);

                    Score score = team.getThisRound().getScores().get(i);
                    IntStream.range(0, score.getScore().length)
                            .parallel()
                            .forEach(episodeIdx -> {
                                outputToFile(teamDirectory, "player_" + (i + 1) + "_episode_" + (episodeIdx + 1), score.getScore()[episodeIdx]);
                            });
                });
    }

    public void save(List<Team> teams) {
        for(Team team : teams) {
            if(team.getDisplay() != null) {
                save(new File(DISPLAY, Integer.toString(team.getDisplay())), team);
            }
        }
    }

    private void save(File directory, Team team) {
        directory.mkdir();
        outputToFile(directory, "name", team.getName());
        IntStream.range(0, team.getPlayers().size())
                .parallel()
                .forEach(i -> outputPlayer(directory, team.getPlayers().get(i), i));
        team.getRounds().parallelStream()
                .forEach(round -> outputRound(directory, team, round, false));

        Round currentRound;
        String userSpecifiedRound = System.getProperty("round");
        if(userSpecifiedRound != null) {
            currentRound = team.getRounds().get(Integer.parseInt(userSpecifiedRound));
        } else {
            currentRound = team.getLastPlayedRound();
        }
        outputRound(directory, team, currentRound, true);
    }

    private void outputPlayer(File directory, Player player, int i) {
        outputToFile(directory, "player_" + (i + 1) + "_name", player.getName());
        outputToFile(directory, "player_" + (i + 1) + "_name_pronouns", player.getNameWithPronouns());
    }

    private void outputRound(File directory, Team team, Round round, boolean current) {
        String roundNumberStr = current ? "current_round" : "round_" + (round.getNumber() + 1);
        outputToFile(directory, roundNumberStr + "_name", round.getRoundNameBadge());
        outputToFile(directory, roundNumberStr + "_episode", round.getRoundBadge());
        outputToFile(directory, roundNumberStr + "_total", round.getTotal());

        IntStream.range(0, team.getPlayers().size())
                .parallel()
                .forEach(i -> {
                    Score score = round.getScores().get(i);
                    IntStream.range(0, score.getScore().length)
                            .parallel()
                            .forEach(episode -> {
                                String field = roundNumberStr + "_player_" + (i + 1) + "_episode_" + (episode + 1);
                                outputToFile(directory, field, score.getScore()[episode]);
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
