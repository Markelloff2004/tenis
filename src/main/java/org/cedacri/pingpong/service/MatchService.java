package org.cedacri.pingpong.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Score;
import org.cedacri.pingpong.entity.TournamentOlympic;
import org.cedacri.pingpong.repository.MatchRepository;
import org.cedacri.pingpong.utils.Constants;
import org.cedacri.pingpong.utils.TournamentUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class MatchService
{

    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository)
    {
        this.matchRepository = matchRepository;
    }


    public Match findMatchById(Long id)
    {
        if (id == null)
        {
            throw new IllegalArgumentException("Match ID cannot be null");
        }

        log.debug("Fetching match with ID: {}", id);
        return matchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Match with ID " + id + " not found"));
    }

    @Transactional
    public Match saveMatch(Match match)
    {
        if (match == null)
        {
            throw new IllegalArgumentException("Match cannot be null");
        }

        if (match.getTournament() == null)
        {
            throw new IllegalArgumentException("Match must be associated with a tournament");
        }

        log.debug("Saving match: {}", match);
        Match savedMatch = matchRepository.saveAndFlush(match);

        log.debug("Match saved: {}", savedMatch);
        return savedMatch;
    }


    private void saveAllMatches(List<Match> matches)
    {
        if (matches == null || matches.isEmpty())
        {
            throw new IllegalArgumentException("Match list cannot be null or empty");
        }

        log.debug("Saving all matches: {}", matches);
        matchRepository.saveAll(matches);
        log.debug("All matches saved successfully");
    }

    @Transactional
    public void deleteMatch(Match match)
    {
        if (match == null)
        {
            throw new IllegalArgumentException("Match cannot be null");
        }

        log.debug("Deleting match: {}", match);
        matchRepository.delete(match);
        log.debug("Match deleted: {}", match);
    }

    public void cleanAllFutureMatches(Match match)
    {
        if (match == null)
        {
            return;
        }

        List<Match> matchesToUpdate = new ArrayList<>();
        Match currMatch = match;

        while (currMatch != null && currMatch.getWinner() != null)
        {
            currMatch.setWinner(null);

            currMatch.getScore().clear();

            Match nextMatch = currMatch.getNextMatch();
            if (nextMatch != null)
            {
                if (currMatch.getPosition() % 2 == 1)
                {
                    nextMatch.setTopPlayer(null);
                }
                else
                {
                    nextMatch.setBottomPlayer(null);
                }
            }

            matchesToUpdate.add(currMatch);
            currMatch = nextMatch;
        }

        saveAllMatches(matchesToUpdate);
    }


    public String validateAndUpdateScores(Match match, List<Score> newMatchScores)
    {

        if (match == null)
        {
            throw new IllegalArgumentException("Match cannot be null.");
        }
        if (newMatchScores == null || newMatchScores.isEmpty())
        {
            throw new IllegalArgumentException("Invalid score list: must not be null or empty.");
        }

        StringBuilder infoMessage = new StringBuilder();
        List<Score> validScores = new ArrayList<>();

        for (Score score : newMatchScores)
        {
            String validationMessage = validateScore(score.getTopPlayerScore(), score.getBottomPlayerScore());
            if (validationMessage != null)
            {
                infoMessage.append(validationMessage).append("\n");
            }
            else
            {
                validScores.add(score);
            }
        }

        if (validScores.isEmpty())
        {
            throw new IllegalArgumentException("No valid scores provided to update.");
        }

        match.setScore(validScores);
        TournamentUtils.determinateWinnerFromScore(match);

        matchRepository.saveAndFlush(match); //throws IllegalArgumentException

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
        }
        else if (Math.max(topScore, bottomScore) == Constants.MINIMAL_POINTS_IN_SET)
        {
            return null;
        }
        else
        {
            return "Score {" + topScore + "-" + bottomScore + "} with points < 11 will not be saved!";
        }

        return null;
    }

}