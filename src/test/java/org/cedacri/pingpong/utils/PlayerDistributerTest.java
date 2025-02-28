package org.cedacri.pingpong.utils;

import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerDistributerTest {

    private PlayerDistributer playerDistributer;
    private Tournament tournament;

    @BeforeEach
    void setUp() {
        playerDistributer = new PlayerDistributer();
        tournament = mock(Tournament.class);
    }

    private Player createPlayer(String name, int rating) {
        Player player = mock(Player.class);
        when(player.getName()).thenReturn(name);
        when(player.getRating()).thenReturn(rating);
        return player;
    }

    private Match createMatch(int round, int position) {
        Match match = mock(Match.class);
        when(match.getRound()).thenReturn(round);
        when(match.getPosition()).thenReturn(position);
        return match;
    }

    @Test
    void testDistributePlayersEvenCount() {
        List<Player> players = List.of(
                createPlayer("Alice", 1000),
                createPlayer("Bob", 900),
                createPlayer("Charlie", 1100),
                createPlayer("Dave", 950)
        );
        when(tournament.getPlayers()).thenReturn(new HashSet<>(players));

        List<Player[]> pairs = playerDistributer.distributePlayers(tournament, 4);

        assertEquals(2, pairs.size());
        assertEquals("Bob", pairs.get(0)[0].getName()); // Lowest-rated player
        assertEquals("Charlie", pairs.get(0)[1].getName()); // Highest-rated player
        assertEquals("Dave", pairs.get(1)[0].getName()); // Second lowest
        assertEquals("Alice", pairs.get(1)[1].getName()); // Second highest
    }


    @Test
    void testDistributePlayersWithPadding() {
        List<Player> players = List.of(
                createPlayer("Alice", 1000),
                createPlayer("Bob", 900),
                createPlayer("Charlie", 1100)
        );
        when(tournament.getPlayers()).thenReturn(new HashSet<>(players));

        List<Player[]> pairs = playerDistributer.distributePlayers(tournament, 4);

        assertEquals(2, pairs.size());
        assertEquals("Bob", pairs.get(0)[0].getName());
        assertNull(pairs.get(0)[1]);
        assertEquals("Alice", pairs.get(1)[0].getName());
        assertEquals("Charlie", pairs.get(1)[1].getName());
    }

    @Test
    void testDistributePlayersEmptyTournament() {
        when(tournament.getPlayers()).thenReturn(new HashSet<>());

        List<Player[]> pairs = playerDistributer.distributePlayers(tournament, 4);

        assertEquals(0, pairs.size()); // Expecting an empty list instead of 2 null pairs
    }


    @Test
    void testDistributePlayersSinglePlayer() {
        List<Player> players = List.of(createPlayer("Alice", 1000));
        when(tournament.getPlayers()).thenReturn(new HashSet<>(players));

        List<Player[]> pairs = playerDistributer.distributePlayers(tournament, 2);

        assertEquals(1, pairs.size());
        assertEquals("Alice", pairs.get(0)[0].getName());
        assertNull(pairs.get(0)[1]);
    }

    @Test
    void testDistributePlayersMoreThanMaxPlayers() {
        List<Player> players = List.of(
                createPlayer("Alice", 1000),
                createPlayer("Bob", 900),
                createPlayer("Charlie", 1100),
                createPlayer("Dave", 950),
                createPlayer("Eve", 1200)
        );
        when(tournament.getPlayers()).thenReturn(new HashSet<>(players));

        List<Player[]> pairs = playerDistributer.distributePlayers(tournament, 4);

        assertEquals(2, pairs.size());
        assertEquals("Bob", pairs.get(0)[0].getName()); // Lowest-rated player
        assertEquals("Charlie", pairs.get(0)[1].getName()); // Highest-rated player
        assertEquals("Dave", pairs.get(1)[0].getName()); // Second lowest
        assertEquals("Alice", pairs.get(1)[1].getName()); // Second highest
    }


    @Test
    void testDistributePlayersInFirstRound() {
        List<Player> players = List.of(
                createPlayer("Alice", 1000),
                createPlayer("Bob", 900),
                createPlayer("Charlie", 1100),
                createPlayer("Dave", 950)
        );
        List<Match> matches = List.of(
                createMatch(1, 1),
                createMatch(1, 2)
        );

        when(tournament.getPlayers()).thenReturn(new HashSet<>(players));
        when(tournament.getMatches()).thenReturn(new HashSet<>(matches));

        assertDoesNotThrow(() -> playerDistributer.distributePlayersInFirstRound(4, tournament));

        verify(matches.get(0)).setTopPlayer(any());
        verify(matches.get(0)).setBottomPlayer(any());
        verify(matches.get(1)).setTopPlayer(any());
        verify(matches.get(1)).setBottomPlayer(any());
    }

    @Test
    void testDistributePlayersInFirstRoundWithMismatch() {
        List<Player> players = List.of(
                createPlayer("Alice", 1000),
                createPlayer("Bob", 900),
                createPlayer("Charlie", 1100),
                createPlayer("Dave", 950)
        );
        List<Match> matches = List.of(createMatch(1, 1));

        when(tournament.getPlayers()).thenReturn(new HashSet<>(players));
        when(tournament.getMatches()).thenReturn(new HashSet<>(matches));

        assertThrows(IllegalStateException.class, () ->
                playerDistributer.distributePlayersInFirstRound(4, tournament));
    }

    @Test
    void testDistributePlayersInRobinRound() {
        List<Player> players = List.of(
                createPlayer("Alice", 1000),
                createPlayer("Bob", 900),
                createPlayer("Charlie", 1100),
                createPlayer("Dave", 950)
        );
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            matches.add(mock(Match.class));
        }

        playerDistributer.distributePlayersInRobinRound(matches, players);

        for (Match match : matches) {
            verify(match).setTopPlayer(any());
            verify(match).setBottomPlayer(any());
        }
    }

    @Test
    void testDistributePlayersInRobinRoundWithInsufficientMatches() {
        List<Player> players = List.of(
                createPlayer("Alice", 1000),
                createPlayer("Bob", 900),
                createPlayer("Charlie", 1100),
                createPlayer("Dave", 950)
        );
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            matches.add(mock(Match.class));
        }

        playerDistributer.distributePlayersInRobinRound(matches, players);

        assertEquals(4, matches.size());
    }

    @Test
    void testDistributePlayersInRobinRoundEmptyPlayers() {
        List<Player> players = List.of();
        List<Match> matches = new ArrayList<>();

        assertDoesNotThrow(() -> playerDistributer.distributePlayersInRobinRound(matches, players));
    }
}
