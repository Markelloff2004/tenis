package org.cedacri.pingpong.utils;

import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Score;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.service.MatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Set;


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

    public static void determinateWinner(Match match) {

        int topPlayerWined = 0;
        int bottomPlayerWined = 0;
        int minScoresCounts = getMinimalNumsOfScores(match);

        for(Score score : match.getScore())
        {
            if(score.getTopPlayerScore() > score.getBottomPlayerScore()
                    && score.getTopPlayerScore() >= Constraints.MINIMAL_POINTS_IN_SET
                    && score.getTopPlayerScore() - score.getBottomPlayerScore() >= Constraints.MINIMAL_DIFFERENCE_OF_POINTS_IN_SET
            )
            {
                topPlayerWined++;
            }
            else if (score.getBottomPlayerScore() > score.getTopPlayerScore()
                    && score.getBottomPlayerScore() >= Constraints.MINIMAL_POINTS_IN_SET
                    && score.getBottomPlayerScore() - score.getTopPlayerScore() >= Constraints.MINIMAL_DIFFERENCE_OF_POINTS_IN_SET
            )
            {
                bottomPlayerWined++;
            }
            else {
                logger.warn("This Score cannot be used for determinate a winner {}", score);
            }
        }

        Player winner = null;

        if(topPlayerWined > bottomPlayerWined
                && topPlayerWined >= minScoresCounts)
        {
            winner = match.getTopPlayer();
        } else if (bottomPlayerWined > topPlayerWined
                && bottomPlayerWined >= minScoresCounts) {
            winner = match.getBottomPlayer();
        } else {
            NotificationManager.showInfoNotification("Cannot be determinate a winner for Match:" + match.getId());
        }

        match.setWinner(winner);
        moveWinner(match);
    }

    private static void moveWinner(Match match) {

        if(match.getRound() < TournamentUtils.calculateNumberOfRounds(match.getTournament().getMaxPlayers()))
        {
            if(match.getPosition()%2==0){
                match.getNextMatch().setBottomPlayer(match.getWinner());
            } else
            {
                match.getNextMatch().setTopPlayer(match.getWinner());
            }
        } else {
            //fine tournament
            fineTournament(match.getTournament());
        }
    }

    private static int getMinimalNumsOfScores(Match match) {

        int setsToWin = match.getTournament().getSetsToWin().getValue();
        int semifinalSetsToWin = match.getTournament().getSemifinalsSetsToWin().getValue();
        int finalSetsToWin = match.getTournament().getFinalsSetsToWin().getValue();
        int numOfRound = calculateNumberOfRounds(match.getTournament().getMaxPlayers());
        int currRound = match.getRound();

        if (currRound == numOfRound){
            return finalSetsToWin/2+1;
        } else if (currRound == numOfRound-1){
            return semifinalSetsToWin/2+1;
        } else {
            return setsToWin/2+1;
        }
    }

    private static void fineTournament(Tournament tournament)
    {
        tournament.setTournamentStatus(TournamentStatusEnum.FINISHED);
        updateRating(tournament);
    }

    private static void updateRating(Tournament tournament) {
        for (Player player : tournament.getPlayers())
        {

            List<Match> playedMatches = tournament.getMatches().stream()
                    .filter(match ->
                            (Objects.nonNull(match.getBottomPlayer())
                                                && match.getBottomPlayer().equals(player))
                            || (Objects.nonNull(match.getTopPlayer())
                                    && match.getTopPlayer().equals(player))
                    ).toList();

            int oldRating = player.getRating();
            int newGoalsScored = 0;
            int newGoalsLost = 0;
            int newWonMatches = 0;
            int newLostMatches = 0;

            for (Match match : playedMatches)
            {
                boolean isTopPlayer = match.getTopPlayer().equals(player);

                for (Score score : match.getScore())
                {
                    newGoalsScored += isTopPlayer
                            ? score.getTopPlayerScore()
                            : score.getBottomPlayerScore() ;

                    newGoalsLost += isTopPlayer
                            ? score.getBottomPlayerScore()
                            : score.getTopPlayerScore() ;
                }

                if (match.getWinner().equals(player))
                {
                    newWonMatches++;
                } else
                {
                    newLostMatches++;
                }
            }

            int newRating = oldRating + (5 * newWonMatches - 3 * newLostMatches) + (2 * newGoalsScored - newGoalsLost) ;

            player.setRating(newRating);
            player.setGoalsScored(player.getGoalsScored() + newGoalsScored);
            player.setGoalsLost(player.getGoalsLost() + newGoalsLost);
            player.setWonMatches(player.getWonMatches() + newWonMatches);
            player.setLostMatches(player.getLostMatches() + newLostMatches);
        }
    }
}
