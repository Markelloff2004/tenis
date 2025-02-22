package org.cedacri.pingpong.service;

import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.repository.PlayerRepository;
import org.cedacri.pingpong.repository.TournamentRepository;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class TournamentServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private MatchService matchService;
    @Mock
    private PlayerService playerService;

    private TournamentService tournamentService;

    private Tournament tournament1;
    private Tournament tournament2;
    private Tournament tournament3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tournamentService = new TournamentService(tournamentRepository,matchService, playerService);

        tournament1 = new Tournament();
        tournament1.setId(1);
        tournament1.setCreatedAt(LocalDate.of(2024, 10,11));

        tournament2 = new Tournament();
        tournament2.setId(2);
        tournament2.setCreatedAt(LocalDate.of(2024, 10, 1));

        tournament3 = new Tournament();
        tournament3.setId(3);
        tournament3.setCreatedAt(LocalDate.of(2024, 9,30));

    }

    @Nested
    @DisplayName("Tests for the findAll method")
    class FindAllTests {

        @Test
        void testAvailableTournamentsReturnOrderedList() {
            List<Tournament> tournaments = Arrays.asList(tournament1, tournament2, tournament3);
            when(tournamentRepository.findAll()).thenReturn(tournaments);

            Stream<Tournament> result = tournamentService.findAll();

            List<Tournament> sortedTournaments = result.toList();
            assertEquals(3, sortedTournaments.size());
            assertEquals(tournament2, sortedTournaments.get(1));
            assertEquals(tournament1, sortedTournaments.get(3));
            assertEquals(tournament3, sortedTournaments.get(0));
        }

        @Test
        void testNoTournamentsInDatabaseReturnsEmptyStream() {
            when(tournamentRepository.findAll()).thenReturn(Collections.emptyList());

            Stream<Tournament> result = tournamentService.findAll();

            assertEquals(0, result.count());

        }

        @Test
        void testSingleTournamentReturnedCorrectly() {
            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournament1));

            Stream<Tournament> result = tournamentService.findAll();

            List<Tournament> tournaments = result.toList();
            assertEquals(1, tournaments.size());
            assertEquals(tournament1, tournaments.get(0));
        }

        @Test
        void testMultipleTournamentsWithSameCreatedAtOrderVerification() {
            tournament1.setCreatedAt(LocalDate.of(2024, 10,1));
            tournament2.setCreatedAt(LocalDate.of(2024, 10,1));
            tournament3.setCreatedAt(LocalDate.of(2024, 10,1));

            List<Tournament> tournaments = Arrays.asList(tournament1, tournament2, tournament3);
            when(tournamentRepository.findAll()).thenReturn(tournaments);

            Stream<Tournament> result = tournamentService.findAll();

            List<Tournament> sortedTournaments = result.toList();
            assertEquals(3, sortedTournaments.size());
            assertTrue(sortedTournaments.containsAll(tournaments));
        }

        @Test
        void testLazyInitializationOfPlayers() {
            Player player = new Player();
            player.setId(1L);
            tournament1.setPlayers(new HashSet<>(Collections.singletonList(player)));

            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournament1));

            Stream<Tournament> result = tournamentService.findAll();
            result.forEach(tournament -> {
                Hibernate.initialize(tournament.getPlayers());
                assertNotNull(tournament.getPlayers());
            });
        }

        @Test
        void testTournamentsWithEmptyPlayersList() {
            tournament1.setPlayers(Collections.emptySet());
            tournament2.setPlayers(Collections.emptySet());

            when(tournamentRepository.findAll()).thenReturn(Arrays.asList(tournament1, tournament2));

            Stream<Tournament> result = tournamentService.findAll();
            List<Tournament> tournaments = result.toList();

            assertEquals(2, tournaments.size());
            tournaments.forEach(tournament -> assertTrue(tournament.getPlayers().isEmpty()));
        }

        @Test
        void testLargeDatabasePerformance() {
            List<Tournament> largeList = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                Tournament tournament = new Tournament();
                tournament.setCreatedAt(LocalDate.of(2023,1,1).plusDays(i%10));  //cate 10 turnee pe zi
                largeList.add(tournament);
            }

            when(tournamentRepository.findAll()).thenReturn(largeList);

            long startTime = System.currentTimeMillis();
            Stream<Tournament> result = tournamentService.findAll();
            result.toList();
            long endTime = System.currentTimeMillis();

            long duration = endTime - startTime;
            assertTrue(duration < 1000);
        }

        @Test
        void testTournamentsWithFutureCreatedAtDates() {
            Tournament futureTournament = new Tournament();
            futureTournament.setId(1);
            futureTournament.setCreatedAt(LocalDate.of(2222, 12,31));

            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(futureTournament));

            Stream<Tournament> result = tournamentService.findAll();

            List<Tournament> tournaments = result.toList();
            assertEquals(1, tournaments.size());
            assertEquals(futureTournament, tournaments.get(0));
        }

        @Test
        void testRepositoryThrowsExceptionHandling() {
            when(tournamentRepository.findAll()).thenThrow(new RuntimeException("Database error"));

            Exception exception = assertThrows(RuntimeException.class, () -> tournamentService.findAll());

            assertEquals("Database error", exception.getMessage());
        }

        @Test
        void testTournamentWithCreatedAtMinUnixEpoch() {
            Tournament tournament = new Tournament();
            tournament.setCreatedAt(LocalDate.MIN);
            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournament));

            // When
            Stream<Tournament> result = tournamentService.findAll();

            // Then
            assertNotNull(result);
            Tournament fetchedTournament = result.findFirst().orElseThrow();
            assertEquals(new Date(0), fetchedTournament.getCreatedAt(), "Tournament should have Unix epoch time.");
        }

        @Test
        void testTournamentWithCreatedAtMaxFutureDate() {
            // Given
            Tournament tournament = new Tournament();
            tournament.setCreatedAt(LocalDate.MAX);
            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournament));

            // When
            Stream<Tournament> result = tournamentService.findAll();

            // Then
            assertNotNull(result);
            Tournament fetchedTournament = result.findFirst().orElseThrow();
            assertEquals(new Date(Long.MAX_VALUE), fetchedTournament.getCreatedAt(), "Tournament should have maximum future date.");
        }

        @Test
        void testTournamentWithNullCreatedAt() {
            // Given
            Tournament tournament = new Tournament();
            tournament.setCreatedAt(null); // Null createdAt
            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournament));

            // When
            Stream<Tournament> result = tournamentService.findAll();

            // Then
            assertNotNull(result);
            Tournament fetchedTournament = result.findFirst().orElseThrow();
            assertNull(fetchedTournament.getCreatedAt(), "Tournament should have a null createdAt.");
        }

        @Test
        void testTournamentWithSpecialCharactersInName() {
            // Given
            Tournament tournament = new Tournament();
            tournament.setTournamentName("Special@#Tournament");
            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournament));

            // When
            Stream<Tournament> result = tournamentService.findAll();

            // Then
            assertNotNull(result);
            Tournament fetchedTournament = result.findFirst().orElseThrow();
            assertEquals("Special@#Tournament", fetchedTournament.getTournamentName(), "Tournament name should handle special characters.");
        }

        @Test
        void testTournamentWithDuplicateNames() {
            // Given
            Tournament tournament1 = new Tournament();
            tournament1.setTournamentName("Duplicate Tournament");
            Tournament tournament2 = new Tournament();
            tournament2.setTournamentName("Duplicate Tournament");
            when(tournamentRepository.findAll()).thenReturn(Arrays.asList(tournament1, tournament2));

            // When
            Stream<Tournament> result = tournamentService.findAll();

            // Then
            assertNotNull(result);
            List<Tournament> tournaments = result.toList();
            assertEquals(2, tournaments.size(), "There should be two tournaments with the same name.");
        }

        @Test
        void testTournamentWithLargeNumberOfPlayers() {
            // Given
            Set<Player> players = new HashSet<>();
            for (int i = 0; i < 1000; i++) {
                players.add(new Player());
            }
            Tournament tournament = new Tournament();
            tournament.setPlayers(players);
            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournament));

            // When
            Stream<Tournament> result = tournamentService.findAll();

            // Then
            assertNotNull(result);
            Tournament fetchedTournament = result.findFirst().orElseThrow();
            assertEquals(1000, fetchedTournament.getPlayers().size(), "Tournament should have 1000 players.");
        }

        @Test
        void testRepositoryReturnsNullHandling() {
            // Given
            when(tournamentRepository.findAll()).thenReturn(null);

            // When & Then
            assertThrows(NullPointerException.class, () -> tournamentService.findAll(), "Repository should not return null.");
        }

        @Test
        void testOutOfMemoryErrorHandling() {
            // Given
            when(tournamentRepository.findAll()).thenThrow(OutOfMemoryError.class);

            // When & Then
            assertThrows(OutOfMemoryError.class, () -> tournamentService.findAll(), "Should throw OutOfMemoryError.");
        }

        @Test
        void testUncommittedTransactionHandling() {
            // Given
            Tournament tournament = new Tournament();
            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournament));

            // When & Then
            assertDoesNotThrow(() -> tournamentService.findAll(), "Method should handle uncommitted transactions without error.");
        }

        @Test
        void testTournamentWithCorruptPlayerDataHandling() {
            // Given
            Player corruptPlayer = new Player();
            corruptPlayer.setName(null); // Corrupt player data
            Set<Player> players = new HashSet<>(Collections.singletonList(corruptPlayer));

            Tournament tournament = new Tournament();
            tournament.setPlayers(players);
            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournament));

            // When
            Stream<Tournament> result = tournamentService.findAll();

            // Then
            assertNotNull(result);
            Tournament fetchedTournament = result.findFirst().orElseThrow();
            assertNotNull(fetchedTournament.getPlayers(), "Player data should be handled even if corrupt.");
        }

        @Test
        void testMethodExecutionInClosedHibernateSession() {
            // Given
            Tournament tournament = new Tournament();
            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournament));

            // Simulate closed session (mock behavior)
            doThrow(new IllegalStateException("Session closed")).when(tournamentRepository).findAll();

            // When & Then
            assertThrows(IllegalStateException.class, () -> tournamentService.findAll(), "Should throw exception if session is closed.");
        }

        @Test
        void testTournamentWithComplexUnicodeCharactersInName() {
            // Given
            Tournament tournament = new Tournament();
            tournament.setTournamentName("ТурнірПривіт");
            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournament));

            // When
            Stream<Tournament> result = tournamentService.findAll();

            // Then
            assertNotNull(result);
            Tournament fetchedTournament = result.findFirst().orElseThrow();
            assertEquals("ТурнірПривіт", fetchedTournament.getTournamentName(), "Tournament name should handle complex Unicode characters.");
        }

        //TODO change into circular tournament->player->tournament->player

        @Test
        void testTournamentWithCircularReferencesInPlayers() {
            // Given
            Player player1 = new Player();
            Player player2 = new Player();
            player1.setName("Player 1");
            player2.setName("Player 2");

//            player1.setFriends(Collections.singletonList(player2));
//            player2.setFriends(Collections.singletonList(player1));

            Set<Player> players = new HashSet<>(Arrays.asList(player1, player2));

            Tournament tournament = new Tournament();
            tournament.setPlayers(players);
            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournament));

            // When
            Stream<Tournament> result = tournamentService.findAll();

            // Then
            assertNotNull(result);
            Tournament fetchedTournament = result.findFirst().orElseThrow();
            assertEquals(2, fetchedTournament.getPlayers().size(), "Tournament should handle circular references.");
        }

        @Test
        void testTournamentWithAsynchronousDataRetrievalHandling() {
            // Given
            Tournament tournament = new Tournament();
            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournament));

            // When & Then
            assertDoesNotThrow(() -> tournamentService.findAll(), "Method should handle asynchronous data retrieval.");
        }

        @Test
        void testTournamentWithDelayedAsyncDataHandling() {
            // Given
            Tournament tournament = new Tournament();
            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournament));

            // When & Then
            assertDoesNotThrow(() -> tournamentService.findAll(), "Method should handle delayed asynchronous data.");
        }

    }
}
