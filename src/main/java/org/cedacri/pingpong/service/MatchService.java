package org.cedacri.pingpong.service;

import jakarta.persistence.EntityNotFoundException;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.repository.MatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Match> findMatchById(Integer id)
    {
        return matchRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Match> getMatchesByTournament(Tournament tournament) {
        return matchRepository.findByTournament(tournament);
    }

    @Transactional(readOnly = true)
    public List<Match> getMatchesByTournamentAndRound(Tournament tournament, String round) {
        return matchRepository.findByTournamentAndRound(tournament, round);
    }

    @Transactional(readOnly = true)
    public Optional<Match> getMatchByTournamentRoundAndPosition(Tournament tournament, String round, Integer position) {
        return matchRepository.findByTournamentAndRoundAndPosition(tournament, round, position);
    }

    @Transactional(readOnly = true)
    public List<Match> getMatchesByTopPlayer(Player topPlayer) {
        return matchRepository.findByTopPlayer(topPlayer);
    }

    @Transactional(readOnly = true)
    public List<Match> getMatchesByBottomPlayer(Player bottomPlayer) {
        return matchRepository.findByBottomPlayer(bottomPlayer);
    }

    // Găsește toate meciurile câștigate de un anumit jucător
    @Transactional(readOnly = true)
    public List<Match> getMatchesByWinner(Player winner) {
        return matchRepository.findByWinner(winner);
    }

    @Transactional
    public Match saveMatch(Match match) {
        return matchRepository.save(match);
    }

    @Transactional
    public void deleteMatchById(Integer matchId) {
        matchRepository.deleteById(matchId);
    }

    @Transactional(readOnly = true)
    public Optional<Match> getMatchById(Integer id) {
        return matchRepository.findById(id);
    }

    public String updateMatch(Match updateMatch)
    {
        if (matchRepository.findById(updateMatch.getId()).isPresent())
        {
            System.out.println("Takoi match exista ");
            matchRepository.save(updateMatch);
        }
        else
        {
            System.out.println("Takoi match non exista ");
        }
        return updateMatch.toString();
    }

    @Transactional
    public void saveOrUpdateMatch(Match nextRoundMatch)
    {
        if (nextRoundMatch == null) {
            throw new IllegalArgumentException("Match cannot be null.");
        }

        if (nextRoundMatch.getId() != null) {
            // Verificăm dacă entitatea există deja
            Optional<Match> existingMatch = matchRepository.findByTournamentAndRoundAndPosition(nextRoundMatch.getTournament(), nextRoundMatch.getRound(), nextRoundMatch.getPosition());
            if (existingMatch.isPresent()) {
                // Actualizăm entitatea existentă
                matchRepository.save(nextRoundMatch);
                System.out.println("INFO: Match "+nextRoundMatch+" updated successfully.");
            } else {
                throw new EntityNotFoundException("Match with ID " + nextRoundMatch.getId() + " not found.");
            }
        } else {
            // Salvăm o entitate nouă
            matchRepository.save(nextRoundMatch);
            System.out.println("INFO: New match "+nextRoundMatch+" saved successfully.");
        }
    }
}