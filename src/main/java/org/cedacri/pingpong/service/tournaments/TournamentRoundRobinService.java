package org.cedacri.pingpong.service.tournaments;

import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.exception.tournament.UnexpectedTournamentException;
import org.cedacri.pingpong.model.enums.TournamentStatusEnum;
import org.cedacri.pingpong.model.enums.TournamentTypeEnum;
import org.cedacri.pingpong.model.match.Match;
import org.cedacri.pingpong.model.player.Player;
import org.cedacri.pingpong.model.tournament.BaseTournament;
import org.cedacri.pingpong.model.tournament.TournamentRoundRobin;
import org.cedacri.pingpong.repository.BaseTournamentRepository;
import org.cedacri.pingpong.utils.Constants;
import org.cedacri.pingpong.utils.TournamentUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service("tournamentRoundRobinService")
public class TournamentRoundRobinService extends BaseTournamentService<TournamentRoundRobin> implements ITournamentOperations<TournamentRoundRobin> {

    public TournamentRoundRobinService(BaseTournamentRepository baseTournamentRepository) {
        super(baseTournamentRepository);
    }

//    @Override
    public List<TournamentRoundRobin> findAllTournamentsOfType() {
        log.info("Fetching all Round Robin tournaments");

        List<TournamentRoundRobin> tournamentRoundRobinList = getTournamentRepository().findAllByTournamentType(TournamentTypeEnum.ROUND_ROBIN).stream().filter(TournamentRoundRobin.class::isInstance).map(TournamentRoundRobin.class::cast).sorted(Comparator.comparing(BaseTournament::getStartedAt, Comparator.nullsLast(Comparator.naturalOrder()))).toList();

        if (tournamentRoundRobinList.isEmpty()) {
            log.warn("No Olympic tournaments found");
            return Collections.emptyList();
        }

        return tournamentRoundRobinList;
    }

    @Override
    public void validateTournament(TournamentRoundRobin tournamentRobinRound) {
        if (tournamentRobinRound == null) {
            throw new IllegalArgumentException("Tournament cannot be null");
        }

        if (tournamentRobinRound.getPlayers().size() < Constants.MINIMAL_AMOUNT_OF_PLAYER_FOR_ROBIN_ROUND) {
            throw new IllegalArgumentException("Tournament must have at least " + Constants.MINIMAL_AMOUNT_OF_PLAYER_FOR_ROBIN_ROUND + " players");
        }

        if (tournamentRobinRound.getTournamentStatus() != TournamentStatusEnum.PENDING) {
            throw new IllegalArgumentException("Tournament must be in a valid state to start (pending)");
        }

        log.debug("Tournament {} is valid for generation", tournamentRobinRound.getTournamentName());
    }

    @SneakyThrows
    @Transactional
    @Override
    public TournamentRoundRobin startTournament(TournamentRoundRobin tournamentRobinRound) {

        log.info("Starting a Robin Round Tournament: {}", tournamentRobinRound.getTournamentName());

        try {
            validateTournament(tournamentRobinRound);
            generateMatches(tournamentRobinRound);

            tournamentRobinRound.setTournamentStatus(TournamentStatusEnum.ONGOING);
            tournamentRobinRound.setStartedAt(java.time.LocalDate.now());

            TournamentRoundRobin savedTournament = createTournament(tournamentRobinRound);

            log.info("Robin Round Tournament started successfully.");
            return savedTournament;

        } catch (IllegalArgumentException e) {
            log.error("Error starting Robin Round Tournament: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error starting Robin Round Tournament: {}", e.getMessage(), e);
            throw new UnexpectedTournamentException("Failed to start Robin Round Tournament", e);
        }
    }

