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
        return tournamentRepository.getAll().toList();
    }

    public Optional<Tournament> find(Integer id) {
        return tournamentRepository.getById(id);
    }

    public Tournament create(Tournament tournament) {
        return tournamentRepository.saveTournament(tournament);
    }

    public void delete(Integer id) {
        tournamentRepository.deleteTournamentById(id);
    }
}
