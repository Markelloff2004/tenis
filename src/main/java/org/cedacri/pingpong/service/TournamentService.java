package org.cedacri.pingpong.service;

import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.repository.TournamentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;

    public TournamentService(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    public List<Tournament> findAll() {
        return tournamentRepository.findAll();
    }

    public Optional<Tournament> find(Long id) {
        return tournamentRepository.findById(id);
    }

    public Tournament create(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    public void delete(Long id) {
        tournamentRepository.deleteById(id);
    }
}
