package org.cedacri.pingpong.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.TournamentOlympic;
import org.cedacri.pingpong.repository.MatchRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;

class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    private MatchService matchService;

    private TournamentOlympic tournamentOlympic;
    private Match match1;
    private Match match2;
    private Match match3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        matchService = new MatchService(matchRepository);

        tournamentOlympic = new TournamentOlympic();
        tournamentOlympic.setId(1);
        tournamentOlympic.setTournamentName("Tournament1");

        match1 = new Match();
        match1.setTournament(tournamentOlympic);
        match1.setRound(1);
        match1.setPosition(1);

        match2 = new Match();
        match2.setTournament(tournamentOlympic);
        match2.setRound(1);
        match2.setPosition(2);

        match3 = new Match();
        match3.setTournament(tournamentOlympic);
        match3.setRound(1);
        match3.setPosition(3);
    }

    @Nested
    @DisplayName("Tests for the getMatchesByTournamentAndRound method")
    class GetMatchesByTournamentAndRoundTestsOlympic {

        @Test
        void testReturnMatchesOrderedByPosition() {
            when(matchRepository.findByTournamentAndRound(tournamentOlympic, 1))
                    .thenReturn(Arrays.asList(match1, match2, match3));

            List<Match> result = matchService.getMatchesByTournamentAndRound(tournamentOlympic, 1);

            assertTrue(result.containsAll(Arrays.asList(match2, match3, match1)));
            verify(matchRepository, times(1)).findByTournamentAndRound(tournamentOlympic, 1);
        }

        @Test
        void testReturnEmptyListWhenNoMatchesExist() {
            when(matchRepository.findByTournamentAndRound(tournamentOlympic, 1))
                    .thenReturn(Collections.emptyList());

            List<Match> result = matchService.getMatchesByTournamentAndRound(tournamentOlympic, 1);

            assertTrue(result.isEmpty());
            verify(matchRepository, times(1)).findByTournamentAndRound(tournamentOlympic, 1);
        }

        @Test
        void testThrowsExceptionWhenTournamentIsNull() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                matchService.getMatchesByTournamentAndRound(null, 1);
            });
            assertEquals("Tournament cannot be null", exception.getMessage());
            verify(matchRepository, times(0)).findByTournamentAndRound(any(), anyInt());
        }

        @Test
        void testReturnSingleMatchWhenOneMatchExists() {
            Match singleMatch = new Match();
            singleMatch.setTournament(tournamentOlympic);
            singleMatch.setRound(1);
            singleMatch.setPosition(1);

            when(matchRepository.findByTournamentAndRound(tournamentOlympic, 1))
                    .thenReturn(Collections.singletonList(singleMatch));

            List<Match> result = matchService.getMatchesByTournamentAndRound(tournamentOlympic, 1);

            assertEquals(Collections.singletonList(singleMatch), result);
            verify(matchRepository, times(1)).findByTournamentAndRound(tournamentOlympic, 1);
        }

        @Test
        void testReturnSortedListWhenMultipleMatchesHaveSamePosition() {
            Match duplicateMatch1 = new Match();
            Match duplicateMatch2 = new Match();
            Match matchWithDifferentPosition = new Match();

            duplicateMatch1.setTournament(tournamentOlympic);
            duplicateMatch1.setRound(1);
            duplicateMatch1.setPosition(1);

            duplicateMatch2.setTournament(tournamentOlympic);
            duplicateMatch2.setRound(1);
            duplicateMatch2.setPosition(1);

            matchWithDifferentPosition.setTournament(tournamentOlympic);
            matchWithDifferentPosition.setRound(1);
            matchWithDifferentPosition.setPosition(2);

            when(matchRepository.findByTournamentAndRound(tournamentOlympic, 1))
                    .thenReturn(Arrays.asList(duplicateMatch1, duplicateMatch2, matchWithDifferentPosition));

            List<Match> result = matchService.getMatchesByTournamentAndRound(tournamentOlympic, 1);

            assertEquals(Arrays.asList(duplicateMatch1, duplicateMatch2, matchWithDifferentPosition), result);
            verify(matchRepository, times(1)).findByTournamentAndRound(tournamentOlympic, 1);
        }

        @Test
        void testReturnNoDuplicateMatches() {
            when(matchRepository.findByTournamentAndRound(tournamentOlympic, 1))
                    .thenReturn(Arrays.asList(match1, match1));

            List<Match> result = matchService.getMatchesByTournamentAndRound(tournamentOlympic, 1);

            assertEquals(Collections.singletonList(match1), result);
            verify(matchRepository, times(1)).findByTournamentAndRound(tournamentOlympic, 1);
        }

        @Test
        void testHandleNullRepositoryReturn() {
            when(matchRepository.findByTournamentAndRound(tournamentOlympic, 1))
                    .thenReturn(null);

            List<Match> result = matchService.getMatchesByTournamentAndRound(tournamentOlympic, 1);

            assertEquals(Collections.emptyList(), result);
            verify(matchRepository, times(1)).findByTournamentAndRound(tournamentOlympic, 1);
        }

        @Test
        void testHandleMultipleTournamentsWithSameRound() {
            TournamentOlympic tournamentOlympic1 = new TournamentOlympic();
            tournamentOlympic1.setId(1);
            tournamentOlympic1.setTournamentName("Tournament1");

            TournamentOlympic tournamentOlympic2 = new TournamentOlympic();
            tournamentOlympic2.setId(2);
            tournamentOlympic2.setTournamentName("Tournament2");

            Match match1ForTourney1 = new Match();
            Match match1ForTourney2 = new Match();

            match1ForTourney1.setTournament(tournamentOlympic);
            match1ForTourney1.setRound(1);
            match1ForTourney1.setPosition(1);

            match1ForTourney2.setTournament(tournamentOlympic);
            match1ForTourney2.setRound(1);
            match1ForTourney2.setPosition(2);

            when(matchRepository.findByTournamentAndRound(tournamentOlympic1, 1))
                    .thenReturn(Collections.singletonList(match1ForTourney1));
            when(matchRepository.findByTournamentAndRound(tournamentOlympic2, 1))
                    .thenReturn(Collections.singletonList(match1ForTourney2));

            List<Match> result1 = matchService.getMatchesByTournamentAndRound(tournamentOlympic1, 1);
            List<Match> result2 = matchService.getMatchesByTournamentAndRound(tournamentOlympic2, 1);

            assertEquals(Collections.singletonList(match1ForTourney1), result1);
            assertEquals(Collections.singletonList(match1ForTourney2), result2);
            verify(matchRepository, times(2)).findByTournamentAndRound(any(), anyInt());
        }

        @Test
        void testTournamentNameCaseInsensitive() {
            TournamentOlympic tournamentOlympicLowerCase = new TournamentOlympic();
            tournamentOlympicLowerCase.setTournamentName("tournament1");

            Match match = new Match();
            match.setTournament(tournamentOlympicLowerCase);
            match.setRound(1);
            match.setPosition(1);

            when(matchRepository.findByTournamentAndRound(tournamentOlympicLowerCase, 1))
                    .thenReturn(Collections.singletonList(match));

            List<Match> result = matchService.getMatchesByTournamentAndRound(tournamentOlympicLowerCase, 1);

            assertEquals(Collections.singletonList(match), result);
            verify(matchRepository, times(1)).findByTournamentAndRound(tournamentOlympicLowerCase, 1);
        }

        @Test
        void testInvalidRoundNumber() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                matchService.getMatchesByTournamentAndRound(tournamentOlympic, -1);
            });
            assertEquals("Invalid round number", exception.getMessage());

            exception = assertThrows(IllegalArgumentException.class, () -> {
                matchService.getMatchesByTournamentAndRound(tournamentOlympic, Integer.MAX_VALUE);
            });
            assertEquals("Invalid round number", exception.getMessage());
            verify(matchRepository, times(0)).findByTournamentAndRound(any(), anyInt());
        }
    }

    @Nested
    @DisplayName("Tests for the getMatchesByTournament method")
    class GetMatchesByTournamentTestsOlympic {

        @Test
        void testGetMatchesByTournamentSortedByPositionAndRound() {
            when(matchRepository.findByTournament(tournamentOlympic)).thenReturn(List.of(match1, match2, match3));

            List<Match> matches = matchService.getMatchesByTournament(tournamentOlympic);

            assertEquals(3, matches.size());
            assertEquals(match1, matches.get(0));
            assertEquals(match2, matches.get(1));
            assertEquals(match3, matches.get(2));
            verify(matchRepository, times(1)).findByTournament(tournamentOlympic);
        }

        @Test
        void testGetMatchesByTournamentEmptyListIfNoMatches() {
            when(matchRepository.findByTournament(tournamentOlympic)).thenReturn(List.of());

            List<Match> matches = matchService.getMatchesByTournament(tournamentOlympic);

            assertTrue(matches.isEmpty());
            verify(matchRepository, times(1)).findByTournament(tournamentOlympic);
        }

        @Test
        void testGetMatchesByTournamentThrowsExceptionIfTournamentIsNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                matchService.getMatchesByTournament(null);
            });
            verify(matchRepository, times(0)).findByTournament(any());
        }

        @Test
        void testGetMatchesByTournamentNullFromRepository() {
            when(matchRepository.findByTournament(tournamentOlympic)).thenReturn(null);

            List<Match> matches = matchService.getMatchesByTournament(tournamentOlympic);

            assertEquals(Collections.emptyList(), matches);
            verify(matchRepository, times(1)).findByTournament(tournamentOlympic);
        }

        @Test
        void testGetMatchesByTournamentOnlyMatchesFromTheCorrectTournament() {
            TournamentOlympic anotherTournamentOlympic = new TournamentOlympic();
            anotherTournamentOlympic.setId(2);
            anotherTournamentOlympic.setTournamentName("Tournament2");

            Match match4 = new Match();
            match4.setTournament(anotherTournamentOlympic);
            match4.setRound(1);
            match4.setPosition(1);

            when(matchRepository.findByTournament(tournamentOlympic)).thenReturn(List.of(match1, match2));
            when(matchRepository.findByTournament(anotherTournamentOlympic)).thenReturn(List.of(match4));

            List<Match> matchesFromTournament1 = matchService.getMatchesByTournament(tournamentOlympic);
            List<Match> matchesFromAnotherTournament = matchService.getMatchesByTournament(anotherTournamentOlympic);


            assertTrue(matchesFromTournament1.contains(match1));
            assertTrue(matchesFromTournament1.contains(match2));
            assertFalse(matchesFromTournament1.contains(match4));
            assertTrue(matchesFromAnotherTournament.contains(match4));
            verify(matchRepository, times(2)).findByTournament(any());
        }

        @Test
        void testGetMatchesByTournamentNoDuplicateMatches() {
            when(matchRepository.findByTournament(tournamentOlympic)).thenReturn(List.of(match1, match1, match2));

            List<Match> matches = matchService.getMatchesByTournament(tournamentOlympic);

            assertEquals(2, matches.size());
            verify(matchRepository, times(1)).findByTournament(tournamentOlympic);
        }

        @Test
        void testGetMatchesByTournamentHandlesNonSequentialRounds() {
            match2.setRound(3);
            match3.setRound(5);

            when(matchRepository.findByTournament(tournamentOlympic)).thenReturn(List.of(match1, match2, match3));

            List<Match> matches = matchService.getMatchesByTournament(tournamentOlympic);

            assertEquals(3, matches.size());
            assertEquals(match1, matches.get(0));
            assertEquals(match2, matches.get(1));
            assertEquals(match3, matches.get(2));
            verify(matchRepository, times(1)).findByTournament(tournamentOlympic);
        }
    }

    @Nested
    @DisplayName("Tests for the getMatchesByPlayerNameSurname method")
    class GetMatchesByPlayerNameSurnameTests {

        private Player topPlayer;
        private Player bottomPlayer;

        @BeforeEach
        void playerSetUp(){
            Player johnDoe = new Player();
            Player janeDoe = new Player();
            Player emilySmith = new Player();

            johnDoe.setName("John");
            johnDoe.setSurname("Doe");

            janeDoe.setName("Jane");
            janeDoe.setSurname("Doe");

            emilySmith.setName("Emily");
            emilySmith.setSurname("Smith");

            match1.setTopPlayer(johnDoe);
            match1.setBottomPlayer(janeDoe);

            match2.setTopPlayer(johnDoe);
            match2.setBottomPlayer(emilySmith);

            match3.setTopPlayer(janeDoe);
            match3.setBottomPlayer(johnDoe);

            topPlayer = johnDoe;
            bottomPlayer = janeDoe;
        }

        @Test
        void testGetMatchesByPlayerNameSurnameReturnsMatchesForJaneDoe() {
            when(matchRepository.findByTournament(tournamentOlympic)).thenReturn(List.of(match1, match2, match3));

            List<Match> matches = matchService.getMatchesByPlayerNameSurname(tournamentOlympic, "Jane", "Doe");

            assertEquals(2, matches.size());
            assertTrue(matches.contains(match1));
            assertTrue(matches.contains(match3));
            verify(matchRepository, times(1)).findByTournament(tournamentOlympic);
        }

        @Test
        void testGetMatchesByPlayerNameSurnameReturnsEmptyListIfNoMatches() {
            when(matchRepository.findByTournament(tournamentOlympic)).thenReturn(List.of(match1, match2, match3));

            List<Match> matches = matchService.getMatchesByPlayerNameSurname(tournamentOlympic, "Michael", "Johnson");

            assertTrue(matches.isEmpty());
            verify(matchRepository, times(1)).findByTournament(tournamentOlympic);
        }

        @Test
        void testGetMatchesByPlayerNameSurnameThrowsExceptionIfTournamentIsNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                matchService.getMatchesByPlayerNameSurname(null, "Doe", "John");
            });
            verifyNoInteractions(matchRepository);
        }

        @Test
        void testGetMatchesByPlayerNameSurnameThrowsExceptionIfPlayerNameIsNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                matchService.getMatchesByPlayerNameSurname(tournamentOlympic, null, "Doe");
            });
            verifyNoInteractions(matchRepository);
        }

        @Test
        void testGetMatchesByPlayerNameSurnameThrowsExceptionIfPlayerSurnameIsNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                matchService.getMatchesByPlayerNameSurname(tournamentOlympic, "John", null);
            });
            verifyNoInteractions(matchRepository);
        }

        @Test
        void testGetMatchesByPlayerNameSurnameCorrectlyDifferentiatesSimilarPlayerNames() {
            Player similarPlayer = new Player();
            similarPlayer.setName("John");
            similarPlayer.setSurname("Doer");

            when(matchRepository.findByTournament(tournamentOlympic)).thenReturn(List.of(match1));

            List<Match> matches = matchService.getMatchesByPlayerNameSurname(tournamentOlympic, "John", "Doer");

            assertTrue(matches.isEmpty());
            verify(matchRepository, times(1)).findByTournament(tournamentOlympic);
        }

        @Test
        void testGetMatchesByPlayerNameSurnameHandlesPlayerInMultipleRounds() {
            when(matchRepository.findByTournament(tournamentOlympic)).thenReturn(List.of(match1, match2, match3));

            List<Match> matches = matchService.getMatchesByPlayerNameSurname(tournamentOlympic, "John", "Doe");

            assertEquals(3, matches.size());
            verify(matchRepository, times(1)).findByTournament(tournamentOlympic);
        }

        @Test
        void testGetMatchesByPlayerNameSurnameHandlesNullFromRepository() {
            when(matchRepository.findByTournament(tournamentOlympic)).thenReturn(null);

            List<Match> matches = matchService.getMatchesByPlayerNameSurname(tournamentOlympic, "John", "Doe");

            assertEquals(Collections.emptyList(), matches);
            verify(matchRepository, times(1)).findByTournament(tournamentOlympic);
        }

        @Test
        void testGetMatchesByPlayerNameSurnameHandlesPlayerInMultipleTournaments() {
            TournamentOlympic anotherTournamentOlympic = new TournamentOlympic();
            anotherTournamentOlympic.setId(2);
            Match matchInAnotherTournament = new Match();
            matchInAnotherTournament.setTournament(anotherTournamentOlympic);
            matchInAnotherTournament.setTopPlayer(topPlayer);
            matchInAnotherTournament.setBottomPlayer(bottomPlayer);

            when(matchRepository.findByTournament(tournamentOlympic)).thenReturn(List.of(match1, match2));
            when(matchRepository.findByTournament(anotherTournamentOlympic)).thenReturn(List.of(matchInAnotherTournament));

            List<Match> matches = matchService.getMatchesByPlayerNameSurname(tournamentOlympic, "John", "Doe");
            List<Match> matchesFromAnotherT = matchService.getMatchesByPlayerNameSurname(anotherTournamentOlympic, "John", "Doe");

            assertEquals(2, matches.size());
            assertTrue(matches.contains(match1));
            assertTrue(matches.contains(match2));

            assertEquals(1, matchesFromAnotherT.size());
            assertTrue(matchesFromAnotherT.contains(matchInAnotherTournament));

            verify(matchRepository, times(1)).findByTournament(tournamentOlympic);
            verify(matchRepository, times(1)).findByTournament(anotherTournamentOlympic);
        }


        @Test
        void testGetMatchesByPlayerNameSurnameHandlesEmptyPlayerName() {
            when(matchRepository.findByTournament(tournamentOlympic)).thenReturn(List.of(match1, match2));

            List<Match> matches = matchService.getMatchesByPlayerNameSurname(tournamentOlympic, " ", "Doe");

            assertTrue(matches.isEmpty());  // No player should match empty name
            verify(matchRepository, times(1)).findByTournament(tournamentOlympic);
        }

        @Test
        void testGetMatchesByPlayerNameSurnameHandlesEmptyPlayerSurname() {
            when(matchRepository.findByTournament(tournamentOlympic)).thenReturn(List.of(match1, match2));

            List<Match> matches = matchService.getMatchesByPlayerNameSurname(tournamentOlympic, "John", " ");

            assertTrue(matches.isEmpty());  // No player should match empty surname
            verify(matchRepository, times(1)).findByTournament(tournamentOlympic);
        }
    }

    @Nested
    @DisplayName("Tests for the saveMatch method")
    class SaveMatchTests {

        @Test
        void shouldSaveValidMatch() {
            when(matchRepository.save(any(Match.class))).thenReturn(match1);

            Match savedMatch = matchService.saveMatch(match1);

            assertEquals(match1, savedMatch);
            verify(matchRepository).save(match1);
        }

        @Test
        void shouldUpdateExistingMatch() {
            when(matchRepository.save(any(Match.class))).thenReturn(match1);
            match1.setPosition(2);

            Match updatedMatch = matchService.saveMatch(match1);

            assertEquals(match1, updatedMatch);
            verify(matchRepository).save(match1);
        }

        @Test
        void shouldThrowExceptionWhenMatchIsNull() {
            assertThrows(IllegalArgumentException.class, () -> matchService.saveMatch(null));

            verifyNoInteractions(matchRepository);
        }

        @Test
        void shouldHandleInvalidMatchData() {
            Match invalidMatch = new Match();
            when(matchRepository.save(any(Match.class))).thenReturn(invalidMatch);

            Match result = matchService.saveMatch(invalidMatch);

            assertNotNull(result);
            verify(matchRepository).save(invalidMatch);
        }

        @Test
        void shouldHandleDuplicateMatchId() {
            match1.setId(1);
            when(matchRepository.save(any(Match.class))).thenThrow(new DataIntegrityViolationException("Duplicate key"));

            assertThrows(DataIntegrityViolationException.class, () -> matchService.saveMatch(match1));
            verify(matchRepository).save(match1);
        }
    }

    @Nested
    @DisplayName("Tests for the deleteMatch method")
    class DeleteMatchTests {

        @BeforeEach
        void setUp()
        {
            match1.setId(1);
            match2.setId(2);
            match3.setId(3);
        }

        @Test
        void shouldDeleteExistingMatchSuccessfully() {
            when(matchRepository.findById(match1.getId())).thenReturn(Optional.of(match1));
            doNothing().when(matchRepository).deleteById(match1.getId());

            assertDoesNotThrow(() -> matchService.deleteMatch(match1));
            verify(matchRepository, times(1)).deleteById(match1.getId());
        }

        @Test
        void shouldThrowExceptionWhenMatchIsNull() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> matchService.deleteMatch(null));
            assertEquals("Match ID cannot be null", exception.getMessage());
            verifyNoInteractions(matchRepository);
        }

        @Test
        void shouldThrowExceptionWhenMatchHasNoId() {
            Match matchWithoutId = new Match();
            matchWithoutId.setTournament(tournamentOlympic);
            matchWithoutId.setRound(1);
            matchWithoutId.setPosition(1);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> matchService.deleteMatch(matchWithoutId));
            assertEquals("Match ID cannot be null", exception.getMessage());
            verifyNoInteractions(matchRepository);
        }

        @Test
        void shouldThrowExceptionWhenRepositoryThrowsException() {
            when(matchRepository.findById(match1.getId())).thenReturn(Optional.of(match1));
            doThrow(new RuntimeException("Database error"))
                    .when(matchRepository).deleteById(match1.getId());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> matchService.deleteMatch(match1));
            assertEquals("Database error", exception.getMessage());
            verify(matchRepository).deleteById(match1.getId());
        }

        @Test
        void shouldDoNothingWhenMatchDoesNotExist() {
            when(matchRepository.findById(match1.getId())).thenReturn(Optional.empty());

            assertDoesNotThrow(() -> matchService.deleteMatch(match1));
            verify(matchRepository, never()).deleteById(any());
        }
    }

}
