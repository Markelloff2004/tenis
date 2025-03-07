package org.cedacri.pingpong.utils;

import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Score;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
import org.cedacri.pingpong.service.TournamentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.cedacri.pingpong.entity.Player$.rating;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TournamentUtilsTest
{

    private Tournament tournament;
    private Match match;

    @InjectMocks
    private TournamentService tournamentService;

    @Mock
    private TournamentUtils tournamentUtils;

    private List<Match> matches;
    private List<Score> scores;

    private Player topPlayer;
    private Player bottomPlayer;

    private Player player1;
    private Player player2;
    private Player player3;

    @BeforeEach
    void setUp()
    {
        tournament = new Tournament();
        tournament.setTournamentType(TournamentTypeEnum.OLYMPIC);
        tournament.setMaxPlayers(16);

        match = new Match();
        match.setRound(1);
        match.setTournament(tournament);

        topPlayer = createPlayer("A");
        bottomPlayer = createPlayer("B");

        tournament = new Tournament();
        tournament.setTournamentType(TournamentTypeEnum.OLYMPIC);

        match = new Match();
        match.setTopPlayer(topPlayer);
        match.setBottomPlayer(bottomPlayer);
        match.setTournament(tournament);

        scores = new ArrayList<>();
        match.setScore(scores);
    }

    @Nested
    @DisplayName("Test method calculateMaxPlayers()")
    class TestCalculateMaxPlayers
    {
        private Set<Player> getPlayers(int numPlayers) {
            Set<Player> result = new HashSet<>();

            for (int i = 0; i < numPlayers; i++) {
                Player player = new Player();
                player.setId((long) i);
                result.add(player);
            }

            return result;
        }

        @Test
        void testCalculateMaxPlayersValid()
        {
            tournament.setPlayers(getPlayers(10));

            assertEquals(16, TournamentUtils.calculateMaxPlayers(tournament));
        }

        @Test
        void testCalculateMaxPlayersPowerOfTwo()
        {
            tournament.setPlayers(getPlayers(16));

            assertEquals(16, TournamentUtils.calculateMaxPlayers(tournament));
        }

    }

    @Nested
    @DisplayName("Test method calculateNumberOfRounds()")
    class TestCalculateNumberOfRounds
    {

        @Test
        void testCalculateNumberOfRounds_ValidCases() {
            assertEquals(3, TournamentUtils.calculateNumberOfRounds(8));
            assertEquals(4, TournamentUtils.calculateNumberOfRounds(16));
            assertEquals(5, TournamentUtils.calculateNumberOfRounds(32));
        }

        @Test
        void testCalculateNumberOfRounds_InvalidCase() {
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                    TournamentUtils.calculateNumberOfRounds(5));
            assertEquals("Number of players must be at least 8.", thrown.getMessage());
        }
    }

//    @Test
//    void testGetMinimalWinsPerMatch_UsingReflection() throws Exception {
//        Method method = TournamentUtils.class.getDeclaredMethod("getMinimalWinsPerMatch", Match.class);
//        method.setAccessible(true);
//
//        int minWins = (int) method.invoke(null, match);
//
//        assertTrue(minWins > 0, "Minimal wins should be greater than 0");
//    }

    @Nested
    @DisplayName("Test method calculateNewRating()")
    class TestCalculateNewRating
    {
        @Test
        void testCalculateNewRating()
        {
            assertEquals(24, TournamentUtils.calculateNewRating(3, 1, 10, 8));
        }

    }

    @Nested
    @DisplayName("Test method isFinished()")
    class TestIsFinished
    {
        @Test
        void testFinishTournament_UsingReflection() throws Exception {
            Method method = TournamentUtils.class.getDeclaredMethod("finishTournament", Tournament.class);
            method.setAccessible(true);

            method.invoke(null, tournament);
            assertEquals(TournamentStatusEnum.FINISHED, tournament.getTournamentStatus(), "Tournament should be marked as finished.");
        }

        @Test
        void testIsFinished_WhenNoMatchesExist_ReturnsTrue() throws Exception {
            tournament.setMatches(new HashSet<>());

            boolean result = invokeIsFinished(tournament);

            assertTrue(result, "Tournament should be considered finished if there are no matches");
        }

        private boolean invokeIsFinished(Tournament tournament) throws Exception {
            Method method = TournamentUtils.class.getDeclaredMethod("isFinished", Tournament.class);
            method.setAccessible(true);
            return (boolean) method.invoke(null, tournament);
        }
    }

    private Player createPlayer(String name)
    {
        Player player = Mockito.mock(Player.class);
        player.setName(name);
        return player;
    }

    private Match createMatch()
    {
        Match match = Mockito.mock(Match.class);
        return match;
    }

    private Match createMatch(Player topPlayer, Player bottomPlayer, Player winner) {
        Match match = Mockito.mock(Match.class);
        match.setTopPlayer(topPlayer);
        match.setBottomPlayer(bottomPlayer);
        match.setWinner(winner);
        return match;
    }
}
