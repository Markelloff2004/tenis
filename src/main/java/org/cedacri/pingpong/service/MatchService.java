package org.cedacri.pingpong.service;

import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.repository.MatchRepository;
import org.springframework.stereotype.Service;

import java.util.*;
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

    public Match save(Match match) {
        return matchRepository.save(match);
    }


    public List<Match> generateMatches(Tournament tournament) {
        List<Player> players = new ArrayList<>(tournament.getPlayers());
        int playersCount = players.size();
        int totalRounds = (int) Math.ceil(Math.log(playersCount) / Math.log(2));

        List<Match> matches = new ArrayList<>();
        Queue<Player> playerQueue = new LinkedList<>(players);

        int round = 1;

        while(!playerQueue.isEmpty()){
            Player leftPlayer = playerQueue.poll();
            Player rightPlayer = playerQueue.poll();

            Match match = new Match();

            match.setTournament(tournament);
            match.setRound(round);
            match.setLeftPlayer(leftPlayer);
            match.setRightPlayer(rightPlayer);
            matches.add(match);
        }

        matches.forEach(m -> save(m));

        return matches;
    }
}


