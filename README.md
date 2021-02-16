# Fallapalooza Formatter
This project is used to pull data from the Fallapalooza spreadsheet into text files that can be used with a streaming
software, such as OBS. 

## Quickstart
1. Insert your `credentials.json` file from Google into the `src/resources` folder.
2. Run the `jarify` gradle job to construct an uber-jar containing all dependencies and resource files
3. Run the `fallapalooza.jar` jar in the `build/libs` directory (`java -jar fallapalooza.jar`)
4. The first time it runs, the program will prompt you for credentials via a link in your command prompt. This is
saved for subsequent uses, but expires after a month.

### System properties
* `sheet` - Specify a custom sheet. Note that it must be in the exact same format as the original.
* `round` - Force a specific round to be accepted as the current one. If omitted, the program will do
its best to figure out the current round. The current round will be determined by figuring out the first
in-progress or incomplete round. If this round is empty, the previous round will be selected.
* `clean` - Completely delete all output files before running this program.

## Output
All text files will be placed in the `output` directory, created in the same directory as the program.

Note that the program will not output 0 for any files - it will display as blank. All output has a space appended.

### matches/
Contains a directory for each team in the tournament, in the format `team_{seed}`. Inside each directory are the
following files:
* `name` - Team name
* `player_#_name` - The name of a player
* `player_#_name_pronouns` - The name of a player, plus their pronouns
* `round_#_player_#_episode_#` - The score of a player, for a particular round and episode
* `round_#_total` - The total score of both players for a particular round
* `round_#_episode` - The episode of that particular round that is being played
* `current_round_episode` - The current episode being played, in the format `<number>/3` or `<number>/5`. Note:
the round is not considered to have advanced unless both players have a score.
* `current_round_name` - The current round being played (Round 1, Round 2, Quarterfinals, Semifinals, Finals)
* `current_round_player_#_episode_#` - The score of a player, for a particular episode of the current round
* `current_round_total` - The total score of both players for the current round

### display/
Identical to matches, but instead contains a directory for each team with a value in the `display` field, in the format
`team_{display_number}`. All files are identical to the `matches/` directory.

### tournament/
Displays a bracket view of the tournament. Unlike the previous version, the bracket is pulled from the spreadsheet, 
rather than calculated by the program. The subdirectories are:
* `round_1`
* `round_2`
* `quarterfinals`
* `semifinals`
* `finals`
* `first_place`,`second_place`,`third_place`,`fourth_place`

In all except the last directory, the output is as follows:
* `matchup_#_team_one_name` - The name of the first team in a matchup
* `matchup_#_team_two_name` - The name of the second team in a matchup
* `matchup_#_team_one_score` - The total score of the first team in a matchup
* `matchup_#_team_two_score` - The total score of the second team in a matchup

In the `winners` directory, the output is as follows:
* `first_place` - The team name of the first-place winner
* `second_place` - The team name of second-place winner
* `third_place` - The team name of the third-place winner
* `fourth_place` - The team name of the fourth-place winner


