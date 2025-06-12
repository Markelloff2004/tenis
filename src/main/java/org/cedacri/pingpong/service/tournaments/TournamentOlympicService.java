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
import org.cedacri.pingpong.model.tournament.TournamentOlympic;
import org.cedacri.pingpong.repository.BaseTournamentRepository;
import org.cedacri.pingpong.utils.Constants;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service("tournamentOlympicService")
public class TournamentOlympicService extends BaseTournamentService<TournamentOlympic> {

    public TournamentOlympicService(BaseTournamentRepository baseTournamentRepository) {
        super(baseTournamentRepository);
    }

//    @Override
    public List<TournamentOlympic> findAllTournamentsOfType() {
        log.info("Fetching all Olympic tournaments");

        List<TournamentOlympic> tournamentOlympicList = getTournamentRepository().findAllByTournamentType(TournamentTypeEnum.OLYMPIC).stream().filter(TournamentOlympic.class::isInstance).map(TournamentOlympic.class::cast).sorted(Comparator.comparing(BaseTournament::getStartedAt, Comparator.nullsLast(Comparator.naturalOrder()))).toList();

        if (tournamentOlympicList.isEmpty()) {
            log.warn("No Olympic tournaments found");
            return Collections.emptyList();
        }

        return tournamentOlympicList;
    }

    @Override
    public void validateTournament(TournamentOlympic tournamentOlympic) {
        if (tournamentOlympic == null) {
            throw new IllegalArgumentException("Tournament cannot be null");
        }

        if (tournamentOlympic.getPlayers().size() <= Constants.MINIMAL_AMOUNT_OF_PLAYER_FOR_OLYMPIC) {
            throw new IllegalArgumentException("Tournament must have at least " + Constants.MINIMAL_AMOUNT_OF_PLAYER_FOR_OLYMPIC + " players");
        }

        if (tournamentOlympic.getTournamentStatus() != TournamentStatusEnum.PENDING) {
            throw new IllegalArgumentException("Tournament must be in a valid state to start (pending)");
        }

        log.debug("Tournament {} is valid for generation", tournamentOlympic.getTournamentName());
    }

    @SneakyThrows
    @Transactional
    @Override
    public TournamentOlympic startTournament(TournamentOlympic tournamentOlympic) {
        log.info("Starting an Olympic Tournament: {}", tournamentOlympic.getTournamentName());

        try {
            validateTournament(tournamentOlympic);

            generateMatches(tournamentOlympic);

            distributePlayersInFirstRound(tournamentOlympic);

            handleWalkoverPlayers(tournamentOlympic);

            TournamentOlympic savedTournament = createTournament(tournamentOlympic);

            log.info("Olympic Tournament started successfully.");
            return savedTournament;

        } catch (IllegalArgumentException e) {
            log.error("Error starting Olympic Tournament: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error starting Olympic Tournament: {}", e.getMessage(), e);
            throw new UnexpectedTournamentException("Failed to start Olympic Tournament", e);
        }

    }

    @Override
    public void generateMatches(TournamentOlympic tournamentOlympic) {

        log.debug("Generating olympic bracket for tournament: {} {}", tournamentOlympic.getId(), tournamentOlympic.getTournamentName());

        int maxPlayers = tournamentOlympic.getMaxPlayers();
        int totalRounds = calculateNumberOfRounds(maxPlayers);

        List<Match> allMatches = new ArrayList<>();
        Map<Integer, List<Match>> roundMatches = new HashMap<>();

        // Generate rounds in reverse order (from finals to first round)
        for (int round = totalRounds; round > 0; round--) {

            List<Match> matchesInRound = generateMatchesForRound(round, totalRounds, tournamentOlympic, roundMatches);
            roundMatches.put(round, matchesInRound);
            allMatches.addAll(matchesInRound);
        }

        // Save all matches to the tournament
        log.debug("Saving all generated matches for tournament: {} {}", tournamentOlympic.getId(), tournamentOlympic.getTournamentName());
        tournamentOlympic.setMatches(allMatches);
    }

    @Override
    public Player determineTournamentWinner(TournamentOlympic tournament) {
        return tournament.getMatches().stream()
                //TODO: Need to check if the final match is actually the match Round=1, Position=1 of the tournament
                .filter(m -> m.getRound() == 1 && m.getPosition() == 1).map(Match::getWinner).findAny().orElse(null);

    }

