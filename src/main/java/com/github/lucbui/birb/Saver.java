package com.github.lucbui.birb;

import com.github.lucbui.birb.obj.*;

import java.io.*;
import java.util.List;
import java.util.Objects;

public class Saver {
    public static final File BASE = new File("output");

    public void outputTeams(List<Team> teams) throws IOException {
        teams.parallelStream()
                .forEach(team -> {
                    try {
                        outputTeam(team, new File(BASE, "matches/team_" + team.getSeed()));

                        //Create display copy
                        if (team.getDisplay() != null) {
                            int display = team.getDisplay();
                            File symlink = new File(BASE, "display/" + display);
                            outputTeam(team, symlink);
                        }
                    } catch (IOException e) {
                        System.out.println("Encountered exception writing team " + team.getName());
                        e.printStackTrace();
                    }
                });
    }

    private void outputTeam(Team team, File directory) throws IOException {
        outputToFile(directory, "name", team.getName().toUpperCase());
        for (int idx = 0; idx < team.getPlayers().size(); idx++) {
            Player player = team.getPlayers().get(idx);
            outputToFile(directory, "player_" + (idx + 1) + "_name", player.getName());
            outputToFile(directory, "player_" + (idx + 1) + "_name_pronouns", player.getNameWithPronouns());
        }

        for (int round = 0; round < team.getRounds().size(); round++) {
            Round roundObj = team.getRounds().get(round);
            for (int player = 0; player < roundObj.getScores().size(); player++) {
                Score score = roundObj.getScores().get(player);
                for (int episode = 0; episode < score.getScore().length; episode++) {
                    outputToFile(directory, "round_" + (round + 1) + "_player_" + (player + 1) + "_episode_" + (episode + 1), score.getScore()[episode]);
                }
            }
            outputToFile(directory, "round_" + (round + 1) + "_total", roundObj.getTotal());
        }

        Round selectedRound = getSelectedRound(team);
        Round nameNumberRound = selectedRound;
        if(selectedRound.isFull()) {
            int nextRound = selectedRound.getNumber() + 1;
            List<Round> rounds = team.getRounds();
            if(nextRound >= rounds.size()) {
                nameNumberRound = rounds.get(rounds.size() - 1);
            } else {
                nameNumberRound = rounds.get(nextRound);
            }
        }
        outputToFile(directory, "current_round_name", RoundUtils.getRoundName(nameNumberRound.getNumber()));
        outputToFile(directory, "current_episode_number", getSelectedEpisode(team, nameNumberRound) + "/" + (nameNumberRound.isFinal() ? "5" : "3"));
        outputToFile(directory, "current_round_total", selectedRound.getTotal());
        for (int player = 0; player < selectedRound.getScores().size(); player++) {
            Score score = selectedRound.getScores().get(player);
            for (int episode = 0; episode < score.getScore().length; episode++) {
                outputToFile(directory, "current_round_player_" + (player + 1) + "_episode_" + (episode + 1), score.getScore()[episode]);
            }
        }
    }

    private Round getSelectedRound(Team team) {
        String hardCodedRound = System.getProperty("round");
        if(hardCodedRound != null) {
            int round = Integer.parseInt(hardCodedRound) - 1;
            return team.getRounds().get(round);
        }
        return team.getLastPlayedRound();
    }

    private int getSelectedEpisode(Team team, Round round) {
        String hardCodedEpisode = System.getProperty("episode");
        if(hardCodedEpisode != null) {
            return Integer.parseInt(hardCodedEpisode);
        }

        return team.getCurrentEpisode(round);
    }

    public void outputBracket(List<TournamentRound> rounds) throws IOException {
        List<TournamentRound> normalRounds = rounds.subList(0, rounds.size() - 1);
        for (TournamentRound round : normalRounds) {
            outputRound(round);
        }

        outputWinners(rounds.get(rounds.size() - 1));
    }

    private void outputRound(TournamentRound round) throws IOException {
        File directory = new File(BASE, "tournament/" + round.getName().toLowerCase());
        outputToFile(directory, "name", round.getName());
        for (int idx = 0; idx < round.getTournamentMatchups().size(); idx++) {
            TournamentMatchup matchup = round.getTournamentMatchups().get(idx);
            outputToFile(directory, "matchup_" + idx + "_team_one_name", matchup.getTeamOne().getName().toUpperCase());
            outputToFile(directory, "matchup_" + idx + "_team_two_name", matchup.getTeamTwo().getName().toUpperCase());
            outputToFile(directory, "matchup_" + idx + "_team_one_score", matchup.getTeamOne().getScore());
            outputToFile(directory, "matchup_" + idx + "_team_two_score", matchup.getTeamTwo().getScore());
        }
    }

    private void outputWinners(TournamentRound winners) throws IOException {
        File directory = new File(BASE, "tournament/" + winners.getName().toLowerCase());
        outputToFile(directory, "first_place", winners.getTournamentMatchups().get(0).getTeamOne().getName());
        outputToFile(directory, "second_place", winners.getTournamentMatchups().get(0).getTeamTwo().getName());
        outputToFile(directory, "third_place", winners.getTournamentMatchups().get(1).getTeamOne().getName());
        outputToFile(directory, "fourth_place", winners.getTournamentMatchups().get(1).getTeamTwo().getName());
    }

    private static void outputToFile(File directory, String field, Object value) throws IOException {
        File file = new File(directory, field + ".txt");
        directory.mkdirs();
        if (!file.exists()) {
            file.createNewFile();
        }
        try (FileOutputStream writer = new FileOutputStream(file)) {
            String string = Objects.toString(value, "");
            if (string.equals("0")) {
                string = "";
            }
            string += " ";
            writer.write(string.getBytes());
            writer.flush();
        }
    }
}
