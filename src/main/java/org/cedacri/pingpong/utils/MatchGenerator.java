package org.cedacri.pingpong.utils;

import lombok.Getter;
import lombok.Setter;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.enums.SetTypesEnum;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
import org.cedacri.pingpong.service.MatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
public class MatchGenerator {

    private SetTypesEnum simpleRoundSets;
    private SetTypesEnum semifinalsRoundSets;
    private SetTypesEnum finalsRoundSets;
    private TournamentTypeEnum tournamentType;

    private static final Logger logger = LoggerFactory.getLogger(MatchService.class);
    private final PlayerDistributer playerDistributer;

    public MatchGenerator(SetTypesEnum simpleRoundSets, SetTypesEnum semifinalsRoundSets, SetTypesEnum finalsRoundSets, TournamentTypeEnum tournamentType, PlayerDistributer playerDistributer) {
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

            // Sort in descending order by rating
            sortedPlayers.sort(Comparator.comparingInt(Player::getRating).reversed());

            return generateOlympicTournament(sortedPlayers, tournament);
        }
        logger.error("Tournament type {} not supported", tournamentType);
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

        // Determine the maximum number of players (nearest power of 2)
        int maxPlayers = 1;
        while (maxPlayers < numPlayers) {
            maxPlayers *= 2;
        }
        logger.info("Max players for the tournament: {}", maxPlayers);

        // Generate matches for each round
        int currentPlayers = maxPlayers;
        int round = 1;
        while (currentPlayers > 1) {
            generateRoundMatches(allMatches, tournament, currentPlayers / 2, round);
            currentPlayers /= 2;
            round++;
        }

        // Distribute players into the first round using PlayerDistributer
        distributePlayersToFirstRound(allMatches, sortedPlayers, maxPlayers);

        return allMatches;
    }

    /**
     * Generates empty matches for a single round.
     *
     * @param allMatches  - all matches of the tournament
     * @param tournament  - tournament being processed
     * @param numMatches  - number of matches in the current round
     */
    private void generateRoundMatches(List<Match> allMatches, Tournament tournament, int numMatches, int round) {
        int groupPosition = 1;
        logger.info("Generating {} matches for round: {}", numMatches, round);

        for (int i = 0; i < numMatches; i++) {
            Match match = createMatch(tournament, round, groupPosition);
            allMatches.add(match);
            if ((i + 1) % 2 == 0) {
                groupPosition++;
            }
        }
    }

    /**
     * Distributes players in the first round, filling the matches with players.
     *
     * @param allMatches    - all tournament matches
     * @param sortedPlayers - sorted players by rating
     * @param maxPlayers    - maximum number of players (nearest power of 2)
     */
    private void distributePlayersToFirstRound(List<Match> allMatches, List<Player> sortedPlayers, int maxPlayers) {

        List<Player[]> pairs = playerDistributer.distributePlayers(sortedPlayers, maxPlayers);

        List<Match> firstRoundMatches = allMatches.stream()
                .filter(m -> m.getRound() == 1)
                .toList();

        for (int i = 0; i < firstRoundMatches.size(); i++) {
            Match match = firstRoundMatches.get(i);
            Player[] pair = pairs.get(i);

            match.setTopPlayer(pair[0]);
            match.setBottomPlayer(pair[1]);

            logger.info("Match {}: TopPlayer={}, BottomPlayer={}",
                    "ID: " + match.getId() + "|Pos: " + match.getPosition() + "|Round: " + match.getRound(),
                    pair[0] != null ? pair[0].getName() : "null",
                    pair[1] != null ? pair[1].getName() : "null");

            // Automatically set winner if only one player is present in the match
            if (pair[0] != null && pair[1] == null) {
                match.setWinner(pair[0]);
            } else if (pair[0] == null && pair[1] != null) {
                match.setWinner(pair[1]);
            }
        }
    }

    private Match createMatch(Tournament tournament, int round, int position) {
        Match match = new Match();
        match.setTopPlayer(null);
        match.setBottomPlayer(null);
        match.setTournament(tournament);
        match.setRound(round);
        match.setPosition(position);
        match.setWinner(null);
        return match;
    }
}
