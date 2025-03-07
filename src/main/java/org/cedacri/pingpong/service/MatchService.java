package org.cedacri.pingpong.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Score;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.repository.MatchRepository;
import org.cedacri.pingpong.utils.Constants;
import org.cedacri.pingpong.utils.TournamentUtils;
import org.springframework.stereotype.Service;

import java.util.*;


@Slf4j
@Service
public class MatchService {

    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public List<Match> getMatchesByTournamentAndRound(Tournament tournament, int round) {
        log.debug("Fetching matches for tournament: {} and round: {}", tournament, round);

        if (tournament == null) {
            throw new IllegalArgumentException(Constants.TOURNAMENT_CANNOT_BE_NULL);
        }

        if (round <= 0 || round == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Invalid round number");
        }

        List<Match> matches = matchRepository.findByTournamentAndRound(tournament, round);

        if (matches != null) {
            matches = matches
                    .stream()
                    .distinct()
                    .sorted(Comparator.comparing(Match::getPosition)).toList();
        } else {
            matches = Collections.emptyList();
        }

        log.info("Found {} matches for tournament: {} and round: {}", matches.size(), tournament, round);
        return matches;
    }

    public List<Match> getMatchesByTournament(Tournament tournament) {

        if (tournament == null) {
            throw new IllegalArgumentException(Constants.TOURNAMENT_CANNOT_BE_NULL);
        }

        log.debug("Fetching matches for tournament: {} ", tournament.getId());
        List<Match> matches = matchRepository.findByTournament(tournament);

        if (matches != null) {
            matches = matches
                    .stream()
                    .distinct()
                    .sorted(Comparator.comparing(Match::getPosition)
                            .thenComparing(Match::getRound))
                    .toList();
        } else {
            matches = Collections.emptyList();
        }


        log.info("Found {} matches for tournament: {}", matches.size(), tournament.getId());
        return matches;
    }

    public List<Match> getMatchesByPlayerNameSurname(Tournament tournament, String playerName, String playerSurname) {
        log.info("Search for match witch Player '{}' '{}'", playerName, playerSurname);

        if (tournament == null) {
            throw new IllegalArgumentException(Constants.TOURNAMENT_CANNOT_BE_NULL);
        }

        if (playerName == null) {
            throw new IllegalArgumentException("Player Name cannot be null");
        }

        if (playerSurname == null) {
            throw new IllegalArgumentException(Constants.TOURNAMENT_CANNOT_BE_NULL);
        }

        List<Match> matchesFromTournament = matchRepository.findByTournament(tournament);

        if (matchesFromTournament == null || matchesFromTournament.isEmpty()) {
            return Collections.emptyList();
        }

        List<Match> matchesWhereIsTopPlayer = matchesFromTournament.stream()
                .filter(m -> (Objects.nonNull(m.getTopPlayer())
                                && (m.getTopPlayer().getName().equals(playerName)
                                && m.getTopPlayer().getSurname().equals(playerSurname))
                        )
                ).toList();

        List<Match> matchesWhereIsBottomPlayer = matchesFromTournament.stream()
                .filter(m -> (Objects.nonNull(m.getTopPlayer())
                                && (m.getBottomPlayer().getName().equals(playerName)
                                && m.getBottomPlayer().getSurname().equals(playerSurname))
                        )
                ).toList();

        List<Match> allMatches = new ArrayList<>();
        allMatches.addAll(matchesWhereIsTopPlayer);
        allMatches.addAll(matchesWhereIsBottomPlayer);

        return allMatches;
    }

    @Transactional
    public Match saveMatch(Match match) {
        log.debug("Attempting to save or update match: {}", match);

        if (match == null) {
            throw new IllegalArgumentException("Cannot save a null Match");
        }

        return matchRepository.save(match);

    }

    @Transactional
    public void deleteMatch(Match match) {
        if (match == null || match.getId() == null) {
            log.error("Attempted to delete a null Match");
            throw new IllegalArgumentException("Match ID cannot be null");
        }

        Optional<Match> matchToDelete = matchRepository.findById(match.getId());

        if (matchToDelete.isPresent()) {
            log.debug("Attempting to delete match: {}", match);
            matchRepository.deleteById(match.getId());
            log.debug("Match deleted: {}", match);
        }

    }


    public String validateAndUpdateScores(Match match, List<Score> newMatchScores) {

        if (match == null) {
            throw new IllegalArgumentException("Match cannot be null.");
        }
        if (newMatchScores == null || newMatchScores.isEmpty()) {
            throw new IllegalArgumentException("Invalid score list: must not be null or empty.");
        }

        StringBuilder infoMessage = new StringBuilder();
        List<Score> validScores = new ArrayList<>();

        for (Score score : newMatchScores) {
            String validationMessage = validateScore(score.getTopPlayerScore(), score.getBottomPlayerScore());
            if (validationMessage != null) {
                infoMessage.append(validationMessage).append("\n");
            } else {
                validScores.add(score);
            }
        }

        if (validScores.isEmpty()) {
            throw new IllegalArgumentException("No valid scores provided to update.");
        }

        match.setScore(validScores);
        TournamentUtils.determinateWinner(match);

        matchRepository.saveAndFlush(match); //throws IllegalArgumentException

        return infoMessage.toString();
    }

    private String validateScore(int topScore, int bottomScore) {
        if (Math.min(topScore, bottomScore) >= Constants.MINIMAL_POINTS_IN_SET - 1) {
            if (Math.abs(topScore - bottomScore) != Constants.MINIMAL_DIFFERENCE_OF_POINTS_IN_SET) {
                return "The difference between the scores {" + topScore + "-" + bottomScore + "} should be 2 points.";
            }
        } else if (Math.max(topScore, bottomScore) == Constants.MINIMAL_POINTS_IN_SET) {
            return null;
        } else {
            return "Score {" + topScore + "-" + bottomScore + "} with points < 11 will not be saved!";
        }

        return null;
    }

}