package org.cedacri.pingpong.service;

import jakarta.persistence.EntityNotFoundException;
import org.cedacri.pingpong.entity.Match;
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

    public List<Match> getMatchesByTournamentAndRound(Tournament tournament, String round) {
        return matchRepository.findByTournamentAndRound(tournament, round);
    }

    public Optional<Match> getMatchByTournamentRoundAndPosition(Tournament tournament, String round, Integer position) {
        return matchRepository.findByTournamentAndRoundAndPosition(tournament, round, position);
    }

    @Transactional
    public void saveOrUpdateMatch(Match nextRoundMatch)
    {
        if (nextRoundMatch == null) {
            throw new IllegalArgumentException("Match cannot be null.");
        }

        if (nextRoundMatch.getId() != null) {
            // Check if entity exists
            Optional<Match> existingMatch = matchRepository.findByTournamentAndRoundAndPosition(nextRoundMatch.getTournament(), nextRoundMatch.getRound(), nextRoundMatch.getPosition());
            if (existingMatch.isPresent()) {
                // Update actual entity
                matchRepository.save(nextRoundMatch);
                System.out.println("INFO: Match "+nextRoundMatch+" updated successfully.");
            } else {
                throw new EntityNotFoundException("Match with ID " + nextRoundMatch.getId() + " not found.");
            }
        } else {
            // Saving new entity
            matchRepository.save(nextRoundMatch);
            System.out.println("INFO: New match "+nextRoundMatch+" saved successfully.");
        }
    }
}