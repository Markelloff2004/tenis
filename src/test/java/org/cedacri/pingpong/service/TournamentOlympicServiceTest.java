package org.cedacri.pingpong.service;

import jakarta.persistence.EntityNotFoundException;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.TournamentOlympic;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
import org.cedacri.pingpong.exception.tournament.NotEnoughPlayersException;
import org.cedacri.pingpong.repository.TournamentRepository;
import org.cedacri.pingpong.utils.Constants;
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
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.dao.DataAccessException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TournamentOlympicServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private PlayerService playerService;

    private TournamentService tournamentService;

    private TournamentOlympic tournamentOlympic1;
    private TournamentOlympic tournamentOlympic2;
    private TournamentOlympic tournamentOlympic3;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tournamentService = new TournamentService(tournamentRepository);

        tournamentOlympic1 = new TournamentOlympic();
        tournamentOlympic1.setId(1);
        tournamentOlympic1.setCreatedAt(LocalDate.of(2024, 10, 11));

        tournamentOlympic2 = new TournamentOlympic();
        tournamentOlympic2.setId(2);
        tournamentOlympic2.setCreatedAt(LocalDate.of(2024, 10, 1));

        tournamentOlympic3 = new TournamentOlympic();
        tournamentOlympic3.setId(3);
        tournamentOlympic3.setCreatedAt(LocalDate.of(2024, 9, 30));

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
            List<TournamentOlympic> tournamentOlympics = Arrays.asList(tournamentOlympic1, tournamentOlympic2, tournamentOlympic3);
            when(tournamentRepository.findAll()).thenReturn(tournamentOlympics);

            Stream<TournamentOlympic> result = tournamentService.findAllTournaments();

            List<TournamentOlympic> sortedTournamentOlympics = result.toList();
            assertEquals(3, sortedTournamentOlympics.size());
            assertEquals(tournamentOlympic1, sortedTournamentOlympics.get(0));
            assertEquals(tournamentOlympic2, sortedTournamentOlympics.get(1));
            assertEquals(tournamentOlympic3, sortedTournamentOlympics.get(2));
        }

        @Test
        void testNoTournamentsInDatabaseReturnsEmptyStream() {
            when(tournamentRepository.findAll()).thenReturn(Collections.emptyList());

            Stream<TournamentOlympic> result = tournamentService.findAllTournaments();

            assertEquals(0, result.count());

        }

        @Test
        void testSingleTournamentReturnedCorrectly() {
            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournamentOlympic1));

            Stream<TournamentOlympic> result = tournamentService.findAllTournaments();

            List<TournamentOlympic> tournamentOlympics = result.toList();
            assertEquals(1, tournamentOlympics.size());
            assertEquals(tournamentOlympic1, tournamentOlympics.get(0));
        }

        @Test
        void testMultipleTournamentsWithSameCreatedAtOrderVerification() {
            tournamentOlympic1.setCreatedAt(LocalDate.of(2024, 10, 1));
            tournamentOlympic2.setCreatedAt(LocalDate.of(2024, 10, 1));
            tournamentOlympic3.setCreatedAt(LocalDate.of(2024, 10, 1));

            List<TournamentOlympic> tournamentOlympics = Arrays.asList(tournamentOlympic1, tournamentOlympic2, tournamentOlympic3);
            when(tournamentRepository.findAll()).thenReturn(tournamentOlympics);

            Stream<TournamentOlympic> result = tournamentService.findAllTournaments();

            List<TournamentOlympic> sortedTournamentOlympics = result.toList();
            assertEquals(3, sortedTournamentOlympics.size());
            assertTrue(sortedTournamentOlympics.containsAll(tournamentOlympics));
        }

        @Test
        void testLazyInitializationOfPlayers() {
            tournamentOlympic1.setPlayers(Set.of(player1, player2));

            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournamentOlympic1));

            Stream<TournamentOlympic> result = tournamentService.findAllTournaments();
            result.forEach(tournament -> {
                Hibernate.initialize(tournament.getPlayers());
                assertNotNull(tournament.getPlayers());
            });
        }

        @Test
        void testTournamentsWithEmptyPlayersList() {
            tournamentOlympic1.setPlayers(Collections.emptySet());
            tournamentOlympic2.setPlayers(Collections.emptySet());

            when(tournamentRepository.findAll()).thenReturn(Arrays.asList(tournamentOlympic1, tournamentOlympic2));

            Stream<TournamentOlympic> result = tournamentService.findAllTournaments();
            List<TournamentOlympic> tournamentOlympics = result.toList();

            assertEquals(2, tournamentOlympics.size());
            tournamentOlympics.forEach(tournament -> assertTrue(tournament.getPlayers().isEmpty()));
        }

        @Test
        void testLargeDatabasePerformance() {
            List<TournamentOlympic> largeList = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                TournamentOlympic tournamentOlympic = new TournamentOlympic();
                tournamentOlympic.setCreatedAt(LocalDate.of(2023, 1, 1).plusDays(i % 10));  //cate 10 turnee pe zi
                largeList.add(tournamentOlympic);
            }

            when(tournamentRepository.findAll()).thenReturn(largeList);

            long startTime = System.currentTimeMillis();
            tournamentService.findAllTournaments();
            long endTime = System.currentTimeMillis();

            long duration = endTime - startTime;
            assertTrue(duration < 1000);
        }

        @Test
        void testTournamentsWithFutureCreatedAtDates() {
            TournamentOlympic futureTournamentOlympic = new TournamentOlympic();
            futureTournamentOlympic.setId(1);
            futureTournamentOlympic.setCreatedAt(LocalDate.of(2222, 12, 31));

            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(futureTournamentOlympic));

            Stream<TournamentOlympic> result = tournamentService.findAllTournaments();

            List<TournamentOlympic> tournamentOlympics = result.toList();
            assertEquals(1, tournamentOlympics.size());
            assertEquals(futureTournamentOlympic, tournamentOlympics.get(0));
        }

        @Test
        void testRepositoryThrowsExceptionHandling() {
            when(tournamentRepository.findAll()).thenThrow(new RuntimeException("Database error"));

            Exception exception = assertThrows(RuntimeException.class, () -> tournamentService.findAllTournaments());

            assertEquals("Database error", exception.getMessage());
        }

        @Test
        void testTournamentWithNullCreatedAt() {
            // Given
            TournamentOlympic tournamentOlympic = new TournamentOlympic();
            tournamentOlympic.setCreatedAt(null); // Null createdAt
            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournamentOlympic));

            // When
            Stream<TournamentOlympic> result = tournamentService.findAllTournaments();

            // Then
            assertNotNull(result);
            TournamentOlympic fetchedTournamentOlympic = result.findFirst().orElseThrow();
            assertNull(fetchedTournamentOlympic.getCreatedAt(), "Tournament should have a null createdAt.");
        }

        @Test
        void testTournamentWithSpecialCharactersInName() {
            // Given
            TournamentOlympic tournamentOlympic = new TournamentOlympic();
            tournamentOlympic.setTournamentName("Special@#Tournament");
            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournamentOlympic));

            // When
            Stream<TournamentOlympic> result = tournamentService.findAllTournaments();

            // Then
            assertNotNull(result);
            TournamentOlympic fetchedTournamentOlympic = result.findFirst().orElseThrow();
            assertEquals("Special@#Tournament", fetchedTournamentOlympic.getTournamentName(), "Tournament name should handle special characters.");
        }

        @Test
        void testTournamentWithDuplicateNames() {
            // Given
            TournamentOlympic duplicateTournament1Olympic = new TournamentOlympic();
            duplicateTournament1Olympic.setTournamentName("Duplicate Tournament");
            TournamentOlympic duplicateTournament2Olympic = new TournamentOlympic();
            duplicateTournament2Olympic.setTournamentName("Duplicate Tournament");
            when(tournamentRepository.findAll()).thenReturn(Arrays.asList(duplicateTournament1Olympic, duplicateTournament2Olympic));

            // When
            Stream<TournamentOlympic> result = tournamentService.findAllTournaments();

            // Then
            assertNotNull(result);
            List<TournamentOlympic> tournamentOlympics = result.toList();
            assertEquals(2, tournamentOlympics.size(), "There should be two tournaments with the same name.");
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
            TournamentOlympic tournamentOlympic = new TournamentOlympic();
            tournamentOlympic.setPlayers(players);
            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournamentOlympic));

            // When
            Stream<TournamentOlympic> result = tournamentService.findAllTournaments();

            // Then
            assertNotNull(result);
            TournamentOlympic fetchedTournamentOlympic = result.findFirst().orElseThrow();
            assertEquals(1000, fetchedTournamentOlympic.getPlayers().size(), "Tournament should have 1000 players.");
        }

        @Test
        void testRepositoryReturnsNullHandling() {
            // Given
            when(tournamentRepository.findAll()).thenReturn(null);

            // When & Then
            assertThrows(NullPointerException.class, () -> tournamentService.findAllTournaments(), "Repository should not return null.");
        }

        @Test
        void testOutOfMemoryErrorHandling() {
            // Given
            when(tournamentRepository.findAll()).thenThrow(OutOfMemoryError.class);

            // When & Then
            assertThrows(OutOfMemoryError.class, () -> tournamentService.findAllTournaments(), "Should throw OutOfMemoryError.");
        }

        @Test
        void testUncommittedTransactionHandling() {
            // Given
            TournamentOlympic tournamentOlympic = new TournamentOlympic();
            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournamentOlympic));

            // When & Then
            assertDoesNotThrow(() -> tournamentService.findAllTournaments(), "Method should handle uncommitted transactions without error.");
        }

        @Test
        void testTournamentWithCorruptPlayerDataHandling() {
            // Given
            Player corruptPlayer = new Player();
            corruptPlayer.setName(null); // Corrupt player data
            Set<Player> players = new HashSet<>(Collections.singletonList(corruptPlayer));

            TournamentOlympic tournamentOlympic = new TournamentOlympic();
            tournamentOlympic.setPlayers(players);
            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournamentOlympic));

            // When
            Stream<TournamentOlympic> result = tournamentService.findAllTournaments();

            // Then
            assertNotNull(result);
            TournamentOlympic fetchedTournamentOlympic = result.findFirst().orElseThrow();
            assertNotNull(fetchedTournamentOlympic.getPlayers(), "Player data should be handled even if corrupt.");
        }

        @Test
        void testMethodExecutionInClosedHibernateSession() {
            // Given
            TournamentOlympic tournamentOlympic = new TournamentOlympic();
            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournamentOlympic));

            // Simulate closed session (mock behavior)
            doThrow(new IllegalStateException("Session closed")).when(tournamentRepository).findAll();

            // When & Then
            assertThrows(IllegalStateException.class, () -> tournamentService.findAllTournaments(), "Should throw exception if session is closed.");
        }

        @Test
        void testTournamentWithComplexUnicodeCharactersInName() {
            // Given
            TournamentOlympic tournamentOlympic = new TournamentOlympic();
            tournamentOlympic.setTournamentName("ТурнірПривіт");
            when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournamentOlympic));

            // When
            Stream<TournamentOlympic> result = tournamentService.findAllTournaments();

            // Then
            assertNotNull(result);
            TournamentOlympic fetchedTournamentOlympic = result.findFirst().orElseThrow();
            assertEquals("ТурнірПривіт", fetchedTournamentOlympic.getTournamentName(), "Tournament name should handle complex Unicode characters.");
        }
    }

    @Nested
    @DisplayName("Tests for the find method")
    class FindTest {

        @Test
        void testFindTournamentWithValidId() {
            // Arrange
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournamentOlympic1));

            // Act
            TournamentOlympic result = tournamentService.findTournamentById(1);

            // Assert
            assertNotNull(result);
            assertEquals(tournamentOlympic1, result);

            verify(tournamentRepository).findById(1);
        }

        @Test
        void testFindTournamentWithNullId() {
            assertThrows(IllegalArgumentException.class, () -> tournamentService.findTournamentById(null));
        }

        @Test
        void testFindTournamentWithNegativeId() {
            assertThrows(IllegalArgumentException.class, () -> tournamentService.findTournamentById(-1));
        }

        @Test
        void testFindTournamentWithZeroId() {
            assertThrows(IllegalArgumentException.class, () -> tournamentService.findTournamentById(0));
        }

        @Test
        void testFindTournamentWithMaxId() {
            when(tournamentRepository.findById(Integer.MAX_VALUE)).thenReturn(Optional.of(tournamentOlympic1));
            TournamentOlympic result = tournamentService.findTournamentById(Integer.MAX_VALUE);
            assertNotNull(result);
        }

        @Test
        void testTournamentNotFound() {
            when(tournamentRepository.findById(1)).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () -> tournamentService.findTournamentById(1));
        }


        @Test
        void testNullTournamentFromRepository() {
            when(tournamentRepository.findById(1)).thenReturn(Optional.ofNullable(null));
            assertThrows(EntityNotFoundException.class, () -> tournamentService.findTournamentById(1));
        }

        @Test
        void testLazyInitializationOfPlayers() {
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournamentOlympic1));
            tournamentService.findTournamentById(1);
            verify(tournamentRepository).findById(1);
        }

        @Test
        void testEmptyPlayersList() {
            tournamentOlympic1.setPlayers(new HashSet<>());
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournamentOlympic1));
            TournamentOlympic result = tournamentService.findTournamentById(1);
            assertNotNull(result.getPlayers());
            assertTrue(result.getPlayers().isEmpty());
        }

        @Test
        void testSinglePlayerInTournament() {
            tournamentOlympic1.setPlayers(Set.of(player1));
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournamentOlympic1));
            TournamentOlympic result = tournamentService.findTournamentById(1);
            assertEquals(1, result.getPlayers().size());
        }

        @Test
        void testMultiplePlayersInTournament() {
            tournamentOlympic1.setPlayers(Set.of(player1, player2));
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournamentOlympic1));
            TournamentOlympic result = tournamentService.findTournamentById(1);
            assertEquals(2, result.getPlayers().size());
        }

        @Test
        void testCorrectOrderOfPlayers() {
            Set<Player> players = new HashSet<>();
            players.add(player2);
            players.add(player1);
            tournamentOlympic1.setPlayers(players);
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournamentOlympic1));
            TournamentOlympic result = tournamentService.findTournamentById(1);

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
            tournamentOlympic1.setPlayers(players);
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournamentOlympic1));

            long startTime = System.nanoTime();
            tournamentService.findTournamentById(1);
            long endTime = System.nanoTime();

            long duration = (endTime - startTime);
            assertTrue(duration < 1000000); // Should be less than 1ms
        }

        @Test
        void testPerformanceForEmptyTournament() {
            tournamentOlympic1.setPlayers(Collections.emptySet());
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournamentOlympic1));

            long startTime = System.nanoTime();
            tournamentService.findTournamentById(1);
            long endTime = System.nanoTime();

            long duration = (endTime - startTime);
            assertTrue(duration < 1000000); // Should be less than 1ms
        }

        @Test
        void testRepeatedCallsWithSameData() {
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournamentOlympic1));
            TournamentOlympic result1 = tournamentService.findTournamentById(1);
            TournamentOlympic result2 = tournamentService.findTournamentById(1);
            assertSame(result1, result2); // Should return the same instance
        }

        @Test
        void testRepeatedCallsWithDifferentData() {
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournamentOlympic1));
            when(tournamentRepository.findById(2)).thenReturn(Optional.of(tournamentOlympic2));
            TournamentOlympic result1 = tournamentService.findTournamentById(1);
            TournamentOlympic result2 = tournamentService.findTournamentById(2);
            assertNotSame(result1, result2); // Should return different instances
        }
    }

    @Nested
    @DisplayName("Tests for the saveTournament method")
    class SaveTournamentTestOlympic {

        @Test
        void testSaveTournamentValid() {
            // Given
            when(tournamentRepository.save(tournamentOlympic1)).thenReturn(tournamentOlympic1);

            // When
            tournamentService.saveTournament(tournamentOlympic1);

            // Then
            verify(tournamentRepository, times(1)).save(tournamentOlympic1);
        }

        @Test
        void testSaveTournamentNull() {
            // Given
            TournamentOlympic tournamentOlympic = null;
            // When / Then
            assertThrows(IllegalArgumentException.class, () -> tournamentService.saveTournament(tournamentOlympic));
        }

        @Test
        void testSaveTournamentInvalidId() {
            // Given
            TournamentOlympic tournamentOlympicInvalid = new TournamentOlympic();
            tournamentOlympicInvalid.setId(-1);
            when(tournamentRepository.save(tournamentOlympicInvalid)).thenThrow(IllegalArgumentException.class);

            // When / Then
            assertThrows(IllegalArgumentException.class, () -> tournamentService.saveTournament(tournamentOlympicInvalid));
        }

        @Test
        void testRollbackOnDataAccessException() {
            // Given
            when(tournamentRepository.save(tournamentOlympic1)).thenThrow(new DataAccessException("Database error") {
            });

            // When / Then
            assertThrows(DataAccessException.class, () -> tournamentService.saveTournament(tournamentOlympic1));
            verify(tournamentRepository, times(1)).save(tournamentOlympic1);  // Verify rollback triggered
        }

        @Test
        void testSaveTournamentHibernateException() {
            // Given
            when(tournamentRepository.save(tournamentOlympic1)).thenThrow(new HibernateException("Hibernate error") {
            });

            // When / Then
            assertThrows(HibernateException.class, () -> tournamentService.saveTournament(tournamentOlympic1));
        }

        @Test
        void testSaveTournamentConstraintViolationException() {
            // Given
            TournamentOlympic tournamentOlympicWithExistingId = new TournamentOlympic();
            tournamentOlympicWithExistingId.setId(tournamentOlympic1.getId());
            when(tournamentRepository.save(tournamentOlympicWithExistingId)).thenThrow(new ConstraintViolationException("Duplicate ID", null, null));

            // When / Then
            assertThrows(ConstraintViolationException.class, () -> tournamentService.saveTournament(tournamentOlympicWithExistingId));
        }

        @Test
        void testSaveTournamentLargeNumberOfPlayers() {
            // Given
            Set<Player> players = new HashSet<>();
            for (long i = 0; i < 1000; i++) {
                Player tempPlayer = new Player();
                tempPlayer.setId(i);
                players.add(tempPlayer);

                // Мокирование вызова playerService.findById
                when(playerService.findPlayerById(i)).thenReturn(tempPlayer);
            }
            tournamentOlympic1.setPlayers(players);

            when(tournamentRepository.save(tournamentOlympic1)).thenReturn(tournamentOlympic1);

            // When
            TournamentOlympic savedTournamentOlympic = tournamentService.saveTournament(tournamentOlympic1);

            // Then
            verify(tournamentRepository, times(1)).save(tournamentOlympic1);
            assertNotNull(savedTournamentOlympic);
            assertEquals(1000, savedTournamentOlympic.getPlayers().size());
            assertTrue(savedTournamentOlympic.getPlayers().containsAll(players));
        }

        @Test
        void testSaveTournamentEmpty() {
            // Given
            TournamentOlympic emptyTournamentOlympic = new TournamentOlympic();
            emptyTournamentOlympic.setId(4);
            emptyTournamentOlympic.setCreatedAt(LocalDate.now());

            // When
            tournamentService.saveTournament(emptyTournamentOlympic);

            // Then
            verify(tournamentRepository, times(1)).save(emptyTournamentOlympic);
        }

        @Test
        void testSaveTournamentWithInvalidPlayers() {
            // Given
            TournamentOlympic tournamentOlympicWithInvalidPlayers = new TournamentOlympic();
            tournamentOlympicWithInvalidPlayers.setId(5);
            tournamentOlympicWithInvalidPlayers.setPlayers(null); // No players assigned

            // When
            tournamentService.saveTournament(tournamentOlympicWithInvalidPlayers);

            // Then
            verify(tournamentRepository, times(1)).save(tournamentOlympicWithInvalidPlayers);
        }
    }

    @Nested
    @DisplayName("Tests for the saveTournament method")
    class DeleteTournamentTestOlympic {
        @Test
        void testDeleteTournamentSuccess() {
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournamentOlympic1));

            tournamentService.deleteTournamentById(1);

            verify(tournamentRepository, times(1)).deleteById(1);
            assertTrue(tournamentOlympic1.getPlayers().stream().allMatch(player -> !player.getTournamentOlympics().contains(tournamentOlympic1)));
        }

        @Test
        void testDeleteTournamentNoPlayers() {
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournamentOlympic1));

            tournamentService.deleteTournamentById(1);

            verify(tournamentRepository, times(1)).deleteById(1);
        }

        @Test
        void testDeleteTournamentWithManyPlayers() {
            when(tournamentRepository.findById(2)).thenReturn(Optional.of(tournamentOlympic2));

            tournamentService.deleteTournamentById(2);

            verify(tournamentRepository, times(1)).deleteById(2);
            assertTrue(tournamentOlympic2.getPlayers().stream().allMatch(player -> !player.getTournamentOlympics().contains(tournamentOlympic2)));
        }

        @Test
        void testDeleteTournamentNotFound() {
            when(tournamentRepository.findById(999)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> tournamentService.deleteTournamentById(999));
        }

        @Test
        void testDeleteTournamentNullId() {
            assertThrows(IllegalArgumentException.class, () -> tournamentService.deleteTournamentById(null));
        }

        @Test
        void testDataIntegrityAfterDelete() {
            when(tournamentRepository.findById(2)).thenReturn(Optional.of(tournamentOlympic2));

            tournamentService.deleteTournamentById(2);

            verify(tournamentRepository, times(1)).deleteById(2);
            assertTrue(tournamentOlympic2.getPlayers().stream().noneMatch(player -> player.getTournamentOlympics().contains(tournamentOlympic2)));
        }

        @Test
        void testDeleteTournamentPerformanceLargeNumberOfPlayers() {
            Player player = new Player();
            tournamentOlympic1.setPlayers(new HashSet<>(Collections.nCopies(1000, player)));

            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournamentOlympic1));

            long startTime = System.currentTimeMillis();
            tournamentService.deleteTournamentById(1);
            long endTime = System.currentTimeMillis();

            assertTrue(endTime - startTime < 1000); // Should complete within 1 second
        }

        @Test
        void testDeleteTournamentPerformanceSinglePlayer() {
            when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournamentOlympic1));

            long startTime = System.currentTimeMillis();
            tournamentService.deleteTournamentById(1);
            long endTime = System.currentTimeMillis();

            assertTrue(endTime - startTime < 500); // Should complete very quickly
        }
    }

    @Nested
    @DisplayName("Tests for the startTournament method")
    class StartTournamentTestOlympic {

        @Mock
        private TournamentOlympic tournamentOlympic;
        private Set<Player> players;

        @Mock
        private MatchGenerator matchGeneratorMock;
        @Mock
        private TournamentRepository tournamentRepository;

        @Spy
        @InjectMocks
        private TournamentService tournamentServiceSpy;

        @BeforeEach
        void setUp() {

            MockitoAnnotations.openMocks(this);

            tournamentOlympic = new TournamentOlympic();
            tournamentOlympic.setId(1);

            players = new HashSet<>();
            for (int i = 1; i <= 8; i++) {
                Player p = new Player();
                p.setId((long) i);
                players.add(p);
            }
            tournamentOlympic.setPlayers(players);
            tournamentOlympic.setTournamentType(TournamentTypeEnum.OLYMPIC);

            doReturn(matchGeneratorMock).when(tournamentServiceSpy).createMatchGenerator(any(TournamentOlympic.class));
            doNothing().when(matchGeneratorMock).generateMatches(any(TournamentOlympic.class));
        }

        @Test
        void testStartTournament_withValidParameters_OLYMPIC() throws NotEnoughPlayersException {
            tournamentOlympic.setTournamentType(TournamentTypeEnum.OLYMPIC);

            tournamentServiceSpy.startTournament(tournamentOlympic);

            verify(matchGeneratorMock, times(1)).generateMatches(tournamentOlympic);
        }

        @Test
        void testStartTournament_withValidParameters_ROBIN_ROUND() throws NotEnoughPlayersException {
            tournamentOlympic.setTournamentType(TournamentTypeEnum.ROBIN_ROUND);

            tournamentServiceSpy.startTournament(tournamentOlympic);

            verify(matchGeneratorMock, times(1)).generateMatches(tournamentOlympic);
        }

        @Test
        void testStartTournament_withExactlyMinimalPlayersForOlympic() throws NotEnoughPlayersException {
            tournamentOlympic.setTournamentType(TournamentTypeEnum.OLYMPIC);
            players.clear();
            for (int i = 1; i <= Constants.MINIMAL_AMOUNT_OF_PLAYER_FOR_OLYMPIC; i++) {
                Player p = new Player();
                p.setId((long) i);
                players.add(p);
            }
            tournamentOlympic.setPlayers(players);

            tournamentServiceSpy.startTournament(tournamentOlympic);

            verify(matchGeneratorMock, times(1)).generateMatches(tournamentOlympic);
        }

        @Test
        void testStartTournament_withExactlyMinimalPlayersForRobinRound() throws NotEnoughPlayersException {
            tournamentOlympic.setTournamentType(TournamentTypeEnum.ROBIN_ROUND);
            players.clear();
            for (int i = 1; i <= Constants.MINIMAL_AMOUNT_OF_PLAYER_FOR_ROBIN_ROUND; i++) {
                Player p = new Player();
                p.setId((long) i);
                players.add(p);
            }
            tournamentOlympic.setPlayers(players);

            tournamentServiceSpy.startTournament(tournamentOlympic);

            verify(matchGeneratorMock, times(1)).generateMatches(tournamentOlympic);
        }

        @Test
        void testStartTournament_withOnePlayerAboveMinimal() throws NotEnoughPlayersException {
            players.add(new Player()); // Adding one more player to exceed the minimum
            tournamentOlympic.setPlayers(players);

            tournamentServiceSpy.startTournament(tournamentOlympic);

            verify(matchGeneratorMock, times(1)).generateMatches(tournamentOlympic);
        }

        @Test
        void testStartTournament_withOnePlayerBelowMinimal() {
            players.remove(players.iterator().next()); // Remove one player to make it below the minimum required
            tournamentOlympic.setPlayers(players);

            assertThrows(NotEnoughPlayersException.class, () -> tournamentServiceSpy.startTournament(tournamentOlympic));
        }

        @Test
        void testStartTournament_withExactly8Players() throws NotEnoughPlayersException {
            players.clear();
            for (int i = 1; i <= 8; i++) {
                Player p = new Player();
                p.setId((long) i);
                players.add(p);
            }
            tournamentOlympic.setPlayers(players);

            tournamentServiceSpy.startTournament(tournamentOlympic);

            verify(matchGeneratorMock, times(1)).generateMatches(tournamentOlympic);
        }

        @Test
        void testStartTournament_withNullPlayers() {
            tournamentOlympic.setPlayers(null);
            assertThrows(NullPointerException.class, () -> {
                tournamentServiceSpy.startTournament(tournamentOlympic);
            });
        }

        @Test
        void testStartTournament_withEmptyPlayers() {
            tournamentOlympic.setPlayers(new HashSet<>());

            assertThrows(NotEnoughPlayersException.class, () -> {
                tournamentServiceSpy.startTournament(tournamentOlympic);
            });
        }

        @Test
        void testStartTournament_withNullTournamentType() {
            tournamentOlympic.setTournamentType(null);

            assertThrows(IllegalArgumentException.class, () -> tournamentServiceSpy.startTournament(tournamentOlympic));
        }
    }
}
