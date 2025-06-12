package org.cedacri.pingpong.utils;

import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.model.tournament.BaseTournament;
import org.cedacri.pingpong.model.match.Match;
import org.cedacri.pingpong.model.player.Player;
import org.cedacri.pingpong.model.match.Score;
import org.cedacri.pingpong.model.tournament.TournamentOlympic;

@Slf4j
public class TournamentUtils {

    private TournamentUtils() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }


    public static int calculateNumberOfRounds(int num) {
        int rounds = 0;
        int players = 1;

        if (num < Constants.MINIMAL_AMOUNT_OF_PLAYER_FOR_OLYMPIC) {
            log.error("Invalid number of rounds: {}", num);
            throw new IllegalArgumentException("Number of players must be at least 8.");
        }

        while (players < num) {
            players *= 2;
            rounds++;
        }
        log.debug("Calculated number of rounds: {}", rounds);
        return rounds;
    }

    public static void determinateWinnerFromScore(Match match) {

        int topPlayerWins = 0;
        int bottomPlayerWins = 0;
        int minWinsRequired = getMinimalWinsPerMatch(match);

        for (Score score : match.getScore()) {

            if (score.getTopPlayerScore() > score.getBottomPlayerScore()) {
                topPlayerWins++;
            } else if (score.getBottomPlayerScore() > score.getTopPlayerScore()) {
                bottomPlayerWins++;
            }
        }

        if (topPlayerWins < minWinsRequired && bottomPlayerWins < minWinsRequired) {
            NotificationManager.showErrorNotification("Cannot be determinate a winner for Match:" + match.getId() + ". Not enough sets were played");
            return;
        }

        Player winner = null;

        if (topPlayerWins > bottomPlayerWins) {
            winner = match.getTopPlayer();
        } else if (bottomPlayerWins > topPlayerWins) {
            winner = match.getBottomPlayer();
        }


        match.setWinner(winner);

        if (match.getTournament() instanceof TournamentOlympic) {
            moveWinner(match);
        }
    }

    private static void moveWinner(Match match) {

        if (match.getRound() < TournamentUtils.calculateNumberOfRounds(match.getTournament().getMaxPlayers())) {
            if (match.getPosition() % 2 == 0) {
                match.getNextMatch().setBottomPlayer(match.getWinner());
            } else {
                match.getNextMatch().setTopPlayer(match.getWinner());
            }
        }
    }

    private static int getMinimalWinsPerMatch(Match match) {

        return getNumsOfSetsPerMatch(match) / 2 + 1;

    }

    public static int getNumsOfSetsPerMatch(Match match) {

        return match.getTournament().getSetsToWin().getValue();
    }


    public static int calculateNewRating(int newWonMatches, int newLostMatches, int newGoalsScored, int newGoalsLost) {
        return (5 * newWonMatches - 3 * newLostMatches) + (2 * newGoalsScored - newGoalsLost);
    }

    public static int calculateNewGoalsScored(Player player, BaseTournament tournament) {
        return tournament.getMatches().stream()
                .filter(match -> (match.getTopPlayer() != null && match.getTopPlayer().equals(player)) ||
                        (match.getBottomPlayer() != null && match.getBottomPlayer().equals(player)))
                .flatMapToInt(match -> match.getScore().stream()
                        .mapToInt(score -> (match.getTopPlayer() != null && match.getTopPlayer().equals(player))
                                ? score.getTopPlayerScore()
                                : score.getBottomPlayerScore())
                )
                .sum();
    }

    public static int calculateNewGoalsLost(Player player, BaseTournament tournament) {
        return tournament.getMatches().stream()
                .filter(match -> (match.getTopPlayer() != null && match.getTopPlayer().equals(player)) ||
                        (match.getBottomPlayer() != null && match.getBottomPlayer().equals(player)))
                .flatMapToInt(match -> match.getScore().stream()
                        .mapToInt(score -> (match.getTopPlayer() != null && match.getTopPlayer().equals(player))
                                ? score.getBottomPlayerScore()
                                : score.getTopPlayerScore())
                )
                .sum();
    }

    public static int calculateNewWonMatches(Player player, BaseTournament tournament) {
        return (int) tournament.getMatches().stream()
                .filter(match -> match.getWinner() != null && match.getWinner().equals(player))
                .count();
    }

    public static int calculateNewLostMatches(Player player, BaseTournament tournament) {
        return (int) tournament.getMatches().stream()
                .filter(match -> match.getWinner() != null
                        && !match.getWinner().equals(player)
                        && ((match.getTopPlayer() != null && match.getTopPlayer().equals(player)) ||
                        (match.getBottomPlayer() != null && match.getBottomPlayer().equals(player))))
                .count();
    }
}