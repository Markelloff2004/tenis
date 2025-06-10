package org.cedacri.pingpong.service.composed_services;

import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.BaseTournament;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.TournamentOlympic;
import org.cedacri.pingpong.service.MatchService;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
public class PlayerMatchService {

    private final PlayerService playerService;
    private final MatchService matchService;

    public PlayerMatchService(PlayerService playerService, MatchService matchService) {
        this.playerService = playerService;
        this.matchService = matchService;
    }

    public List<Match> getMatchesByPlayerNameSurname(List<Match> matches, String playerName, String playerSurname)
    {
        log.info("Search for match witch Player '{}' '{}'", playerName, playerSurname);

        if (matches == null || matches.isEmpty())
        {
            log.warn("No matches provided for player: {} {}", playerName, playerSurname);
            return Collections.emptyList();
        }

        if (playerName == null || playerSurname == null)
        {
            throw new IllegalArgumentException("Player Name and Player Surname cannot be null");
        }

        if (playerName.isBlank() || playerSurname.isBlank())
        {
            throw new IllegalArgumentException("Player Name and Surname cannot be blank");
        }

        List<Match> matchesFromTournament = matches.stream()
                .filter(Objects::nonNull)
                .toList();
        if (matchesFromTournament.isEmpty())
        {
            log.warn("No matches found for player: {} {}", playerName, playerSurname);
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

}
