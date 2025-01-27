package org.cedacri.pingpong.utils;

import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class PlayerDistributer {

    private static final Logger logger = LoggerFactory.getLogger(PlayerDistributer.class);

    /***
     * Distributes players into pairs based on seeding logic.
     *
     * @param players - represents list of players sorted by rating in descending order
     * @param maxPlayers - maximum number of players (nearest power of 2)
     * @return list of player pairs for matches
     */
    public List<Player[]> distributePlayers(List<Player> players, int maxPlayers) {
        List<Player[]> pairs = new ArrayList<>();
        List<Player> paddedPlayers = new ArrayList<>(players);

        while (paddedPlayers.size() < maxPlayers) {
            paddedPlayers.add(null);
        }

        for (int i = 0; i < maxPlayers / 2; i++) {
            Player topPlayer = paddedPlayers.get(i);
            Player bottomPlayer = paddedPlayers.get(maxPlayers - i - 1);

            if (topPlayer == null && bottomPlayer == null) {
                logger.warn("Skipping empty match pair: Index {}", i);
                continue;
            }

            pairs.add(new Player[]{topPlayer, bottomPlayer});
            logger.info("Generated new pair: TopPlayer={}, BottomPlayer={}",
                    topPlayer != null ? topPlayer.getName() : "null",
                    bottomPlayer != null ? bottomPlayer.getName() : "null");
        }

        return pairs;
    }

    /**
     * Creates the first round of matches based on player pairs.
     *
     * @param players - list of sorted players
     * @param maxPlayers - maximum number of players (nearest power of 2)
     * @param tournament - current tournament
     * @return list of first-round matches
     */
    public List<Match> createFirstRoundMatches(List<Player> players, int maxPlayers, Tournament tournament) {
        List<Player[]> pairs = distributePlayers(players, maxPlayers);

        return IntStream.range(0, pairs.size())
                .mapToObj(i -> {
                    Match match = new Match();
                    match.setTournament(tournament);
                    match.setRound(1);
                    match.setPosition(i + 1);
                    match.setTopPlayer(pairs.get(i)[0]);
                    match.setBottomPlayer(pairs.get(i)[1]);

                    // Automatically set winner if only one player is present
                    if (pairs.get(i)[0] != null && pairs.get(i)[1] == null) {
                        match.setWinner(pairs.get(i)[0]);
                    } else if (pairs.get(i)[0] == null && pairs.get(i)[1] != null) {
                        match.setWinner(pairs.get(i)[1]);
                    }

                    return match;
                })
                .toList();
    }
}
