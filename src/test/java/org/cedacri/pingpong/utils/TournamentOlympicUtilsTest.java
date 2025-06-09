package org.cedacri.pingpong.utils;

import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Score;
import org.cedacri.pingpong.entity.TournamentOlympic;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
import org.cedacri.pingpong.service.TournamentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TournamentOlympicUtilsTest
{

    private TournamentOlympic tournamentOlympic;
    private Match match;

    @InjectMocks
    private TournamentService tournamentService;

    @Mock
    private TournamentUtils tournamentUtils;

    private List<Score> scores;

    private Player topPlayer;
    private Player bottomPlayer;

    @BeforeEach
    void setUp()
    {
        tournamentOlympic = new TournamentOlympic();
        tournamentOlympic.setTournamentType(TournamentTypeEnum.OLYMPIC);
        tournamentOlympic.setMaxPlayers(16);

        match = new Match();
        match.setRound(1);
        match.setTournament(tournamentOlympic);

        topPlayer = createPlayer("A");
        bottomPlayer = createPlayer("B");

        tournamentOlympic = new TournamentOlympic();
        tournamentOlympic.setTournamentType(TournamentTypeEnum.OLYMPIC);

        match = new Match();
        match.setTopPlayer(topPlayer);
        match.setBottomPlayer(bottomPlayer);
        match.setTournament(tournamentOlympic);

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
            tournamentOlympic.setPlayers(getPlayers(10));

            assertEquals(16, TournamentUtils.calculateMaxPlayers(tournamentOlympic));
        }

        @Test
        void testCalculateMaxPlayersPowerOfTwo()
        {
            tournamentOlympic.setPlayers(getPlayers(16));

            assertEquals(16, TournamentUtils.calculateMaxPlayers(tournamentOlympic));
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

    private Player createPlayer(String name)
    {
        Player player = Mockito.mock(Player.class);
        player.setName(name);
        return player;
    }
}
