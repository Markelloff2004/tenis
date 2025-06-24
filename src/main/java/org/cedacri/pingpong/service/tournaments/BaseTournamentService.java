package org.cedacri.pingpong.service.tournaments;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.model.tournament.BaseTournament;
import org.cedacri.pingpong.repository.BaseTournamentRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class BaseTournamentService<T extends BaseTournament> implements ITournamentCrud<T>{

    private final BaseTournamentRepository tournamentRepository;

    protected BaseTournamentService(BaseTournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    protected BaseTournamentRepository getTournamentRepository() {
        return this.tournamentRepository;
    }

    // ITournamentCrud Implementation
    @Transactional
    @Override
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
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .toList();
    }

    protected void validateTournamentId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Tournament ID must be a positive number");
        }
    }

    @Transactional
    @Override
    public T findTournamentById(Long id) {
        validateId(id);
        log.debug("Fetching tournament with ID: {}", id);

        BaseTournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tournament with ID " + id + " not found"));

//        try {
//            Hibernate.initialize(tournament.getPlayers());
//            Hibernate.initialize(tournament.getMatches());
//        } catch (Exception ex) {
//            log.error(ex.getMessage(), ex);
//        }

        return castToType(tournament, id);
    }

    @Transactional
    @Override
    public T createTournament(T tournament) {
        validateTournamentNotNull(tournament);
        log.debug("Creating tournament: {}", tournament);

        T savedTournament = tournamentRepository.save(tournament);
        log.info("Tournament created with ID: {}", savedTournament.getId());
        return savedTournament;
    }

    @Transactional
    @Override
    public T updateTournament(T tournament) {
        validateTournamentForUpdate(tournament);
        log.debug("Updating tournament with ID: {}", tournament.getId());

        T updatedTournament = tournamentRepository.save(tournament);
        log.info("Tournament updated with ID: {}", updatedTournament.getId());
        return updatedTournament;
    }

    @Transactional
    @Override
    public void deleteTournamentById(Long id) {
        validateId(id);
        log.debug("Deleting tournament with ID: {}", id);

        if (!tournamentRepository.existsById(id)) {
            throw new EntityNotFoundException("Tournament with ID " + id + " not found");
        }

        tournamentRepository.deleteById(id);
        log.info("Tournament deleted with ID: {}", id);
    }

    // Additional methods
    protected void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Tournament ID must be a positive number");
        }
    }

    protected void validateTournamentNotNull(T tournament) {
        if (tournament == null) {
            throw new IllegalArgumentException("Tournament cannot be null");
        }
    }

    protected void validateTournamentForUpdate(T tournament) {
        validateTournamentNotNull(tournament);
        if (tournament.getId() == null) {
            throw new IllegalArgumentException("Tournament ID cannot be null for update");
        }
    }

    @SuppressWarnings("unchecked")
    private T castToType(BaseTournament tournament, Long id) {
        try {
            return (T) tournament;
        } catch (ClassCastException e) {
            String errorMessage = String.format(
                    "Type mismatch for tournament ID %d. Expected: %s, Actual: %s",
                    id,
                    this.getClass().getSimpleName(),
                    tournament.getClass().getSimpleName()
            );
            log.error(errorMessage, e);
            throw new IllegalArgumentException(errorMessage, e);
        }
    }
}