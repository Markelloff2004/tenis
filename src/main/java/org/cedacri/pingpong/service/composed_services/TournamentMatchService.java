package org.cedacri.pingpong.service.composed_services;

import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.BaseTournament;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.TournamentOlympic;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.service.MatchService;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.Constants;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class TournamentMatchService {

    private final TournamentService tournamentService;
    private final MatchService matchService;

    public TournamentMatchService(TournamentService tournamentService, MatchService matchService) {
        this.tournamentService = tournamentService;
        this.matchService = matchService;
    }

    // Add methods to handle tournament and match interactions

    public List<Match> getAllMatchesByTournament(BaseTournament baseTournament) {

        if (baseTournament == null) {
            throw new IllegalArgumentException(Constants.TOURNAMENT_CANNOT_BE_NULL);
        }

        if (baseTournament.getTournamentStatus().equals(TournamentStatusEnum.PENDING)) {
            log.warn("Tournament {} is pending, no matches available", baseTournament.getId());
            return Collections.emptyList();
        }

        log.debug("Fetching matches for tournament: {} ", baseTournament.getId());
        List<Match> matches = baseTournament.getMatches();
        if (matches != null && !matches.isEmpty()) {
            matches = matches.stream()
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(Match::getPosition)
                            .thenComparing(Match::getRound))
                    .toList();
        } else {
            log.warn("No matches found for tournament: {}", baseTournament.getId());
            matches = Collections.emptyList();
        }

        log.info("Found {} matches for tournament: {}", matches.size(), baseTournament.getId());
        return matches;
    }

    public List<Match> getMatchesByTournamentAndRound(BaseTournament baseTournament, int round) {
        log.debug("Fetching matches for tournament: {} and round: {}", baseTournament, round);

        if (baseTournament == null) {
            throw new IllegalArgumentException(Constants.TOURNAMENT_CANNOT_BE_NULL);
        }

        if (round <= 0 || round == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Invalid round number");
        }

        List<Match> matches = baseTournament.getMatches().stream().filter(m -> m.getRound() == round).toList();

        if ( matches.isEmpty()) {
            log.warn("No matches found for tournament: {} and round: {}", baseTournament, round);
            return Collections.emptyList();
        }

        log.info("Found {} matches for tournament: {} and round: {}", matches.size(), baseTournament, round);
        return matches;
    }
}
