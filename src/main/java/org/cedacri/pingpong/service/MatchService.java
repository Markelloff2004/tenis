package org.cedacri.pingpong.service;

import jakarta.transaction.Transactional;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Score;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.repository.MatchRepository;
import org.cedacri.pingpong.utils.Constants;
import org.cedacri.pingpong.utils.TournamentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private static final Logger log = LoggerFactory.getLogger(MatchService.class);

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public List<Match> getMatchesByTournamentAndRound(Tournament tournament, int round) {
        log.debug("Fetching matches for tournament: {} and round: {}", tournament, round);
        List<Match> matches = matchRepository.findByTournamentAndRound(tournament, round);
        log.info("Found {} matches for tournament: {} and round: {}", matches.size(), tournament, round);
        return matches;
    }

    public Optional<Match> getMatchByTournamentRoundAndPosition(Tournament tournament, int round, int position) {
        log.debug("Fetching match for tournament: {}, round: {} and position: {}", tournament, round, position);
        Optional<Match> match = matchRepository.findByTournamentAndRoundAndPosition(tournament, round, position);
        if(match.isPresent()) {
            log.info("Match found: {}", match.get());
        }
        else{
            log.warn("Match not found!");
        }
        return match;

    }

    @Transactional
    public Match saveMatch(Match match) {
        log.debug("Attempting to save or update match: {}", match);
        // Saving new entity
        return matchRepository.save(match);
    }

    @Transactional
    public void deleteMatch(Match match) {
        if(match == null || match.getId() == null) {
            log.error("Attempted to delete a null Match");
            throw new IllegalArgumentException("Match cannot be null.");
        }

        log.debug("Attempting to delete match: {}", match);

        matchRepository.deleteById(match.getId());
        log.debug("Match deleted: {}", match);
    }


    public String validateAndUpdateScores(Match match, List<Score> newMatchScores)
    {
        StringBuilder infoMessage = new StringBuilder();

        List<Score> validScores = new ArrayList<>();

        for (Score score : newMatchScores)
        {
            String validationMessage = validateScore(score.getTopPlayerScore(), score.getBottomPlayerScore());
            if (validationMessage != null)
            {
                infoMessage.append(validationMessage).append("\n");
            } else
            {
                validScores.add(score);
            }
        }

        if (!validScores.isEmpty())
        {
            match.setScore(validScores);
            matchRepository.save(match);

            TournamentUtils.determinateWinner(match);
            matchRepository.save(match);
        }

        return infoMessage.toString();
    }

    private String validateScore(int topScore, int bottomScore)
    {
        if (Math.min(topScore, bottomScore) >= Constants.MINIMAL_POINTS_IN_SET - 1)
        {
            if (Math.abs(topScore - bottomScore) != Constants.MINIMAL_DIFFERENCE_OF_POINTS_IN_SET)
            {
                return "The difference between the scores {" + topScore + "-" + bottomScore + "} should be 2 points.";
            }
        } else if (Math.max(topScore, bottomScore) == Constants.MINIMAL_POINTS_IN_SET)
        {
            return null;
        } else
        {
            return "Score {" + topScore + "-" + bottomScore + "} with points < 11 will not be saved!";
        }

        return null;
    }


}