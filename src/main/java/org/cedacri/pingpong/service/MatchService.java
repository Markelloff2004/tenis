package org.cedacri.pingpong.service;

import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.repository.MatchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public List<Match> findAll() {
        return matchRepository.findAll().collect(Collectors.toList());
    }

    public Optional<Match> findById(Integer id) {
        return matchRepository.findById(id);
    }

    public List<Match> findAllByTournament(Tournament tournament) {
        return matchRepository.findByTournament(tournament).collect(Collectors.toList());
    }

//    public Match save(Match match) {
//        return matchRepository.save(match);
//    }
//
//    public void deleteById(Long id) {
//        matchRepository.deleteById(id);
//    }
}
