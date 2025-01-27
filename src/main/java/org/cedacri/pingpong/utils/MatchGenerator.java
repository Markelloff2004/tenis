package org.cedacri.pingpong.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.enums.SetTypesEnum;
import org.cedacri.pingpong.enums.TournamentTypeEnum;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Getter
@Setter
public class MatchGenerator {

    private SetTypesEnum simpleRoundSets;
    private SetTypesEnum semifinalsRoundSets;
    private SetTypesEnum finalsRoundSets;
    private TournamentTypeEnum tournamentType;

    private final PlayerDistributer playerDistributer;

    public MatchGenerator(SetTypesEnum simpleRoundSets, SetTypesEnum semifinalsRoundSets,
                          SetTypesEnum finalsRoundSets, TournamentTypeEnum tournamentType,
                          PlayerDistributer playerDistributer) {
        this.simpleRoundSets = simpleRoundSets;
        this.semifinalsRoundSets = semifinalsRoundSets;
        this.finalsRoundSets = finalsRoundSets;
        this.tournamentType = tournamentType;
        this.playerDistributer = playerDistributer;
    }

    /**
     * Generates matches for the tournament based on the selected type.
     *
     * @param tournament - tournament for which matches should be generated
     * @return - list of generated matches
     */
    public List<Match> generateMatches(Tournament tournament) {
        if (tournamentType == TournamentTypeEnum.OLIMPIC) {
            List<Player> sortedPlayers = new ArrayList<>(tournament.getPlayers());

            // Sort players by rating in descending order
            sortedPlayers.sort(Comparator.comparingInt(Player::getRating).reversed());

            return generateOlympicTournament(sortedPlayers, tournament);
        }
        log.error("Tournament type {} not supported", tournamentType);
        throw new UnsupportedOperationException("Tournament type " + tournamentType + " not supported yet");
    }

    /**
     * Generates an Olympic-style tournament structure with proper match distribution.
     *
     * @param sortedPlayers - sorted players by rating
     * @param tournament    - tournament being processed
     * @return - list of generated matches
     */
    private List<Match> generateOlympicTournament(List<Player> sortedPlayers, Tournament tournament) {
        List<Match> allMatches = new ArrayList<>();
        int numPlayers = sortedPlayers.size();
        int maxPlayers = calculateMaxPlayers(numPlayers);

        log.info("Max players for the tournament: {}", maxPlayers);

        // Generate empty matches for each round
        int currentPlayers = maxPlayers;
        int round = 1;
        while (currentPlayers > 1) {
            generateMatchesForRound(allMatches, tournament, currentPlayers / 2, round);
            currentPlayers /= 2;
            round++;
        }


        List<Match> firstRoundMatches = distributePlayersInFirstRound(allMatches, tournament);
        allMatches.removeIf(m -> m.getRound() == 1);
        allMatches.addAll(firstRoundMatches);
        allMatches.sort(Comparator.comparingInt(Match::getRound).reversed());

        propagateWinnersToNextRounds(allMatches);

        return allMatches;
    }

    private void propagateWinnersToNextRounds(List<Match> allMatches) {

        List<Match> matchesWithWinners = allMatches.stream()
            .filter(match -> match.getWinner() != null)
            .toList();

        for (Match match : matchesWithWinners) {
        Match nextMatch = match.getNextMatch(); // Получаем следующий матч
        if (nextMatch != null) {
            // if current match winner != null, setting him as top or bottom player of next match
            if (nextMatch.getTopPlayer() == null) {
                nextMatch.setTopPlayer(match.getWinner());
            } else if (nextMatch.getBottomPlayer() == null) {
                nextMatch.setBottomPlayer(match.getWinner());
            }

            log.info("Match {} (Round {}) winner {} assigned to Next Match {} (Round {})",
                    match.getId(),
                    match.getRound(),
                    match.getWinner().getName(),
                    nextMatch.getId(),
                    nextMatch.getRound());
        }
    }

    }

    private List<Match> distributePlayersInFirstRound(List<Match> allMatches, Tournament tournament){

        List<Match> filledMatches = new ArrayList<>();

        List<Player[]> pairs = playerDistributer.distributePlayers(
                tournament.getPlayers().stream().toList(),
                tournament.getMaxPlayers());

        //setting players in first round
        for(int i = 0; i < pairs.size(); i++) {
            Player[] pair = pairs.get(i);

            Match currentMatch = allMatches.get(i);
            currentMatch.setTopPlayer(pair[0]);
            currentMatch.setBottomPlayer(pair[1]);

            log.info("Match {}: TopPlayer={}, BottomPlayer={}",
                    currentMatch.getId(),
                    pair[0] != null ? pair[0].getName() : "null",
                    pair[1] != null ? pair[1].getName() : "null");

            // Automatically set winner if only one player is present in the match
            if (pair[0] != null && pair[1] == null) {
                currentMatch.setWinner(pair[0]);
            } else if (pair[0] == null && pair[1] != null) {
                currentMatch.setWinner(pair[1]);
            }

            filledMatches.add(currentMatch);
        }

        return filledMatches;
    }

    /**
     * Generates matches for a single round.
     *
     * @param allMatches  - all matches of the tournament
     * @param tournament  - tournament being processed
     * @param numMatches  - number of matches in the current round
     * @param round       - current round number
     */
    private void generateMatchesForRound(List<Match> allMatches, Tournament tournament, int numMatches, int round) {
        log.info("Generating {} matches for round: {}", numMatches, round);

        List<Match> currentRoundMatches = IntStream.range(0, numMatches)
                .mapToObj(i -> createMatch(tournament, round, (i / 2) + 1))
                .toList();

        allMatches.addAll(currentRoundMatches);
    }

    /**
     * Calculates the maximum number of players (nearest power of 2).
     *
     * @param numPlayers - number of players
     * @return nearest power of 2 greater than or equal to numPlayers
     */
    private int calculateMaxPlayers(int numPlayers) {
        int maxPlayers = 1;
        while (maxPlayers < numPlayers) {
            maxPlayers *= 2;
        }
        return maxPlayers;
    }

    /**
     * Creates an empty match.
     *
     * @param tournament - tournament for the match
     * @param round      - round number
     * @param position   - position of the match in the round
     * @return an empty match
     */
    private Match createMatch(Tournament tournament, int round, int position) {
        Match match = new Match();
        match.setTournament(tournament);
        match.setRound(round);
        match.setPosition(position);
        match.setTopPlayer(null);
        match.setBottomPlayer(null);
        match.setWinner(null);
        return match;
    }
}
