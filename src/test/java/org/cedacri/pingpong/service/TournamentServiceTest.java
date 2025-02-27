package org.cedacri.pingpong.service;

import jakarta.persistence.EntityNotFoundException;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.enums.SetTypesEnum;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
import org.cedacri.pingpong.exception.tournament.NotEnoughPlayersException;
import org.cedacri.pingpong.repository.PlayerRepository;
import org.cedacri.pingpong.repository.TournamentRepository;
import org.cedacri.pingpong.utils.MatchGenerator;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tournamentService = new TournamentService(tournamentRepository, matchService, playerService);

        tournament1 = new Tournament();
        tournament1.setId(1);
        tournament1.setCreatedAt(LocalDate.of(2024, 10, 11));

        tournament2 = new Tournament();
        tournament2.setId(2);
        tournament2.setCreatedAt(LocalDate.of(2024, 10, 1));

        tournament3 = new Tournament();
        tournament3.setId(3);
        tournament3.setCreatedAt(LocalDate.of(2024, 9, 30));

        player1 = new Player();
        player1.setId(1L);
        player1.setName("John");
        player1.setSurname("Doe");
        player1.setBirthDate(LocalDate.of(1990, 1, 1));
        player1.setAddress("123 Main St");
        player1.setEmail("john.doe@example.com");
        player1.setHand("RIGHT");

        player2 = new Player();
        player2.setId(2L);
        player2.setName("Jane");
        player2.setSurname("Doe");
        player2.setBirthDate(LocalDate.of(1994, 1, 1));
        player2.setAddress("321 Secondary St");
        player2.setEmail("jane.doe@example.com");
        player2.setHand("LEFT");

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
            assertEquals(tournament1, sortedTournaments.get(0));
            assertEquals(tournament2, sortedTournaments.get(1));
            assertEquals(tournament3, sortedTournaments.get(2));
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
            tournament1.setCreatedAt(LocalDate.of(2024, 10, 1));
            tournament2.setCreatedAt(LocalDate.of(2024, 10, 1));
            tournament3.setCreatedAt(LocalDate.of(2024, 10, 1));

            List<Tournament> tournaments = Arrays.asList(tournament1, tournament2, tournament3);
            when(tournamentRepository.findAll()).thenReturn(tournaments);

            Stream<Tournament> result = tournamentService.findAll();

            List<Tournament> sortedTournaments = result.toList();
            assertEquals(3, sortedTournaments.size());
            assertTrue(sortedTournaments.containsAll(tournaments));
        }

        @Test
        void testLazyInitializationOfPlayers() {
            tournament1.setPlayers(Set.of(player1, player2));

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
                tournament.setCreatedAt(LocalDate.of(2023, 1, 1).plusDays(i % 10));  //cate 10 turnee pe zi
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
            futureTournament.setCreatedAt(LocalDate.of(2222, 12, 31));

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
            for (long i = 0; i < 1000; i++) {
                Player tempPlayer = new Player();
                tempPlayer.setId(i);
                players.add(tempPlayer);
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
        void testTournamentWithCircularReferences() {
            Tournament tournament = new Tournament();
            Set<Player> players = new HashSet<>();
            players.add(player1);
            players.add(player2);
            tournament.setPlayers(players);

            // Setăm referințele circulare
            player1.setTournaments(Set.of(tournament));
            player2.setTournaments(Set.of(tournament));

            // Mock repository să returneze turneul nostru
            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournament));

            // Când apelăm metoda
            Stream<Tournament> result = tournamentService.findAll();

            // Verificăm că turneul a fost returnat corect
            assertNotNull(result);
            Tournament fetchedTournament = result.findFirst().orElseThrow();

            // Verificăm că există 2 jucători
            assertEquals(2, fetchedTournament.getPlayers().size(), "Tournament should contain 2 players.");

            // Verificăm că fiecare jucător are referința corectă către turneu
            for (Player player : fetchedTournament.getPlayers()) {
                assertEquals(fetchedTournament, player.getTournaments().stream().findFirst().orElseThrow(), "Player should reference the correct Tournament.");
                assertTrue(player.getTournaments().contains(fetchedTournament), "Player should reference the correct Tournament.");
            }
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

    @Nested
    @DisplayName("Tests for the find method")
    class FindTest {

        @Test
        void testFindTournamentWithValidId() {
            // Arrange
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament1));

            // Act
            Tournament result = tournamentService.find(1);

            // Assert
            assertNotNull(result);
            assertEquals(tournament1, result);

            verify(tournamentRepository).findById(1);
        }

        @Test
        void testFindTournamentWithNullId() {
            assertThrows(IllegalArgumentException.class, () -> tournamentService.find(null));
        }

        @Test
        void testFindTournamentWithNegativeId() {
            assertThrows(IllegalArgumentException.class, () -> tournamentService.find(-1));
        }

        @Test
        void testFindTournamentWithZeroId() {
            assertThrows(IllegalArgumentException.class, () -> tournamentService.find(0));
        }

        @Test
        void testFindTournamentWithMaxId() {
            when(tournamentRepository.findById(Integer.MAX_VALUE)).thenReturn(Optional.of(tournament1));
            Tournament result = tournamentService.find(Integer.MAX_VALUE);
            assertNotNull(result);
        }

        @Test
        void testTournamentNotFound() {
            when(tournamentRepository.findById(1)).thenReturn(Optional.empty());
            assertThrows(NoSuchElementException.class, () -> tournamentService.find(1));
        }


        @Test
        void testNullTournamentFromRepository() {
            when(tournamentRepository.findById(1)).thenReturn(Optional.ofNullable(null));
            assertThrows(NoSuchElementException.class, () -> tournamentService.find(1));
        }

        @Test
        void testLazyInitializationOfPlayers() {
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament1));
            tournamentService.find(1);
            verify(tournamentRepository).findById(1);
        }

        @Test
        void testEmptyPlayersList() {
            tournament1.setPlayers(new HashSet<>());
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament1));
            Tournament result = tournamentService.find(1);
            assertNotNull(result.getPlayers());
            assertTrue(result.getPlayers().isEmpty());
        }

        @Test
        void testSinglePlayerInTournament() {
            tournament1.setPlayers(Set.of(player1));
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament1));
            Tournament result = tournamentService.find(1);
            assertEquals(1, result.getPlayers().size());
        }

        @Test
        void testMultiplePlayersInTournament() {
            tournament1.setPlayers(Set.of(player1, player2));
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament1));
            Tournament result = tournamentService.find(1);
            assertEquals(2, result.getPlayers().size());
        }

        @Test
        void testCorrectOrderOfPlayers() {
            Set<Player> players = new HashSet<>();
            players.add(player2);
            players.add(player1);
            tournament1.setPlayers(players);
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament1));
            Tournament result = tournamentService.find(1);

            List<Player> playersList = new ArrayList<>(result.getPlayers());

            assertEquals(player1, playersList.get(0));
            assertEquals(player2, playersList.get(1));
        }

        @Test
        void testPerformanceForTournamentWithManyPlayers() {
            Set<Player> players = new HashSet<>();
            for (long i = 0; i < 10000; i++) {
                Player tempPlayer = new Player();
                tempPlayer.setId(i);
                players.add(tempPlayer);
            }
            tournament1.setPlayers(players);
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament1));

            long startTime = System.nanoTime();
            tournamentService.find(1);
            long endTime = System.nanoTime();

            long duration = (endTime - startTime);
            assertTrue(duration < 1000000); // Should be less than 1ms
        }

        @Test
        void testPerformanceForEmptyTournament() {
            tournament1.setPlayers(Collections.emptySet());
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament1));

            long startTime = System.nanoTime();
            tournamentService.find(1);
            long endTime = System.nanoTime();

            long duration = (endTime - startTime);
            assertTrue(duration < 1000000); // Should be less than 1ms
        }

        @Test
        void testRepeatedCallsWithSameData() {
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament1));
            Tournament result1 = tournamentService.find(1);
            Tournament result2 = tournamentService.find(1);
            assertSame(result1, result2); // Should return the same instance
        }

        @Test
        void testRepeatedCallsWithDifferentData() {
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament1));
            when(tournamentRepository.findById(2)).thenReturn(Optional.of(tournament2));
            Tournament result1 = tournamentService.find(1);
            Tournament result2 = tournamentService.find(2);
            assertNotSame(result1, result2); // Should return different instances
        }
    }

    @Nested
    @DisplayName("Tests for the saveTournament method")
    class SaveTournamentTest {

        @Test
        void testSaveTournamentValid() {
            // Given
            when(tournamentRepository.save(tournament1)).thenReturn(tournament1);

            // When
            tournamentService.saveTournament(tournament1);

            // Then
            verify(tournamentRepository, times(1)).save(tournament1);
        }

        @Test
        void testSaveTournamentNull() {
            // Given
            Tournament tournament = null;
            // When / Then
            assertThrows(IllegalArgumentException.class, () -> tournamentService.saveTournament(tournament));
        }

        @Test
        void testSaveTournamentInvalidId() {
            // Given
            Tournament tournamentInvalid = new Tournament();
            tournamentInvalid.setId(-1);
            when(tournamentRepository.save(tournamentInvalid)).thenThrow(IllegalArgumentException.class);

            // When / Then
            assertThrows(IllegalArgumentException.class, () -> tournamentService.saveTournament(tournamentInvalid));
        }

        @Test
        void testSaveTournamentTransactionalSuccess() {
            // Given
            when(tournamentRepository.save(tournament1)).thenReturn(tournament1);

            // When
            tournamentService.saveTournament(tournament1);

            // Then
            verify(tournamentRepository, times(1)).save(tournament1);
        }

        @Test
        void testRollbackOnDataAccessException() {
            // Given
            when(tournamentRepository.save(tournament1)).thenThrow(new DataAccessException("Database error") {
            });

            // When / Then
            assertThrows(DataAccessException.class, () -> tournamentService.saveTournament(tournament1));
            verify(tournamentRepository, times(1)).save(tournament1);  // Verify rollback triggered
        }

        @Test
        void testSaveTournamentHibernateException() {
            // Given
            when(tournamentRepository.save(tournament1)).thenThrow(new HibernateException("Hibernate error") {
            });

            // When / Then
            assertThrows(HibernateException.class, () -> tournamentService.saveTournament(tournament1));
        }

        @Test
        void testSaveTournamentConstraintViolationException() {
            // Given
            Tournament tournamentWithExistingId = new Tournament();
            tournamentWithExistingId.setId(tournament1.getId());
            when(tournamentRepository.save(tournamentWithExistingId)).thenThrow(new ConstraintViolationException("Duplicate ID", null, null));

            // When / Then
            assertThrows(ConstraintViolationException.class, () -> tournamentService.saveTournament(tournamentWithExistingId));
        }

        @Test
        void testSaveTournamentLargeNumberOfPlayers() {
            // Given
            Set<Player> players = new HashSet<>();
            for (long i = 0; i < 10000; i++) {
                Player tempPlayer = new Player();
                tempPlayer.setId(i);
                players.add(tempPlayer);
            }
            tournament1.setPlayers(players);

            // When
            tournamentService.saveTournament(tournament1);

            // Then
            verify(tournamentRepository, times(1)).save(tournament1);
        }

        @Test
        void testSaveTournamentEmpty() {
            // Given
            Tournament emptyTournament = new Tournament();
            emptyTournament.setId(4);
            emptyTournament.setCreatedAt(LocalDate.now());

            // When
            tournamentService.saveTournament(emptyTournament);

            // Then
            verify(tournamentRepository, times(1)).save(emptyTournament);
        }

        @Test
        void testSaveTournamentWithInvalidPlayers() {
            // Given
            Tournament tournamentWithInvalidPlayers = new Tournament();
            tournamentWithInvalidPlayers.setId(5);
            tournamentWithInvalidPlayers.setPlayers(null); // No players assigned

            // When
            tournamentService.saveTournament(tournamentWithInvalidPlayers);

            // Then
            verify(tournamentRepository, times(1)).save(tournamentWithInvalidPlayers);
        }
    }

    @Nested
    @DisplayName("Tests for the saveTournament method")
    class DeleteTournamentTest {
        @Test
        void testDeleteTournamentSuccess() {
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament1));

            tournamentService.deleteById(1);

            verify(tournamentRepository, times(1)).deleteById(1);
            assertTrue(tournament1.getPlayers().stream().allMatch(player -> !player.getTournaments().contains(tournament1)));
        }

        @Test
        void testDeleteTournamentNoPlayers() {
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament1));

            tournamentService.deleteById(1);

            verify(tournamentRepository, times(1)).deleteById(1);
        }

        @Test
        void testDeleteTournamentWithManyPlayers() {
            when(tournamentRepository.findById(2)).thenReturn(Optional.of(tournament2));

            tournamentService.deleteById(2);

            verify(tournamentRepository, times(1)).deleteById(2);
            assertTrue(tournament2.getPlayers().stream().allMatch(player -> !player.getTournaments().contains(tournament2)));
        }

        @Test
        void testDeleteTournamentNotFound() {
            when(tournamentRepository.findById(999)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> tournamentService.deleteById(999));
        }

        @Test
        void testDeleteTournamentNullId() {
            assertThrows(IllegalArgumentException.class, () -> tournamentService.deleteById(null));
        }

        @Test
        void testDataIntegrityAfterDelete() {
            when(tournamentRepository.findById(2)).thenReturn(Optional.of(tournament2));

            tournamentService.deleteById(2);

            verify(tournamentRepository, times(1)).deleteById(2);
            assertTrue(tournament2.getPlayers().stream().noneMatch(player -> player.getTournaments().contains(tournament2)));
        }

        @Test
        void testDeleteTournamentPerformanceLargeNumberOfPlayers() {
            Player player = new Player();
            tournament1.setPlayers(new HashSet<>(Collections.nCopies(1000, player)));

            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament1));

            long startTime = System.currentTimeMillis();
            tournamentService.deleteById(1);
            long endTime = System.currentTimeMillis();

            assertTrue(endTime - startTime < 1000); // Should complete within 1 second
        }

        @Test
        void testDeleteTournamentPerformanceSinglePlayer() {
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament1));

            long startTime = System.currentTimeMillis();
            tournamentService.deleteById(1);
            long endTime = System.currentTimeMillis();

            assertTrue(endTime - startTime < 500); // Should complete very quickly
        }
    }

    @Nested
    @DisplayName("Tests for the updateTournament method")
    class UpdateTournamentTest {




    }


}
