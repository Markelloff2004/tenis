package org.cedacri.pingpong.service.tournament_round_robin;

import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.*;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
import org.cedacri.pingpong.exception.tournament.UnexpectedTournamentException;
import org.cedacri.pingpong.repository.BaseTournamentRepository;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.Constants;
import org.cedacri.pingpong.utils.TournamentUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TournamentRoundRobinService extends TournamentService implements ITournamentRoundRobinServiceInterface {

    public TournamentRoundRobinService(BaseTournamentRepository tournamentRepository) {
        super(tournamentRepository);
    }

    public List<TournamentOlympic> findAllTournamentsOlympic() {
        List<BaseTournament> baseTournamentList = super.findAllTournamentsByType(TournamentTypeEnum.OLYMPIC);

        return baseTournamentList
                .stream()
                .filter(TournamentOlympic.class::isInstance)
                .map(TournamentOlympic.class::cast)
                .toList();
    }

    @Override
    public TournamentRoundRobin findTournamentById(Long id) {
        validateTournamentId(id);

        if (super.findTournamentById(id) instanceof TournamentRoundRobin tournamentRoundRobin) {
            return tournamentRoundRobin;
        } else {
            throw new IllegalArgumentException("Tournament with ID " + id + " is not a Round Robin tournament");
        }
    }

    @SneakyThrows
    @Transactional
    @Override
    public BaseTournament startTournament(BaseTournament baseTournament) {
        if (!(baseTournament instanceof TournamentRoundRobin tournamentRobinRound)) {
            throw new IllegalArgumentException("Tournament must be a Robin Round tournament");
        }

        log.info("Starting a Robin Round Tournament: {}", tournamentRobinRound.getTournamentName());

        try {
            validateTournamentForGeneration(tournamentRobinRound);
            generateRobinRoundMatches(tournamentRobinRound);

            tournamentRobinRound.setTournamentStatus(TournamentStatusEnum.ONGOING);
            tournamentRobinRound.setStartedAt(java.time.LocalDate.now());

            BaseTournament savedTournament = saveTournament(tournamentRobinRound);

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
    public void validateTournamentForGeneration(TournamentRoundRobin tournamentRobinRound) {
        if (tournamentRobinRound == null) {
            throw new IllegalArgumentException("Tournament cannot be null");
        }

        if (tournamentRobinRound.getPlayers().size() < Constants.MINIMAL_AMOUNT_OF_PLAYER_FOR_ROBIN_ROUND) {
            throw new IllegalArgumentException("Tournament must have at least " +
                    Constants.MINIMAL_AMOUNT_OF_PLAYER_FOR_ROBIN_ROUND + " players");
        }

        if (tournamentRobinRound.getTournamentStatus() == TournamentStatusEnum.ONGOING ||
                tournamentRobinRound.getTournamentStatus() == TournamentStatusEnum.FINISHED) {
            throw new IllegalArgumentException("Tournament must be in a valid state to start (not ongoing or finished)");
        }

        log.debug("Tournament {} is valid for generation", tournamentRobinRound.getTournamentName());
    }

    @Override
    public void generateRobinRoundMatches(TournamentRoundRobin tournamentRobinRound) {
        log.debug("Generating robin round matches for tournament: {} {}",
                tournamentRobinRound.getId(), tournamentRobinRound.getTournamentName());

        List<Player> players = new ArrayList<>(tournamentRobinRound.getPlayers());
        List<Match> allMatches = new ArrayList<>();

        // For Robin Round, each player plays against every other player
        for (int i = 0; i < players.size(); i++) {
            for (int j = i + 1; j < players.size(); j++) {
                Player player1 = players.get(i);
                Player player2 = players.get(j);

                Match match = Match.builder()
                        .topPlayer(player1)
                        .bottomPlayer(player2)
                        .tournament(tournamentRobinRound)
                        .round(1) // All matches are in a single round for robin
                        .position(allMatches.size() + 1) // Sequential match position
                        .build();

                allMatches.add(match);
            }
        }

        tournamentRobinRound.setMatches(allMatches);
        log.debug("Generated {} matches for Robin Round tournament", allMatches.size());
    }

    @Override
    public List<Player> calculatePlayerRankings(TournamentRoundRobin tournamentRobinRound) {
        log.debug("Calculating player rankings for tournament: {}", tournamentRobinRound.getId());

        Set<Player> players = tournamentRobinRound.getPlayers();

        // Create a map with player rankings
        Map<Player, Integer> playerScores = new HashMap<>();

        for (Player player : players) {
            int score = TournamentUtils.calculateNewRating(
                    TournamentUtils.calculateNewWonMatches(player, tournamentRobinRound),
                    TournamentUtils.calculateNewLostMatches(player, tournamentRobinRound),
                    TournamentUtils.calculateNewGoalsScored(player, tournamentRobinRound),
                    TournamentUtils.calculateNewGoalsLost(player, tournamentRobinRound));

            playerScores.put(player, score);
        }

        // Sort players by score in descending order
        return playerScores.entrySet().stream()
                .sorted(Map.Entry.<Player, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public boolean isTournamentReadyToFinish(TournamentRoundRobin tournamentRobinRound) {
        // Check if all matches have been played
        return tournamentRobinRound.getMatches().stream().allMatch(m -> m.getWinner() != null);
    }

}
