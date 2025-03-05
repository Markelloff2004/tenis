package org.cedacri.pingpong.utils;

import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlayerDistributerTest
{

    private PlayerDistributer playerDistributer;
    private Tournament tournament;

    @BeforeEach
    void setUp()
    {
        playerDistributer = new PlayerDistributer();
        tournament = mock(Tournament.class);
    }

    private Player createPlayer(String name, int rating)
    {
        Player player = mock(Player.class);
        when(player.getName()).thenReturn(name);
        when(player.getRating()).thenReturn(rating);
        return player;
    }

    private Match createMatch(int round, int position)
    {
        Match match = mock(Match.class);
        when(match.getRound()).thenReturn(round);
        when(match.getPosition()).thenReturn(position);
        return match;
    }

    @Nested
    @DisplayName("Test method distributePlayers()")
    class GetDistributedPlayersTest
    {

        @Test
        void distributePlayersNormally()
        {
            HashSet<Player> players = new HashSet<>(List.of(
                    createPlayer("A", 1100),
                    createPlayer("B", 900),
                    createPlayer("C", 1000),
                    createPlayer("D", 800)
            ));
            tournament.setPlayers(players);
            when(tournament.getPlayers()).thenReturn(new HashSet<>(players));

            List<Player[]> pairs = playerDistributer.distributePlayers(tournament, 4);

            assertEquals(2, pairs.size());
            assertEquals("D", pairs.get(0)[0].getName());
            assertEquals("A", pairs.get(0)[1].getName());
            assertEquals("B", pairs.get(1)[0].getName());
            assertEquals("C", pairs.get(1)[1].getName());
        }

        @Test
        void handlePaddingForInsufficientPlayers()
        {
            HashSet<Player> players = new HashSet<>(List.of(
                    createPlayer("A", 1100),
                    createPlayer("B", 900),
                    createPlayer("C", 1000)
            ));
            tournament.setPlayers(players);
            when(tournament.getPlayers()).thenReturn(new HashSet<>(players));

            List<Player[]> pairs = playerDistributer.distributePlayers(tournament, 4);

            assertEquals(2, pairs.size());
            assertEquals("B", pairs.get(0)[0].getName());
            assertEquals("C", pairs.get(1)[0].getName());
            assertNull(pairs.get(0)[1]);
        }

        @Test
        void handleZeroPlayers()
        {
            tournament.setPlayers(new HashSet<>());

            List<Player[]> pairs = playerDistributer.distributePlayers(tournament, 2);

            assertTrue(pairs.isEmpty());
        }

        @Test
        void handleNullTournament()
        {
            assertThrows(NullPointerException.class, () -> playerDistributer.distributePlayers(null, 2));
        }

        @Test
        void handleUnevenPlayerCountWithPadding()
        {
            HashSet<Player> players = new HashSet<>(List.of(
                    createPlayer("A", 1100),
                    createPlayer("B", 900),
                    createPlayer("C", 1000)
            ));
            tournament.setPlayers(players);
            when(tournament.getPlayers()).thenReturn(new HashSet<>(players));

            List<Player[]> pairs = playerDistributer.distributePlayers(tournament, 4);

            assertEquals(2, pairs.size());
            assertNotNull(pairs.get(0)[0]);
            assertNull(pairs.get(0)[1]);
            assertNotNull(pairs.get(1)[0]);
            assertNotNull(pairs.get(1)[1]);
        }

        @Test
        void handleSinglePlayerWithPadding()
        {
            HashSet<Player> players = new HashSet<>(List.of(createPlayer("A", 1100)));
            tournament.setPlayers(players);
            when(tournament.getPlayers()).thenReturn(new HashSet<>(players));

            List<Player[]> pairs = playerDistributer.distributePlayers(tournament, 2);

            assertEquals(1, pairs.size());
            assertEquals("A", pairs.get(0)[0].getName());
            assertNull(pairs.get(0)[1]);
        }

        @Test
        void testDistributePlayersEvenCount()
        {
            List<Player> players = List.of(
                    createPlayer("Alice", 1000),
                    createPlayer("Bob", 900),
                    createPlayer("Charlie", 1100),
                    createPlayer("Dave", 950)
            );
            when(tournament.getPlayers()).thenReturn(new HashSet<>(players));

            List<Player[]> pairs = playerDistributer.distributePlayers(tournament, 4);

            assertEquals(2, pairs.size());
            assertEquals("Bob", pairs.get(0)[0].getName());
            assertEquals("Charlie", pairs.get(0)[1].getName());
            assertEquals("Dave", pairs.get(1)[0].getName());
            assertEquals("Alice", pairs.get(1)[1].getName());
        }

        @Test
        void testDistributePlayersWithPadding()
        {
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
        void testDistributePlayersEmptyTournament()
        {
            when(tournament.getPlayers()).thenReturn(new HashSet<>());

            List<Player[]> pairs = playerDistributer.distributePlayers(tournament, 4);

            assertEquals(0, pairs.size());
        }

        @Test
        void testDistributePlayersSinglePlayer()
        {
            List<Player> players = List.of(createPlayer("Alice", 1000));
            when(tournament.getPlayers()).thenReturn(new HashSet<>(players));

            List<Player[]> pairs = playerDistributer.distributePlayers(tournament, 2);

            assertEquals(1, pairs.size());
            assertEquals("Alice", pairs.get(0)[0].getName());
            assertNull(pairs.get(0)[1]);
        }

        @Test
        void testDistributePlayersMoreThanMaxPlayers()
        {
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
            assertEquals("Bob", pairs.get(0)[0].getName());
            assertEquals("Charlie", pairs.get(0)[1].getName());
            assertEquals("Dave", pairs.get(1)[0].getName());
            assertEquals("Alice", pairs.get(1)[1].getName());
        }

    }

    @Nested
    @DisplayName("Test method distributePlayersInFirstRound()")
    class DistributePlayersInFirstRound
    {

        @Test
        void testDistributePlayersInFirstRound()
        {
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
        void testDistributePlayersInFirstRoundWithMismatch()
        {
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
        void testValidFirstRoundPairing()
        {
            List<Player> players = List.of(
                    createPlayer("Alice", 800),
                    createPlayer("Bob", 900),
                    createPlayer("Charlie", 1000),
                    createPlayer("Dave", 1100)
            );
            List<Match> matches = List.of(
                    createMatch(1, 1),
                    createMatch(1, 2));

            when(tournament.getPlayers()).thenReturn(new HashSet<>(players));
            when(tournament.getMatches()).thenReturn(new HashSet<>(matches));

            assertDoesNotThrow(() -> playerDistributer.distributePlayersInFirstRound(4, tournament));

            ArgumentCaptor<Player> topPlayerCaptor = ArgumentCaptor.forClass(Player.class);
            ArgumentCaptor<Player> bottomPlayerCaptor = ArgumentCaptor.forClass(Player.class);

            verify(matches.get(0)).setTopPlayer(topPlayerCaptor.capture());
            verify(matches.get(0)).setBottomPlayer(bottomPlayerCaptor.capture());
            verify(matches.get(1)).setTopPlayer(topPlayerCaptor.capture());
            verify(matches.get(1)).setBottomPlayer(bottomPlayerCaptor.capture());

            List<Player> topPlayers = topPlayerCaptor.getAllValues();
            List<Player> bottomPlayers = bottomPlayerCaptor.getAllValues();

            assertEquals("Alice", topPlayers.get(0).getName());
            assertEquals("Dave", bottomPlayers.get(0).getName());
            assertEquals("Bob", topPlayers.get(1).getName());
            assertEquals("Charlie", bottomPlayers.get(1).getName());
        }


        @Test
        void testMismatchBetweenMatchesAndPairs()
        {
            List<Player> players = List.of(
                    createPlayer("Alice", 800),
                    createPlayer("Bob", 900),
                    createPlayer("Charlie", 1000),
                    createPlayer("Dave", 1100));
            List<Match> matches = List.of(createMatch(1, 1));

            when(tournament.getPlayers()).thenReturn(new HashSet<>(players));
            when(tournament.getMatches()).thenReturn(new HashSet<>(matches));

            assertThrows(IllegalStateException.class, () ->
                    playerDistributer.distributePlayersInFirstRound(4, tournament)
            );
        }

        @Test
        void testSinglePlayerWithPadding()
        {
            List<Player> players = List.of(createPlayer("Alice", 1000));
            List<Match> matches = List.of(createMatch(1, 1));

            when(tournament.getPlayers()).thenReturn(new HashSet<>(players));
            when(tournament.getMatches()).thenReturn(new HashSet<>(matches));

            assertDoesNotThrow(() -> playerDistributer.distributePlayersInFirstRound(2, tournament));

            verify(matches.get(0)).setTopPlayer(players.get(0));
            verify(matches.get(0)).setBottomPlayer(null);
            verify(matches.get(0)).setWinner(players.get(0));
        }

        @Test
        void testNullTournament()
        {
            assertThrows(NullPointerException.class, () ->
                    playerDistributer.distributePlayersInFirstRound(4, null)
            );
        }

        @Test
        void testNoFirstRoundMatches()
        {
            List<Player> players = List.of(createPlayer("Alice", 1000), createPlayer("Bob", 1100));
            when(tournament.getPlayers()).thenReturn(new HashSet<>(players));
            when(tournament.getMatches()).thenReturn(new HashSet<>());

            assertThrows(IllegalStateException.class, () ->
                    playerDistributer.distributePlayersInFirstRound(4, tournament)
            );
        }

        @Test
        void testEmptyPlayerList()
        {
            List<Match> matches = List.of(
                    createMatch(1, 1),
                    createMatch(1, 2));
            when(tournament.getPlayers()).thenReturn(new HashSet<>());
            when(tournament.getMatches()).thenReturn(new HashSet<>(matches));

            assertThrows(IllegalStateException.class, () ->
                    playerDistributer.distributePlayersInFirstRound(4, tournament)
            );

            for (Match match : matches)
            {
                verify(match, never()).setTopPlayer(any());
                verify(match, never()).setBottomPlayer(any());
            }
        }

//        @Test
//        void testNullPlayersInPairs() {
//            List<Player> players = List.of(
//                    createPlayer("Alice", 1000),
//                    null,
//                    createPlayer("Charlie", 1100),
//                    null
//            );
//            List<Match> matches = List.of(createMatch(1, 1), createMatch(1, 2));
//
//            when(tournament.getPlayers()).thenReturn(new HashSet<>(players));
//            when(tournament.getMatches()).thenReturn(new HashSet<>(matches));
//
//            assertDoesNotThrow(() -> playerDistributer.distributePlayersInFirstRound(4, tournament));
//
//            verify(matches.get(0)).setTopPlayer(players.get(0));
//            verify(matches.get(0)).setBottomPlayer(null);
//            verify(matches.get(0)).setWinner(players.get(0));
//
//            verify(matches.get(1)).setTopPlayer(players.get(2));
//            verify(matches.get(1)).setBottomPlayer(null);
//            verify(matches.get(1)).setWinner(players.get(2));
//        }

    }

    @Nested
    @DisplayName("Test method distributePlayersInRobinRound()")
    class DistributePlayersInRobinRound
    {
        @Test
        void testDistributePlayersInRobinRound()
        {
            List<Player> players = List.of(
                    createPlayer("Alice", 1000),
                    createPlayer("Bob", 900),
                    createPlayer("Charlie", 1100),
                    createPlayer("Dave", 950)
            );
            List<Match> matches = new ArrayList<>();
            for (int i = 0; i < 6; i++)
            {
                matches.add(mock(Match.class));
            }

            playerDistributer.distributePlayersInRobinRound(matches, players);

            for (Match match : matches)
            {
                verify(match).setTopPlayer(any());
                verify(match).setBottomPlayer(any());
            }
        }

        @Test
        void testDistributePlayersInRobinRoundWithInsufficientMatches()
        {
            List<Player> players = List.of(
                    createPlayer("Alice", 1000),
                    createPlayer("Bob", 900),
                    createPlayer("Charlie", 1100),
                    createPlayer("Dave", 950)
            );
            List<Match> matches = new ArrayList<>();
            for (int i = 0; i < 4; i++)
            {
                matches.add(mock(Match.class));
            }

            playerDistributer.distributePlayersInRobinRound(matches, players);

            assertEquals(4, matches.size());
        }

        @Test
        void testDistributePlayersInRobinRoundEmptyPlayers()
        {
            List<Player> players = List.of();
            List<Match> matches = new ArrayList<>();

            assertDoesNotThrow(() -> playerDistributer.distributePlayersInRobinRound(matches, players));
        }

        @Test
        void testDistributePlayersInRobinRoundInsufficientMatches()
        {
            List<Player> players = List.of(
                    createPlayer("Alice", 1100),
                    createPlayer("Bob", 1000),
                    createPlayer("Charlie", 900),
                    createPlayer("Dave", 800)
            );
            List<Match> matches = new ArrayList<>();
            for (int i = 0; i < 4; i++)
            {
                matches.add(createMatch(1, 0));
            }

            playerDistributer.distributePlayersInRobinRound(matches, players);

            assertEquals(4, matches.size());
            verify(matches.get(0)).setTopPlayer(players.get(0));
            verify(matches.get(0)).setBottomPlayer(players.get(1));
            verify(matches.get(1)).setTopPlayer(players.get(0));
            verify(matches.get(1)).setBottomPlayer(players.get(2));
            verify(matches.get(2)).setTopPlayer(players.get(0));
            verify(matches.get(2)).setBottomPlayer(players.get(3));
            verify(matches.get(3)).setTopPlayer(players.get(1));
            verify(matches.get(3)).setBottomPlayer(players.get(2));
        }

        @Test
        void testDistributePlayersInRobinRoundNoPlayers()
        {
            List<Player> players = new ArrayList<>();
            List<Match> matches = new ArrayList<>();
            for (int i = 0; i < 6; i++)
            {
                matches.add(createMatch(1, 0));
            }

            playerDistributer.distributePlayersInRobinRound(matches, players);

            for (Match match : matches)
            {
                verify(match, never()).setTopPlayer(any());
                verify(match, never()).setBottomPlayer(any());
            }
        }

        @Test
        void testDistributePlayersInRobinRoundNullPlayers()
        {
            List<Match> matches = new ArrayList<>();
            for (int i = 0; i < 6; i++)
            {
                matches.add(createMatch(1, 0));
            }

            assertThrows(NullPointerException.class, () ->
                    playerDistributer.distributePlayersInRobinRound(matches, null)
            );
        }

        @Test
        void testDistributePlayersInRobinRoundNullMatches()
        {
            List<Player> players = List.of(
                    createPlayer("Alice", 1000),
                    createPlayer("Bob", 800)
            );

            assertThrows(NullPointerException.class, () ->
                    playerDistributer.distributePlayersInRobinRound(null, players)
            );
        }

    }
}
