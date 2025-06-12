package org.cedacri.pingpong.service.tournaments;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.model.player.Player;
import org.cedacri.pingpong.model.tournament.BaseTournament;
import org.cedacri.pingpong.repository.BaseTournamentRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public abstract class BaseTournamentService<T extends BaseTournament> implements ITournamentCrud<T>, ITournamentOperations<T> {

    private final BaseTournamentRepository tournamentRepository;

    protected BaseTournamentService(BaseTournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    protected BaseTournamentRepository getTournamentRepository() {
        return this.tournamentRepository;
    }

    protected void validateTournamentId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Tournament ID must be a positive number");
        }
    }

    // ITournamentCrud Implementation
    @Override
    @Transactional
    public List<BaseTournament> findAllTournaments() {
        log.info("Fetching all tournaments");

        List<BaseTournament> tournaments = tournamentRepository.findAll();

        if (tournaments.isEmpty()) {
            log.warn("No tournaments found");
            return List.of();
        }

        return tournaments.stream()
                .sorted(Comparator.comparing(
                        BaseTournament::getStartedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    @Override
    public T findTournamentById(Long id) {

        validateTournamentId(id);

        log.debug("Fetching tournament with ID: {}", id);

        BaseTournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tournament with ID " + id + " not found"));

        Hibernate.initialize(tournament.getPlayers());

        try {
            return (T) tournament;
        } catch (ClassCastException e) {
            log.error("Tournament with ID {} is not of type {}", id, this.getClass().getSimpleName(), e);
            throw new IllegalArgumentException("Tournament with ID " + id + " is not of type " + this.getClass().getSimpleName(), e);
        }
    }

    @Override
    @Transactional
    public T createTournament(T tournament) {
        if (tournament == null) {
            throw new IllegalArgumentException("Tournament cannot be null");
        }

        log.debug("Creating tournament: {}", tournament);
        T savedTournament = tournamentRepository.save(tournament);
        log.info("Tournament created with ID: {}", savedTournament.getId());
        return savedTournament;
    }
}