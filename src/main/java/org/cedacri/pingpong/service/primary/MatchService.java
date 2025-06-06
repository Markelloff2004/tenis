package org.cedacri.pingpong.service.primary;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Score;
import org.cedacri.pingpong.entity.Tournament;
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

    @Transactional
    public Match saveMatch(Match match)
    {
        log.debug("Attempting to save or update match: {}", match);

        if (match == null)
        {
            throw new IllegalArgumentException("Cannot save a null Match");
        }

        return matchRepository.save(match);

    }

    @Transactional
    public void deleteMatch(Match match)
    {
        if (match == null || match.getId() == null)
        {
            log.error("Attempted to delete a null Match");
            throw new IllegalArgumentException("Match ID cannot be null");
        }

        Optional<Match> matchToDelete = matchRepository.findById(match.getId());

        if (matchToDelete.isPresent())
        {
            log.debug("Attempting to delete match: {}", match);
            matchRepository.deleteById(match.getId());
            log.debug("Match deleted: {}", match);
        }

    }


    public String validateAndUpdateScores(Match match, List<Score> newMatchScores)
    {

        validateMatchAndScoreList(match, newMatchScores);

        List<String> infoMessages = new ArrayList<>();
        List<Score> validScores = new ArrayList<>();

        for (Score score : newMatchScores)
        {
            String validationMessage = validateScore(score.getTopPlayerScore(), score.getBottomPlayerScore());
            if (validationMessage != null)
            {
                infoMessages.add(validationMessage);
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

        if(Objects.equals(match.getScore(), validScores))
        {
            log.debug("No new scores provided to update. Score is the same.");
            return String.join("\n", infoMessages);
        }

        match.setScore(validScores);
        TournamentUtils.determinateWinnerFromScore(match);

        matchRepository.saveAndFlush(match); //throws IllegalArgumentException

        return String.join("\n", infoMessages);
    }

    private static void validateMatchAndScoreList(Match match, List<Score> newMatchScores) {
        if (match == null)
        {
            throw new IllegalArgumentException("Match cannot be null.");
        }
        if (newMatchScores == null || newMatchScores.isEmpty())
        {
            throw new IllegalArgumentException("Invalid score list: must not be null or empty.");
        }
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

        matchRepository.saveAllAndFlush(matchesToUpdate);

    }
}