    @Override
    public void endTournament(TournamentOlympic tournament) {
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
                throw new IllegalStateException("No winner determined for tournament: " + tournament.getTournamentName());
            }

            getTournamentRepository().save(tournament);
        } else {
            log.warn("Cannot end tournament {}: not all matches have been played", tournament.getTournamentName());
            throw new IllegalStateException("Cannot end tournament: not all matches have been played");
        }

        log.info("Tournament {} ended successfully with winner: {}", tournament.getTournamentName(), tournament.getWinner().getName());
    }

    @Override
    public boolean allMatchesHasBeenPlayed(TournamentOlympic tournament) {
        return tournament.getMatches().stream().noneMatch(m -> m.getWinner() == null);
    }

    // Private methods that help for generating matches and advancing players
    private void advancePlayerToNextRound(Match match, Player advancingPlayer) {
        Match nextMatch = match.getNextMatch();

        if (nextMatch != null) {
            if (nextMatch.getTopPlayer() == null) {
                nextMatch.setTopPlayer(advancingPlayer);
            } else if (nextMatch.getBottomPlayer() == null) {
                nextMatch.setBottomPlayer(advancingPlayer);
            }

            match.setWinner(advancingPlayer);
        }
    }

    public void distributePlayersInFirstRound(TournamentOlympic tournamentOlympic) {

        int maxPlayers = tournamentOlympic.getMaxPlayers();

        // Group players by rating -> best to worst. After those pairs will be arranged in a predefined order
        List<Player[]> pairs = distributePlayersIntoOlympicPairs(tournamentOlympic, maxPlayers);
        List<Match> firstRoundMatches = getFirstRoundMatches(tournamentOlympic);

        if (tournamentOlympic.getMatches().size() != pairs.size()) {
            throw new IllegalStateException("Amount of first round matches does not match with number of pairs!");
        }

        int[] orderedPosition = Constants.OLYMPIC_POSITIONS.get(maxPlayers);

        if (orderedPosition == null) {
            throw new IllegalArgumentException("Unsupported number of players: " + maxPlayers);
        }

        log.debug("Distributing players in first round matches for tournament: {} {}", tournamentOlympic.getId(), tournamentOlympic.getTournamentName());
        assignPlayersToMatches(firstRoundMatches, pairs, orderedPosition);
    }

    public void handleWalkoverPlayers(TournamentOlympic tournamentOlympic) {
        log.debug("Handling walkover players for tournament: {} {}", tournamentOlympic.getId(), tournamentOlympic.getTournamentName());

        List<Match> firstRoundMatches = getFirstRoundMatches(tournamentOlympic);

        int walkoversProcessed = 0;

        for (Match match : firstRoundMatches) {
            Player advancingPlayer = null;

            if (match.getTopPlayer() != null && match.getBottomPlayer() == null) {
                advancingPlayer = match.getTopPlayer();
            } else if (match.getBottomPlayer() != null && match.getTopPlayer() == null) {
                advancingPlayer = match.getBottomPlayer();
            }

            if (advancingPlayer != null && match.getNextMatch() != null) {

                advancePlayerToNextRound(match, advancingPlayer);

                walkoversProcessed++;
            }
        }

        log.debug("Processed {} walkovers in first round", walkoversProcessed);
    }

    private int calculateNumberOfRounds(int maximalNumberOfPlayers) {
        if (maximalNumberOfPlayers < Constants.MINIMAL_AMOUNT_OF_PLAYER_FOR_OLYMPIC) {
            log.warn("Number of players must be at least {}", Constants.MINIMAL_AMOUNT_OF_PLAYER_FOR_OLYMPIC);
            throw new IllegalArgumentException("Number of players must be at least " + Constants.MINIMAL_AMOUNT_OF_PLAYER_FOR_OLYMPIC);
        }

        int rounds = 0;
        while (maximalNumberOfPlayers > Constants.MINIMAL_AMOUNT_OF_PLAYER_FOR_OLYMPIC) {
            maximalNumberOfPlayers = (maximalNumberOfPlayers + 1) / 2;
            rounds++;
        }
        return rounds;
    }

    private List<Match> generateMatchesForRound(int round, int totalRounds, TournamentOlympic tournament, Map<Integer, List<Match>> existingRounds) {
        List<Match> matches = new ArrayList<>();
        int matchesInRound = calculateMatchesInRound(round, totalRounds);

        if (round == totalRounds) {
            // Generating matches for the final round
            Match finalMatch = createMatch(round, 1, null, tournament);
            matches.add(finalMatch);
        } else {
            // Generating matches for other rounds
            List<Match> nextRoundMatches = existingRounds.get(round + 1);

            for (int position = 1; position <= matchesInRound; position++) {
                Match nextMatch = findNextMatchForPosition(position, nextRoundMatches);
                Match match = createMatch(round, position, nextMatch, tournament);
                matches.add(match);
            }
        }

        return matches;
    }

    private int calculateMatchesInRound(int round, int totalRounds) {
        if (round == totalRounds) {
            return 1; // Finals
        }
        return (int) Math.pow(2, (totalRounds - round));
    }


    private Match findNextMatchForPosition(int position, List<Match> nextRoundMatches) {
        int nextMatchPosition = (position + 1) / 2;
        return nextRoundMatches.stream().filter(match -> match.getPosition() == nextMatchPosition).findFirst().orElse(null);
    }


    private Match createMatch(int round, int position, Match nextMatch, TournamentOlympic tournamentOlympic) {
        return Match.builder().round(round).position(position).nextMatch(nextMatch).tournament(tournamentOlympic).build();
    }

    private List<Player[]> distributePlayersIntoOlympicPairs(TournamentOlympic tournament, int maxPlayers) {
        log.info("Ordering players by rating in {} tournament", tournament);
        List<Player> sortedPlayers = getSortedPlayersByRating(tournament);

        log.debug("Complete player list with BYE(null) players");
        List<Player> paddedPlayers = padPlayersToSize(sortedPlayers, maxPlayers);

        return createPlayerPairs(paddedPlayers, maxPlayers);
    }

    private List<Player> getSortedPlayersByRating(TournamentOlympic tournament) {
        return tournament.getPlayers().stream().sorted(Comparator.comparingInt(Player::getRating).reversed()).toList();
    }

    private List<Player> padPlayersToSize(List<Player> players, int maxPlayers) {
        List<Player> paddedPlayers = new ArrayList<>(players);
        while (paddedPlayers.size() < maxPlayers) {
            paddedPlayers.add(null);
        }
        return paddedPlayers;
    }

    private List<Player[]> createPlayerPairs(List<Player> paddedPlayers, int maxPlayers) {
        List<Player[]> pairs = new ArrayList<>();
        log.info("Grouping players into pairs");

        for (int i = 0; i < maxPlayers / 2; i++) {
            Player topPlayer = paddedPlayers.get(i);
            Player bottomPlayer = paddedPlayers.get(maxPlayers - i - 1);

            if (topPlayer == null && bottomPlayer == null) {
                log.warn("Skipping empty match pair: Index {}", i);
                continue;
            }

            pairs.add(new Player[]{topPlayer, bottomPlayer});

            log.info("Generated new pair: TopPlayer={}, BottomPlayer={}", topPlayer != null ? topPlayer.getName() + " " + topPlayer.getSurname() : "null", bottomPlayer != null ? bottomPlayer.getName() + " " + bottomPlayer.getSurname() : "null");

        }
        return pairs;
    }

    private List<Match> getFirstRoundMatches(TournamentOlympic tournament) {
        return tournament.getMatches().stream().filter(m -> m.getRound() == 1).sorted(Comparator.comparingInt(Match::getPosition).reversed()).toList();
    }

    private void assignPlayersToMatches(List<Match> matches, List<Player[]> pairs, int[] orderedPosition) {
        for (int i = 0; i < matches.size(); i++) {
            Match match = matches.get(i);
            Player[] pair = pairs.get(orderedPosition[i] - 1);

            assignPlayersToMatch(match, pair[0], pair[1]);
        }
    }

    private void assignPlayersToMatch(Match match, Player topPlayer, Player bottomPlayer) {
        match.setTopPlayer(topPlayer);
        match.setBottomPlayer(bottomPlayer);

        if (topPlayer != null && bottomPlayer == null) {
            match.setWinner(topPlayer);
        } else if (topPlayer == null && bottomPlayer != null) {
            match.setWinner(bottomPlayer);
        }
    }

    public boolean isTournamentReadyToFinish(TournamentOlympic tournamentOlympic) {
        // All matches have been played
        return tournamentOlympic.getMatches().stream().allMatch(m -> m.getWinner() != null);
    }
}
