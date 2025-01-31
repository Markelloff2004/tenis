package org.cedacri.pingpong.service;

import jakarta.transaction.Transactional;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.repository.MatchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private static final Logger logger = LoggerFactory.getLogger(MatchService.class);

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public List<Match> getMatchesByTournamentAndRound(Tournament tournament, int round) {
        logger.debug("Fetching matches for tournament: {} and round: {}", tournament, round);
        List<Match> matches = matchRepository.findByTournamentAndRound(tournament, round);
        logger.info("Found {} matches for tournament: {} and round: {}", matches.size(), tournament, round);
        return matches;
    }

    public Optional<Match> getMatchByTournamentRoundAndPosition(Tournament tournament, int round, int position) {
        logger.debug("Fetching match for tournament: {}, round: {} and position: {}", tournament, round, position);
        Optional<Match> match = matchRepository.findByTournamentAndRoundAndPosition(tournament, round, position);
        if(match.isPresent()) {
            logger.info("Match found: {}", match.get());
        }
        else{
            logger.warn("Match not found!");
        }
        return match;

    }

    @Transactional
    public Match saveMatch(Match match) {
        logger.debug("Attempting to save or update match: {}", match);
        // Saving new entity
        return matchRepository.save(match);
    }

    @Transactional
    public void deleteMatch(Match match) {
        if(match == null || match.getId() == null) {
            logger.error("Attempted to delete a null Match");
            throw new IllegalArgumentException("Match cannot be null.");
        }

        logger.debug("Attempting to delete match: {}", match);

        matchRepository.deleteById(match.getId());
        logger.debug("Match deleted: {}", match);
    }
}