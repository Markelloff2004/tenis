package org.cedacri.pingpong.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.repository.TournamentRepository;
import org.cedacri.pingpong.utils.MatchGenerator;
import org.cedacri.pingpong.utils.PlayerDistributer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;

    private static final Logger logger = LoggerFactory.getLogger(TournamentService.class);
    private final MatchService matchService;

    public TournamentService(TournamentRepository tournamentRepository, MatchService matchService) {
        this.tournamentRepository = tournamentRepository;
        this.matchService = matchService;
    }

    @Transactional
    public Stream<Tournament> findAll() {
        List<Tournament> tournaments = tournamentRepository.findAll();

        return tournaments.stream();
    }

    public Tournament find(Integer id) {
        return tournamentRepository.findById(id).orElseThrow();
    }

    @Transactional
    public Tournament saveTournament(Tournament tournament)  {
        return tournamentRepository.save(tournament);
    }

    @Transactional
    public void deleteById(Integer id)
    {
        Tournament tournamentToDelete = tournamentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tournament not found"));

        for(Player player : tournamentToDelete.getPlayers()){
            player.getTournaments().remove(tournamentToDelete);
        }

        tournamentRepository.deleteById(id);
    }

    @Transactional
    public void startTournament(Tournament tournament) {
        MatchGenerator matchGenerator = new MatchGenerator(tournament.getSetsToWin(), tournament.getSemifinalsSetsToWin(),
                tournament.getFinalsSetsToWin(), tournament.getTournamentType(), new PlayerDistributer(), this, matchService);

        matchGenerator.generateMatches(tournament);
    }
}
