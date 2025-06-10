package org.cedacri.pingpong.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.BaseTournament;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
import org.cedacri.pingpong.repository.BaseTournamentRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public abstract class TournamentService implements ITournamentServiceInterface {
    private final BaseTournamentRepository tournamentRepository;

    public TournamentService(BaseTournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Transactional
    public List<BaseTournament> findAllTournaments() {
        log.info("Fetching all tournaments");

        List<BaseTournament> tournaments = tournamentRepository.findAll();

        if (tournaments.isEmpty()) {
            log.warn("No tournaments found");
            return List.of();
        }

        // Sort tournaments by start date
        return tournaments.stream()
                .sorted(Comparator.comparing(BaseTournament::getStartedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    public BaseTournament findTournamentById(Long id) {
        validateTournamentId(id);
        log.debug("Fetching tournament with ID: {}", id);

        return tournamentRepository.findById(id)
                .map(tournament -> {
                    // NOTE: Check if is better to initialize players here or in the TournamentPlayerService in a method like findTournamentWithPlayers
                    Hibernate.initialize(tournament.getPlayers());
                    return tournament;
                })
                .orElseThrow(() -> new EntityNotFoundException("Tournament with ID " + id + " not found"));
    }

    public List<BaseTournament> findAllTournamentsByType(TournamentTypeEnum tournamentType) {
        log.debug("Fetching all tournaments of type: {}", tournamentType);
        List<BaseTournament> tournaments = tournamentRepository.findAllByTournamentType(tournamentType);

        if (tournaments.isEmpty()) {
            log.warn("No tournaments found for type: {}", tournamentType);
            return List.of();
        }

        // Sort tournaments by start date
        return tournaments.stream()
                .sorted(Comparator.comparing(BaseTournament::getStartedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    @Transactional
    public BaseTournament saveTournament(BaseTournament baseTournament) {

        if (baseTournament == null) {
            throw new IllegalArgumentException("Tournament cannot be null");
        }

        log.debug("Saving tournament: {}", baseTournament);
        BaseTournament savedTournament = tournamentRepository.saveAndFlush(baseTournament);

        log.info("Tournament saved with ID: {}", savedTournament.getId());
        return savedTournament;
    }

    @Transactional
    public void deleteTournamentById(Long id) {
        validateTournamentId(id);
        log.debug("Deleting tournament with ID: {}", id);

        BaseTournament tournament = findTournamentById(id);
        if (tournament == null) {
            throw new EntityNotFoundException("Tournament with ID " + id + " not found");
        }

        tournamentRepository.delete(tournament);
        log.info("Tournament with ID: {} deleted successfully", id);
    }

    protected void validateTournamentId(Long id) {
        log.debug("Validating tournament ID: {}", id);
        if (id == null) {
            throw new IllegalArgumentException("Tournament ID cannot be null");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("Tournament ID must be a positive number");
        }

        log.debug("Tournament ID {} is valid", id);
    }

    public boolean isTournamentActive(BaseTournament tournament) {
        return tournament.getTournamentStatus().equals(TournamentStatusEnum.ONGOING);
    }

    @Override
    public abstract BaseTournament startTournament(BaseTournament baseTournament);
}
