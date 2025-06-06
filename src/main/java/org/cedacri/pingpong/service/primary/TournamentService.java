package org.cedacri.pingpong.service.primary;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.exception.tournament.NotEnoughPlayersException;
import org.cedacri.pingpong.repository.TournamentRepository;
import org.cedacri.pingpong.utils.MatchGenerator;
import org.cedacri.pingpong.utils.PlayerDistributer;
import org.cedacri.pingpong.utils.TournamentUtils;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;

    public TournamentService(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Transactional
    public Tournament saveTournament(Tournament tournament) {
        if (tournament == null) {
            throw new IllegalArgumentException("Tournament cannot be null");
        }
        return tournamentRepository.save(tournament);
    }

    @Transactional
    public void deleteTournamentById(Long id) {
        validateTournamentId(id);
        Tournament tournament = loadTournamentOrThrow(id);
        tournamentRepository.delete(tournament);
    }

    @Transactional
    public void startTournament(Tournament tournament) throws NotEnoughPlayersException {
        int minPlayers = TournamentUtils.getMinimalPlayersRequired(tournament.getTournamentType());
        if (tournament.getPlayers().size() < minPlayers) {
            throw new NotEnoughPlayersException(tournament.getPlayers().size(), minPlayers);
        }

        tournament.setStartedAt(LocalDate.now());
        tournamentRepository.save(tournament);

        MatchGenerator generator = createMatchGenerator();
        generator.generateMatches(tournament);
    }

    @Transactional
    public Stream<Tournament> findAllTournamentsWithInitializedPlayers() {
        List<Tournament> tournaments = tournamentRepository.findAll();
        tournaments.forEach(t -> Hibernate.initialize(t.getPlayers()));
        return sortTournamentsByCreationDateDesc(tournaments);
    }

    @Transactional
    public Stream<Tournament> findAllTournaments() {
        return sortTournamentsByCreationDateDesc(tournamentRepository.findAll());
    }

    @Transactional
    public Tournament findTournamentByIdWithInitializedPlayers(Long id) {
        Tournament tournament = loadTournamentOrThrow(id);
        Hibernate.initialize(tournament.getPlayers());
        return tournament;
    }

    @Transactional
    public Tournament findTournamentById(Long id) {
        return loadTournamentOrThrow(id);
    }

    private Tournament loadTournamentOrThrow(Long id) {
        validateTournamentId(id);
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tournament with ID " + id + " not found"));
    }

    private static void validateTournamentId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Tournament ID must be positive and not null");
        }
    }

    private static Stream<Tournament> sortTournamentsByCreationDateDesc(List<Tournament> tournaments) {
        return tournaments.stream()
                .sorted(Comparator.comparing(Tournament::getCreatedAt).reversed());
    }

    private MatchGenerator createMatchGenerator() {
        return new MatchGenerator(new PlayerDistributer(), this);
    }
}