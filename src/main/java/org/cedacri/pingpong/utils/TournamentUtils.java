package org.cedacri.pingpong.utils;

import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Tournament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class TournamentUtils {

    private static final Logger logger = LoggerFactory.getLogger(TournamentUtils.class);

    public static int calculateMaxPlayers(int num) {

        logger.debug("calculateMaxPlayers called with arg: {}", num);

        if (num < 8) {
            logger.error("Invalid number of players: {}", num);
            throw new IllegalArgumentException("Number of players must be at least 8.");
        }

        int maxPlayers = 1;
        while (maxPlayers < num) {
            maxPlayers *= 2;
        }

        logger.debug("Calculated max players: {}", maxPlayers);
        return maxPlayers;
    }

    public static int calculateNumberOfRounds(int num) {
        int rounds = 0;
        int players = 1;

        if(num < 8) {
            logger.error("Invalid number of rounds: {}", num);
            throw new IllegalArgumentException("Number of players must be at least 8.");
        }

        while(players < num) {
            players *= 2;
            rounds++;
        }
        logger.debug("Calculated number of rounds: {}", rounds);
        return rounds;
    }

    public static void updateWinnersAfterRound(int round, Tournament tournament) {

        List<Match> thisRoundMatches = tournament.getMatches()
                .stream()
                .filter(m -> m.getRound() == round)
                .toList();

        try{
            for(Match match : thisRoundMatches) {
                if(match.getWinner() != null && round < TournamentUtils.calculateNumberOfRounds(tournament.getMaxPlayers())){
                    if(match.getNextMatch().getTopPlayer() == null) {
                        match.getNextMatch().setTopPlayer(match.getWinner());
                    }
                    else match.getNextMatch().setBottomPlayer(match.getWinner());
                }
            }
        } catch (Exception e){
            logger.error("Error updating winners after round: {}", round, e);
        }

    }
}
