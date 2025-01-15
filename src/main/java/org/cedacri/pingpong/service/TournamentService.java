package org.cedacri.pingpong.service;

import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.repository.MatchRepository;
import org.cedacri.pingpong.repository.TournamentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Stream;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;

    public TournamentService(TournamentRepository tournamentRepository, MatchRepository matchRepository) {
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
    public void delete(Integer id)
    {
        tournamentRepository.deleteById(id);
    }

}
