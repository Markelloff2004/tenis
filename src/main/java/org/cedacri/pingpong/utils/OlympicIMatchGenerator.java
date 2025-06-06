package org.cedacri.pingpong.utils;

import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.entity.TournamentOlympic;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.interfaces.IMatchGenerator;
import org.cedacri.pingpong.service.primary.TournamentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class OlympicIMatchGenerator implements IMatchGenerator<TournamentOlympic> {

    private final PlayerDistributer playerDistributer;
    private final TournamentService tournamentService;

    public OlympicIMatchGenerator(PlayerDistributer playerDistributer, TournamentService tournamentService) {
        this.playerDistributer = playerDistributer;
        this.tournamentService = tournamentService;
    }

    @Override
    public void generateMatches(TournamentOlympic tournament) {
        // Implementation for generating matches in an Olympic tournament
        validateTournament(tournament);

        tournament.setTournamentStatus(TournamentStatusEnum.ONGOING);
        int totalRounds = TournamentUtils.calculateNumberOfRounds(tournament.getMaxPlayers());

        generateMatchStructure(tournament, totalRounds);
        distributePlayersInMatches(tournament);

        tournamentService.saveTournament(tournament);
    }

    @Override
    public void distributePlayersInMatches(TournamentOlympic tournament) {
        playerDistributer.distributePlayersInFirstRound(tournament.getMaxPlayers(), tournament);
        TournamentUtils.movePlayersWithoutOpponent(1, tournament);
    }

    @Override
    public void validateTournament(TournamentOlympic tournament) {
        if (tournament == null) {
            throw new IllegalArgumentException("Tournament cannot be null");
        }
        if (tournament.getMaxPlayers() < Constants.MINIMAL_AMOUNT_OF_PLAYER_FOR_OLYMPIC) {
            throw new IllegalArgumentException("Not enough players to generate matches");
        }
    }

    @Override
    public void generateMatchStructure(TournamentOlympic tournament, int totalRounds) {
        // Logic to create the match structure based on the number of rounds
        // This could involve creating matches, setting up rounds, etc.
        log.info("Generating match structure for Olympic tournament with {} rounds", totalRounds);

        int currentMatches = 1;
        int position = 1;

        Match finalMatch = createMatch(totalRounds, position, null, tournament);
        tournament.getMatches().add(finalMatch);

        for (int round = totalRounds - 1; round > 0; round--) {
            currentMatches *= 2;
            List<Match> currentRoundMatches = new ArrayList<>();

            for (int i = 0; i < currentMatches; i++) {
                position++;
                int currentRound = round;

                List<Match> previousRoundMatches = tournament.getMatches().stream()
                        .filter(m -> m.getRound() == (currentRound + 1))
                        .toList();

                int nextMatchIndex = position / 2;
                Optional<Match> nextMatch = previousRoundMatches.stream()
                        .filter(m -> m.getPosition() == nextMatchIndex)
                        .findFirst();

                if (nextMatch.isPresent()) {
                    Match match = createMatch(round, position, nextMatch.get(), tournament);
                    currentRoundMatches.add(match);
                } else {
                    log.warn("Failed to generate match: No match found for position {} in round {} of tournament {}",
                            nextMatchIndex, round, tournament.getTournamentName());
                }
            }
            tournament.getMatches().addAll(currentRoundMatches);

        }
    }

    private Match createMatch(int round, int position, Match nextMatch, Tournament tournament) {
        return Match.builder()
                .round(round)
                .position(position)
                .nextMatch(nextMatch)
                .tournament(tournament)
                .build();

    }
}
