package org.cedacri.pingpong.service;

import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.repository.TournamentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;

    public TournamentService(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    public Stream<Tournament> findAll() {
        return tournamentRepository.findAll();
    }

    public Optional<Tournament> find(Integer id) {
        return tournamentRepository.findById(id);
    }

    public Tournament create(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    @Transactional
    public void delete(Integer id) {
        tournamentRepository.deleteById(id);
    }
}
