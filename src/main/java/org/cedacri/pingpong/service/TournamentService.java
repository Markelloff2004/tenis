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

    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }

    public Optional<Tournament> getTournamentById(Long id) {
        return tournamentRepository.findById(id);
    }

    public Tournament createTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    public Tournament updateTournament(Long id, Tournament tournamentDetails) {
        return tournamentRepository.findById(id).map(tournament -> {
            tournament.setName(tournamentDetails.getName());
            tournament.setStatus(tournamentDetails.getStatus());
            tournament.setPlayers(tournamentDetails.getPlayers());
            tournament.setRules(tournamentDetails.getRules());
            tournament.setWinner(tournamentDetails.getWinner());
            return tournamentRepository.save(tournament);
        }).orElseThrow(() -> new RuntimeException("Tournament not found with id " + id));
    }

    public void deleteTournament(Long id) {
        tournamentRepository.deleteById(id);
    }
}
