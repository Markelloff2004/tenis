package org.cedacri.pingpong.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.repository.TournamentRepository;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final EntityManager em;

//    private static final Logger logger = LoggerFactory.getLogger(TournamentService.class);

    public TournamentService(TournamentRepository tournamentRepository, EntityManager em) {
        this.tournamentRepository = tournamentRepository;
        this.em = em;
    }

    @Transactional
    public Stream<Tournament> findAll(boolean initializePlayers) {
        List<Tournament> tournaments = tournamentRepository.findAll();

        if(initializePlayers) {
            tournaments.forEach(t -> Hibernate.initialize(t.getPlayers()));
        }

        return tournaments.stream();
    }

    public Optional<Tournament> find(Integer id) {
        return tournamentRepository.findById(id);
    }

    @Transactional
    public Tournament saveTournament(Tournament tournament) {
        Tournament merged = em.merge(tournament);
        em.flush();

        return merged;
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



}
