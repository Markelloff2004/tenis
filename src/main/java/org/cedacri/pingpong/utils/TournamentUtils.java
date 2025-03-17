package org.cedacri.pingpong.utils;

import com.vaadin.flow.component.UI;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Score;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
import org.cedacri.pingpong.service.TournamentService;

import java.util.*;

@Slf4j
public class TournamentUtils {

    private TournamentUtils() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static int calculateMaxPlayers(Tournament tournament) {
        int numPlayers = tournament.getPlayers().size();
        TournamentTypeEnum tournamentType = tournament.getTournamentType();

        log.debug("calculateMaxPlayers called with arg: {} {}", numPlayers, tournamentType);

        int minimalAmountOfPlayers = getMinimalPlayersRequired(tournament.getTournamentType());

        if (tournament.getPlayers().size() < minimalAmountOfPlayers) {
            return minimalAmountOfPlayers;
        }

        if (tournamentType == TournamentTypeEnum.ROBIN_ROUND) {
            return numPlayers;
        }

        int maxPlayers = 1;
        while (maxPlayers < numPlayers) {
            maxPlayers *= 2;
        }

        log.debug("Calculated max players: {}", maxPlayers);
        return maxPlayers;
    }

    public static int getMinimalPlayersRequired(TournamentTypeEnum tournamentType) {

        if (tournamentType == null) {
            throw new IllegalArgumentException("Tournament Type cannot be null");
        }

        return switch (tournamentType) {
            case OLYMPIC -> Constants.MINIMAL_AMOUNT_OF_PLAYER_FOR_OLYMPIC;
            case ROBIN_ROUND -> Constants.MINIMAL_AMOUNT_OF_PLAYER_FOR_ROBIN_ROUND;
        };
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

    public static void movePlayersWithoutOpponent(int round, Tournament tournament) {

        List<Match> thisRoundMatches = tournament.getMatches()
                .stream()
                .filter(m -> m.getRound() == round)
                .toList();

        try {
            for (Match match : thisRoundMatches) {
                if (match.getWinner() != null && round < TournamentUtils.calculateNumberOfRounds(tournament.getMaxPlayers())) {
                    if (match.getNextMatch().getTopPlayer() == null) {
                        match.getNextMatch().setTopPlayer(match.getWinner());
                    } else match.getNextMatch().setBottomPlayer(match.getWinner());
                }
            }
        } catch (Exception e) {
            log.error("Error updating winners after round: {}", round, e);
        }

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

        if (match.getTournament().getTournamentType() == TournamentTypeEnum.OLYMPIC) {
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

        int setsToWin = match.getTournament().getSetsToWin().getValue();
        int semifinalSetsToWin = match.getTournament().getSemifinalsSetsToWin().getValue();
        int finalSetsToWin = match.getTournament().getFinalsSetsToWin().getValue();
        int numOfRound = 0;
        if (match.getTournament().getTournamentType() == TournamentTypeEnum.ROBIN_ROUND) {
            numOfRound = match.getTournament().getSetsToWin().getValue();
        }
        if (match.getTournament().getTournamentType() == TournamentTypeEnum.OLYMPIC) {
            numOfRound = calculateNumberOfRounds(match.getTournament().getMaxPlayers());
        }

        int currRound = match.getRound();

        if (currRound == numOfRound) {
            return finalSetsToWin;
        } else if (currRound == numOfRound - 1) {
            return semifinalSetsToWin;
        } else {
            return setsToWin;
        }
    }

    private static void updateRatingAndSetTournamentWinnerRobinRound(Tournament tournament) {
        Map<Player, Integer> wonMatchesMap = new HashMap<>();
        Map<Player, Integer> goalsScoredMap = new HashMap<>();
        Map<Player, Integer> goalsLostMap = new HashMap<>();
        Map<Player, Integer> lostMatchesMap = new HashMap<>();

        for (Player player : tournament.getPlayers()) {
            wonMatchesMap.put(player, calculateNewWonMatches(player, tournament));
            lostMatchesMap.put(player, calculateNewLostMatches(player, tournament));
            goalsScoredMap.put(player, calculateNewGoalsScored(player, tournament));
            goalsLostMap.put(player, calculateNewGoalsLost(player, tournament));
        }

        for (Player player : tournament.getPlayers()) {
            int oldRating = player.getRating();
            int newWonMatches = wonMatchesMap.get(player);
            int newGoalsScored = goalsScoredMap.get(player);
            int newGoalsLost = goalsLostMap.get(player);
            int newLostMatches = lostMatchesMap.get(player);


            int newRating = oldRating + (5 * newWonMatches - 3 * newLostMatches) + (2 * newGoalsScored - newGoalsLost);

            player.setRating(newRating);
            player.setGoalsScored(player.getGoalsScored() + newGoalsScored);
            player.setGoalsLost(player.getGoalsLost() + newGoalsLost);
            player.setWonMatches(player.getWonMatches() + newWonMatches);
            player.setLostMatches(player.getLostMatches() + newLostMatches);
        }

        determineRobinRoundWinner(tournament, wonMatchesMap, goalsScoredMap, goalsLostMap);

    }

    private static void determineRobinRoundWinner(Tournament tournament, Map<Player, Integer> wonMatches, Map<Player, Integer> goalsScored, Map<Player, Integer> goalsLost) {

        Player bestPlayer = null;
        int maxWins = -1;
        int bestGoalDifference = Integer.MIN_VALUE;

        for (Player player : tournament.getPlayers()) {
            int wins = wonMatches.get(player);
            int goalDifference = goalsScored.get(player) - goalsLost.get(player);

            if (wins > maxWins || (wins == maxWins && goalDifference > bestGoalDifference)) {
                maxWins = wins;
                bestGoalDifference = goalDifference;
                bestPlayer = player;
            }

        }

        if (bestPlayer != null) {
            tournament.setWinner(bestPlayer);
        }

    }


    private static void updateRatingAndSetTournamentWinnerOlympic(Tournament tournament) {
        for (Player player : tournament.getPlayers()) {
            int oldRating = player.getRating();
            int newGoalsScored = calculateNewGoalsScored(player, tournament);
            int newGoalsLost = calculateNewGoalsLost(player, tournament);
            int newWonMatches = calculateNewWonMatches(player, tournament);
            int newLostMatches = calculateNewLostMatches(player, tournament);

            int newRating = oldRating + calculateNewRating(newWonMatches, newLostMatches, newGoalsScored, newGoalsLost);

            player.setRating(newRating);
            player.setGoalsScored(player.getGoalsScored() + newGoalsScored);
            player.setGoalsLost(player.getGoalsLost() + newGoalsLost);
            player.setWonMatches(player.getWonMatches() + newWonMatches);
            player.setLostMatches(player.getLostMatches() + newLostMatches);
        }

        tournament.getMatches()
                .stream().filter(match -> match.getRound() == 1).findFirst()
                .ifPresent(finalMatch -> tournament.setWinner(finalMatch.getWinner()));
    }

    public static int calculateNewRating(int newWonMatches, int newLostMatches, int newGoalsScored, int newGoalsLost) {
        return (5 * newWonMatches - 3 * newLostMatches) + (2 * newGoalsScored - newGoalsLost);
    }

    public static int calculateNewGoalsScored(Player player, Tournament tournament) {
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

    public static int calculateNewGoalsLost(Player player, Tournament tournament) {
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

    public static int calculateNewWonMatches(Player player, Tournament tournament) {
        return (int) tournament.getMatches().stream()
                .filter(match -> match.getWinner() != null && match.getWinner().equals(player))
                .count();
    }

    public static int calculateNewLostMatches(Player player, Tournament tournament) {
        return (int) tournament.getMatches().stream()
                .filter(match -> match.getWinner() != null
                        && !match.getWinner().equals(player)
                        && ((match.getTopPlayer() != null && match.getTopPlayer().equals(player)) ||
                        (match.getBottomPlayer() != null && match.getBottomPlayer().equals(player))))
                .count();
    }

    public static void checkAndUpdateTournamentWinner(Tournament tournament, TournamentService tournamentService) {
        boolean isOlympic = tournament.getTournamentType() == TournamentTypeEnum.OLYMPIC;
        boolean isRobinRound = tournament.getTournamentType() == TournamentTypeEnum.ROBIN_ROUND;

        if (isOlympic) {
            boolean finalMatchHasWinner = tournament.getMatches().stream()
                    .anyMatch(m -> m.getPosition() == 1 && m.getWinner() != null);

            if (finalMatchHasWinner) {
                finalizeTournament(tournament, tournamentService, TournamentTypeEnum.OLYMPIC);
            } else {
                NotificationManager.showErrorNotification(Constants.TOURNAMENT_WINNER_CANT_BE_DETERMINATED
                        + "Please assure that the Final match has a winner.");
            }
        } else if (isRobinRound) {
            boolean allMatchesHaveWinner = tournament.getMatches().stream()
                    .allMatch(m -> m.getWinner() != null);

            if (allMatchesHaveWinner) {
                finalizeTournament(tournament, tournamentService, TournamentTypeEnum.ROBIN_ROUND);
            } else {
                NotificationManager.showErrorNotification(Constants.TOURNAMENT_WINNER_CANT_BE_DETERMINATED
                        + "Please assure that all matches have a winner.");
            }
        }
    }

    private static void finalizeTournament(Tournament tournament, TournamentService tournamentService, TournamentTypeEnum type) {
        tournament.setTournamentStatus(TournamentStatusEnum.FINISHED);

        if (type == TournamentTypeEnum.OLYMPIC) {
            updateRatingAndSetTournamentWinnerOlympic(tournament);
        } else {
            updateRatingAndSetTournamentWinnerRobinRound(tournament);
        }

        tournamentService.saveTournament(tournament);

        UI.getCurrent().navigate("home");
        NotificationManager.showInfoNotification(Constants.TOURNAMENT_WINNER_HAS_BEEN_DETERMINATED
                + tournament.getWinner().getName() + " " + tournament.getWinner().getSurname());
    }


}
