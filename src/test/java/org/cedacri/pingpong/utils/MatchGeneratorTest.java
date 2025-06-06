package org.cedacri.pingpong.utils;

import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.enums.SetTypesEnum;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
import org.cedacri.pingpong.service.primary.TournamentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MatchGeneratorTest {

    private MatchGenerator matchGenerator;
    private TournamentService tournamentService;
    private PlayerDistributer playerDistributer;
    private Tournament tournament;

    @BeforeEach
    void setUp() {
        tournamentService = mock(TournamentService.class);
        playerDistributer = mock(PlayerDistributer.class);

        matchGenerator = new MatchGenerator(
                SetTypesEnum.BEST_OF_THREE,
                SetTypesEnum.BEST_OF_FIVE,
                SetTypesEnum.BEST_OF_SEVEN,
                TournamentTypeEnum.OLYMPIC,  // Default type
                playerDistributer,
                tournamentService
        );

        tournament = mock(Tournament.class);

        when(tournament.getTournamentType()).thenReturn(TournamentTypeEnum.OLYMPIC);

        when(tournament.getPlayers()).thenReturn(new HashSet<>(List.of(
                createRealPlayer(1L,"Alice", 1000),
                createRealPlayer(2L,"Bob", 950),
                createRealPlayer(3L, "Charlie", 1100),
                createRealPlayer(4L, "Dave", 900),
                createRealPlayer(5L, "Eve", 1050),
                createRealPlayer(6L, "Frank", 980),
                createRealPlayer(7L, "Grace", 1020),
                createRealPlayer(8L, "Hank", 970)
        )));

        when(tournamentService.findTournamentById(anyInt())).thenReturn(tournament);
    }

//    @Test
//    void testGenerateMatches_Olympic() {
//
//        Tournament olympicTournament = new Tournament();
//        Set<Player> playerSet = Set.of(
//                createRealPlayer(1L, "Alice", 1000),
//                createRealPlayer(2L,"Bob", 950),
//                createRealPlayer(3L, "Charlie", 1100),
//                createRealPlayer(4L, "Dave", 900),
//                createRealPlayer(5L, "Eve", 1050),
//                createRealPlayer(6L, "Frank", 980),
//                createRealPlayer(7L, "Grace", 1020),
//                createRealPlayer(8L, "Hank", 970)
//        );
//        olympicTournament.setTournamentType(TournamentTypeEnum.OLYMPIC);
//        olympicTournament.setPlayers(playerSet);
//        olympicTournament.setMaxPlayers(TournamentUtils.calculateMaxPlayers(olympicTournament));
//
//        when(tournamentService.findTournamentById(anyInt())).thenReturn(olympicTournament);
//
//        System.out.println("Number of players: " + olympicTournament.getPlayers().size());
//        assertNotNull(olympicTournament.getPlayers(), "Players should not be null");
//        assertTrue(olympicTournament.getPlayers().size() >= 8, "Tournament must have at least 8 players");
//
//        matchGenerator.generateMatches(olympicTournament);
//
//        verify(playerDistributer).distributePlayersInFirstRound(anyInt(), eq(olympicTournament));
//        assertEquals(TournamentStatusEnum.ONGOING, olympicTournament.getTournamentStatus());
//    }



    private Player createRealPlayer(Long playerId, String name, int rating) {
        Player player = new Player();
        player.setId(playerId);
        player.setName(name);
        player.setRating(rating);
        return player;
    }


    @Test
    void testGenerateMatches_RobinRound() {
        matchGenerator.setTournamentType(TournamentTypeEnum.ROBIN_ROUND);
        matchGenerator.generateMatches(tournament);
        verify(playerDistributer).distributePlayersInRobinRound(anyList(), anyList());
    }

    @Test
    void testGenerateMatches_UnsupportedType() {
        matchGenerator.setTournamentType(null);
        assertThrows(RuntimeException.class, () -> matchGenerator.generateMatches(tournament));
    }

    @Test
    void testMinimumPlayers_OlympicTournament() {
        when(tournament.getPlayers()).thenReturn(new HashSet<>(List.of(createRealPlayer(1L, "Alice", 1000))));
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> matchGenerator.generateMatches(tournament));
        assertEquals("Number of players must be at least 8.", thrown.getMessage());
    }

//    @Test
//    void testCalculateMaxPlayers_UsingReflection() throws Exception {
//        Method method = MatchGenerator.class.getDeclaredMethod("calculateMaxPlayers", int.class);
//        method.setAccessible(true);
//        assertEquals(8, (int) method.invoke(matchGenerator, 6));
//        assertEquals(16, (int) method.invoke(matchGenerator, 10));
//        assertEquals(32, (int) method.invoke(matchGenerator, 17));
//    }

    @Test
    void testCreateMatch_UsingReflection() throws Exception {
        // Access the private method
        Method method = MatchGenerator.class.getDeclaredMethod("createMatch", int.class, int.class, Match.class, Tournament.class);
        method.setAccessible(true); // Make it accessible for testing

        // Invoke the method and capture the result
        Match match = (Match) method.invoke(matchGenerator, 2, 3, null, tournament);

        // Assertions
        assertNotNull(match, "Match should not be null");
        assertEquals(2, match.getRound(), "Match round should be 2");
        assertEquals(3, match.getPosition(), "Match position should be 3");
    }


}
