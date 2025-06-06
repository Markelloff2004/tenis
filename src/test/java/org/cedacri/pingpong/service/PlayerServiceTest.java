package org.cedacri.pingpong.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityNotFoundException;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.exception.tournament.EntityDeletionException;
import org.cedacri.pingpong.repository.PlayerRepository;
import org.cedacri.pingpong.service.primary.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.*;

public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    private PlayerService playerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        playerService = new PlayerService(playerRepository);
    }

    @Nested
    @DisplayName("Tests for the findById method")
    class FindByIdTests {

        @Test
        void testFindById_playerExists() {
            Long playerId = 1L;
            Player player = new Player();
            player.setId(playerId);
            when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));

            Player result = playerService.findPlayerById(playerId);
            assertNotNull(result);
            assertEquals(playerId, result.getId());
        }

        @Test
        void testFindById_playerNotFound() {
            Long playerId = 1L;
            when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> playerService.findPlayerById(playerId));
            assertEquals("Player not found", exception.getMessage());
        }

        @Test
        void testFindById_nullId() {
            Long playerId = null;
            assertThrows(IllegalArgumentException.class, () -> playerService.findPlayerById(playerId));
        }

        @Test
        void testFindById_negativeId() {
            Long playerId = -1L;
            when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> playerService.findPlayerById(playerId));
            assertEquals("Player not found", exception.getMessage());
        }

        @Test
        void testFindById_largeId() {
            Long playerId = Long.MAX_VALUE;
            Player player = new Player();
            player.setId(playerId);
            when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));

            Player result = playerService.findPlayerById(playerId);
            assertNotNull(result);
            assertEquals(playerId, result.getId());
        }

        @Test
        void testFindById_zeroId() {
            Long playerId = 0L;
            when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> playerService.findPlayerById(playerId));
            assertEquals("Player not found", exception.getMessage());
        }

        @Test
        void testFindById_concurrentAccess() throws InterruptedException {
            Long playerId = 1L;
            Player player = new Player();
            player.setId(playerId);
            when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));

            Runnable task = () -> playerService.findPlayerById(playerId);

            Thread thread1 = new Thread(task);
            Thread thread2 = new Thread(task);

            thread1.start();
            thread2.start();

            thread1.join();
            thread2.join();

            verify(playerRepository, times(2)).findById(playerId);
        }
    }

    @Nested
    @DisplayName("Tests for the getAll method")
    class GetAllTests {

        @Test
        void testGetAll_returnAllPlayers() {
            List<Player> players = Arrays.asList(new Player(), new Player());
            when(playerRepository.findAll()).thenReturn(players);

            List<Player> result = playerService.getAllPlayers();
            assertEquals(2, result.size());
            assertEquals(players, result);
        }

        @Test
        void testGetAll_noPlayers() {
            when(playerRepository.findAll()).thenReturn(Collections.emptyList());

            List<Player> result = playerService.getAllPlayers();
            assertTrue(result.isEmpty());
        }

        @Test
        void testGetAll_performanceWithLargeNumberOfPlayers() {
            List<Player> players = new ArrayList<>();
            for (int i = 0; i < 10000; i++) {
                players.add(new Player());
            }
            when(playerRepository.findAll()).thenReturn(players);

            long startTime = System.nanoTime();
            List<Player> result = playerService.getAllPlayers();
            long duration = System.nanoTime() - startTime;

            assertEquals(10000, result.size());
            assertTrue(duration < 1000000000L);
        }

        @Test
        void testGetAll_nullPlayersList() {
            when(playerRepository.findAll()).thenReturn(null);

            List<Player> result = playerService.getAllPlayers();

            assertEquals(Collections.emptyList(), result);
        }

        @Test
        void testGetAll_emptyCollectionFieldInPlayer() {
            Player playerWithEmptyCollection = new Player();
            playerWithEmptyCollection.setTournaments(new HashSet<>());

            List<Player> players = List.of(playerWithEmptyCollection);
            when(playerRepository.findAll()).thenReturn(players);

            List<Player> result = playerService.getAllPlayers();
            assertNotNull(result);
            assertEquals(1, result.size());
            assertTrue(result.get(0).getTournaments().isEmpty());
        }

        @Test
        void testGetAll_duplicatePlayers() {
            Player player1 = new Player();
            Player player2 = new Player();
            when(playerRepository.findAll()).thenReturn(Arrays.asList(player1, player1, player2));

            List<Player> result = playerService.getAllPlayers();

            assertEquals(3, result.size());
            assertTrue(result.contains(player1));
            assertTrue(result.contains(player2));
        }

        @Test
        void testGetAll_afterBulkDeletion() {
            List<Player> players = Arrays.asList(new Player(), new Player());
            when(playerRepository.findAll()).thenReturn(players);

            playerService.getAllPlayers();

            playerRepository.deleteAll();
            when(playerRepository.findAll()).thenReturn(Collections.emptyList());

            List<Player> result = playerService.getAllPlayers();
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Tests for the save method")
    class SaveTests {

        @Test
        void testSave_validPlayer() {
            Player validPlayer = new Player("John", "Doe", "123 Main St", "john.doe@example.com", "RIGHT");
            validPlayer.setRating(10);
            validPlayer.setWonMatches(5);
            validPlayer.setLostMatches(2);
            validPlayer.setGoalsScored(12);
            validPlayer.setGoalsLost(5);
            validPlayer.setBirthDate(LocalDate.of(1995, 1, 1));
            when(playerRepository.save(validPlayer)).thenReturn(validPlayer);

            Player savedPlayer = playerService.savePlayer(validPlayer);
            assertEquals(validPlayer, savedPlayer);
            verify(playerRepository).save(validPlayer);
        }

        @Test
        void testSave_updateExistingPlayer() {
            Player existingPlayer = new Player("Jane", "Doe", "456 Main St", "jane.doe@example.com", "LEFT");
            existingPlayer.setId(1L);
            existingPlayer.setRating(8);
            when(playerRepository.findById(existingPlayer.getId())).thenReturn(Optional.of(existingPlayer));
            when(playerRepository.save(existingPlayer)).thenReturn(existingPlayer);

            Player updatedPlayer = playerService.savePlayer(existingPlayer);
            assertEquals(existingPlayer, updatedPlayer);
            verify(playerRepository).save(existingPlayer);
            verify(playerRepository).findById(existingPlayer.getId());
        }

        @Test
        void testSave_updateNonExistingPlayer() {
            Player nonExistingPlayer = new Player("Jane", "Doe", "456 Main St", "jane.doe@example.com", "LEFT");
            nonExistingPlayer.setId(1L);
            nonExistingPlayer.setRating(8);
            when(playerRepository.findById(nonExistingPlayer.getId())).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> playerService.savePlayer(nonExistingPlayer));
            verify(playerRepository).findById(nonExistingPlayer.getId());
        }

        @Test
        void testSave_playerIsNull() {
            assertThrows(IllegalArgumentException.class, () -> playerService.savePlayer(null));
        }

        @Test
        void testSave_saveFails() {
            Player playerToSave = new Player("Error Player", "Unknown", "789 Main St", "error.player@example.com", "RIGHT");
            when(playerRepository.save(playerToSave)).thenThrow(new RuntimeException("Database error"));

            assertThrows(RuntimeException.class, () -> playerService.savePlayer(playerToSave));
        }

        @Test
        void testSave_existingPlayer() {
            Player existingPlayer = new Player("Alice", "Smith", "456 Elm St", "alice.smith@example.com", "RIGHT");
            existingPlayer.setId(2L);

            Player updatedPlayer = new Player("Alice", "Johnson", "789 Oak St", "alice.johnson@example.com", "LEFT");
            updatedPlayer.setId(2L);

            when(playerRepository.findById(2L)).thenReturn(Optional.of(existingPlayer));
            when(playerRepository.save(any(Player.class))).thenReturn(updatedPlayer);

            Player result = playerService.savePlayer(updatedPlayer);

            assertNotNull(result);
            assertEquals("Alice", result.getName());
            assertEquals("Johnson", result.getSurname());
            assertEquals("alice.johnson@example.com", result.getEmail());
            verify(playerRepository).save(updatedPlayer);
        }

        @Test
        void testSave_playerWithSpecialFormattingFields() {
            Player playerWithSpecialFormat = new Player("Special", "Format", "123 Address", "special+format@example.com", "RIGHT");
            when(playerRepository.save(playerWithSpecialFormat)).thenReturn(playerWithSpecialFormat);

            Player savedPlayer = playerService.savePlayer(playerWithSpecialFormat);
            assertEquals("special+format@example.com", savedPlayer.getEmail());
            verify(playerRepository).save(playerWithSpecialFormat);
        }

        @Test
        void testSave_transactionAfterNetworkFailure() {
            Player playerWithNetworkIssue = new Player("Network Error", "Player", "456 Main St", "network.error@example.com", "LEFT");
            when(playerRepository.save(playerWithNetworkIssue))
                    .thenThrow(new RuntimeException("Network failure"))
                    .thenReturn(playerWithNetworkIssue);

            assertThrows(RuntimeException.class, () -> playerService.savePlayer(playerWithNetworkIssue));
            Player savedPlayer = playerService.savePlayer(playerWithNetworkIssue);
            assertEquals(playerWithNetworkIssue, savedPlayer);
        }
    }

    @Nested
    @DisplayName("Tests for the deleteById method")
    class DeleteByIdTests {

        @Test
        void testDeleteById_existingPlayer() {
            Long playerId = 1L;
            when(playerRepository.existsById(playerId)).thenReturn(true);
            doNothing().when(playerRepository).deleteById(playerId);

            assertDoesNotThrow(() -> playerService.deletePlayerById(playerId));
            verify(playerRepository).deleteById(playerId);
        }

        @Test
        void testDeleteById_twice() {
            Long playerId = 1L;
            when(playerRepository.existsById(playerId)).thenReturn(true).thenReturn(false);
            doNothing().when(playerRepository).deleteById(playerId);

            assertDoesNotThrow(() -> playerService.deletePlayerById(playerId));
            assertThrows(EntityNotFoundException.class, () -> playerService.deletePlayerById(playerId));

            verify(playerRepository, times(1)).deleteById(playerId);
            verify(playerRepository, times(2)).existsById(playerId);
        }

        @Test
        void testDeleteById_nullId() {
            assertThrows(IllegalArgumentException.class, () -> playerService.deletePlayerById(null));
        }

        @Test
        void testDeleteById_nonExistentPlayer() {
            Long playerId = 99L;
            when(playerRepository.existsById(playerId)).thenReturn(false);

            assertThrows(EntityNotFoundException.class, () -> playerService.deletePlayerById(playerId));
        }

        @Test
        void testDeleteById_deleteFails() {
            Long playerId = 3L;
            when(playerRepository.existsById(playerId)).thenReturn(true);
            doThrow(new RuntimeException("Database failure"))
                    .when(playerRepository).deleteById(playerId);

            assertThrows(IllegalStateException.class, () -> playerService.deletePlayerById(playerId));
        }

        @Test
        void testDeleteById_nonExistentPlayerNoEffect() {
            Long playerId = 100L;
            when(playerRepository.existsById(playerId)).thenReturn(false);

            assertThrows(EntityNotFoundException.class, () -> playerService.deletePlayerById(playerId));
            verify(playerRepository, never()).deleteById(playerId);
        }

        @Test
        void testDeleteById_playerWithRelations() {
            Long playerId = 4L;
            when(playerRepository.existsById(playerId)).thenReturn(true);
            doThrow(new DataIntegrityViolationException("Cannot delete, player is referenced"))
                    .when(playerRepository).deleteById(playerId);

            assertThrows(EntityDeletionException.class, () -> playerService.deletePlayerById(playerId));
        }

        @Test
        void testDeleteById_duringSchemaUpdate() {
            Long playerId = 6L;
            when(playerRepository.existsById(playerId)).thenReturn(true);
            doThrow(new IllegalStateException("Database schema update in progress"))
                    .when(playerRepository).deleteById(playerId);

            assertThrows(IllegalStateException.class, () -> playerService.deletePlayerById(playerId));
        }
    }
}