    @Override
    public void generateMatches(TournamentRoundRobin tournamentRobinRound) {
        //TODO: Change this to a more efficient algorithm that will include grouping players in groups of 4 or 8 [watch in Teams condition]
        log.debug("Generating robin round matches for tournament: {} {}", tournamentRobinRound.getId(), tournamentRobinRound.getTournamentName());

        List<Player> players = new ArrayList<>(tournamentRobinRound.getPlayers());
        List<Match> allMatches = new ArrayList<>();

        // For Robin Round, each player plays against every other player
        for (int i = 0; i < players.size(); i++) {
            for (int j = i + 1; j < players.size(); j++) {
                Player player1 = players.get(i);
                Player player2 = players.get(j);

                Match match = Match.builder().topPlayer(player1).bottomPlayer(player2).tournament(tournamentRobinRound).round(1) // All matches are in a single round for robin
                        .position(allMatches.size() + 1) // Sequential match position
                        .build();

                allMatches.add(match);
            }
        }

        tournamentRobinRound.setMatches(allMatches);
        log.debug("Generated {} matches for Robin Round tournament", allMatches.size());
    }

    @Override
    public Player determineTournamentWinner(TournamentRoundRobin tournamentRoundRobin) {
        log.debug("Determining winner for tournament: {}", tournamentRoundRobin.getId());

        if (allMatchesHasBeenPlayed(tournamentRoundRobin)) {
            log.warn("Tournament {} is not ready to finish. All matches must be played first.", tournamentRoundRobin.getId());
            return null;
        }

        List<Player> rankedPlayers = calculatePlayerRankings(tournamentRoundRobin);

        if (rankedPlayers.isEmpty()) {
            log.warn("No players found in the tournament: {}", tournamentRoundRobin.getId());
            return null;
        }

        Player winner = rankedPlayers.get(0);
        tournamentRoundRobin.setWinner(winner);
        log.info("Winner of the tournament {} is: {}", tournamentRoundRobin.getId(), winner.getName());

        return winner;
    }

    @Override
    public void endTournament(TournamentRoundRobin tournament) {
        log.info("Ending tournament: {}", tournament.getTournamentName());

        if (tournament.getTournamentStatus() != TournamentStatusEnum.ONGOING) {
            log.warn("Tournament {} is not in an ongoing state, cannot end it.", tournament.getId());
            throw new IllegalStateException("Cannot end tournament: not in an ongoing state");
        }
        if (allMatchesHasBeenPlayed(tournament)) {

            // Logic to finalize the tournament, e.g., updating status, notifying players, etc.
            tournament.setTournamentStatus(TournamentStatusEnum.FINISHED);

            Player winner = determineTournamentWinner(tournament);

            if (winner != null) {
                tournament.setWinner(winner);
                tournament.setTournamentStatus(TournamentStatusEnum.FINISHED);
                log.info("Tournament {} ended successfully with winner: {}", tournament.getId(), winner.getName());
            } else {
                log.warn("No winner determined for tournament: {}", tournament.getId());
                throw new IllegalStateException("Cannot end tournament: no winner found");
            }

            getTournamentRepository().save(tournament);
        } else {
            log.warn("Cannot end tournament {}: not all matches have been played", tournament.getTournamentName());
            throw new IllegalStateException("Cannot end tournament: not all matches have been played");
        }

        log.info("Tournament {} ended successfully with winner: {}", tournament.getTournamentName(), tournament.getWinner().getName());
    }

    public List<Player> calculatePlayerRankings(TournamentRoundRobin tournamentRobinRound) {
        log.debug("Calculating player rankings for tournament: {}", tournamentRobinRound.getId());

        Set<Player> players = tournamentRobinRound.getPlayers();

        // Create a map with player rankings
        Map<Player, Integer> playerScores = new HashMap<>();

        for (Player player : players) {
            int score = TournamentUtils.calculateNewRating(TournamentUtils.calculateNewWonMatches(player, tournamentRobinRound), TournamentUtils.calculateNewLostMatches(player, tournamentRobinRound), TournamentUtils.calculateNewGoalsScored(player, tournamentRobinRound), TournamentUtils.calculateNewGoalsLost(player, tournamentRobinRound));

            playerScores.put(player, score);
        }

        // Sort players by score in descending order
        return playerScores.entrySet().stream().sorted(Map.Entry.<Player, Integer>comparingByValue().reversed()).map(Map.Entry::getKey).toList();
    }

    @Override
    public boolean allMatchesHasBeenPlayed(TournamentRoundRobin tournament) {
        return tournament.getMatches().stream().noneMatch(m -> m.getWinner() == null);
    }
}
