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

    private static final Logger log = LoggerFactory.getLogger(TournamentUtils.class);

    public static int calculateMaxPlayers(int num) {

        log.debug("calculateMaxPlayers called with arg: {}", num);

        if (num < 8) {
            log.error("Invalid number of players: {}", num);
            throw new IllegalArgumentException("Number of players must be at least 8.");
        }

        int maxPlayers = 1;
        while (maxPlayers < num) {
            maxPlayers *= 2;
        }

        log.debug("Calculated max players: {}", maxPlayers);
        return maxPlayers;
    }

    public static int calculateNumberOfRounds(int num) {
        int rounds = 0;
        int players = 1;

        if(num < 8) {
            log.error("Invalid number of rounds: {}", num);
            throw new IllegalArgumentException("Number of players must be at least 8.");
        }

        while(players < num) {
            players *= 2;
            rounds++;
        }
        log.debug("Calculated number of rounds: {}", rounds);
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
            log.error("Error updating winners after round: {}", round, e);
        }

    }

    public static void determinateWinner(Match match) {

        int topPlayerWins = 0;
        int bottomPlayerWins = 0;
        int minWinsRequired = getMinimalWinsPerMatch(match);

        for(Score score : match.getScore())
        {

            if(score.getTopPlayerScore() > score.getBottomPlayerScore() )
            {
                topPlayerWins++;
            }
            else if (score.getBottomPlayerScore() > score.getTopPlayerScore() )
            {
                bottomPlayerWins++;
            }
        }

        if(topPlayerWins < minWinsRequired && bottomPlayerWins < minWinsRequired)
        {
            NotificationManager.showErrorNotification("Cannot be determinate a winner for Match:" + match.getId() + ". Not enough sets were played" );
            return;
        }


        Player winner = null;

        if(topPlayerWins > bottomPlayerWins)
        {
            winner = match.getTopPlayer();
        } else if (bottomPlayerWins > topPlayerWins)
        {
            winner = match.getBottomPlayer();
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

    private static int getMinimalWinsPerMatch(Match match) {

        return getNumsOfSetsPerMatch(match)/2+1;

    }

    public static  int getNumsOfSetsPerMatch(Match match) {

        int setsToWin = match.getTournament().getSetsToWin().getValue();
        int semifinalSetsToWin = match.getTournament().getSemifinalsSetsToWin().getValue();
        int finalSetsToWin = match.getTournament().getFinalsSetsToWin().getValue();
        int numOfRound = calculateNumberOfRounds(match.getTournament().getMaxPlayers());
        int currRound = match.getRound();

        if (currRound == numOfRound){
            return finalSetsToWin;
        } else if (currRound == numOfRound-1){
            return semifinalSetsToWin;
        } else {
            return setsToWin;
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

    public static int determineMaxPlayers(Set<Player> players) {
        int playerCount = players.size();
        return (playerCount < 8) ? 8 : calculateMaxPlayers(playerCount);
    }
}
