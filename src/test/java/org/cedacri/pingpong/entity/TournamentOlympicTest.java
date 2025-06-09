package org.cedacri.pingpong.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.cedacri.pingpong.enums.SetTypesEnum;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class TournamentOlympicTest {

    private static Validator validator;
    private TournamentOlympic tournamentOlympic;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUpValidTournament() {
        tournamentOlympic = new TournamentOlympic();
        tournamentOlympic.setTournamentName("Grand Championship");
        tournamentOlympic.setMaxPlayers(16);
        tournamentOlympic.setTournamentStatus(TournamentStatusEnum.ONGOING);
        tournamentOlympic.setTournamentType(TournamentTypeEnum.OLYMPIC);
        tournamentOlympic.setSetsToWin(SetTypesEnum.BEST_OF_THREE);
        tournamentOlympic.setSemifinalsSetsToWin(SetTypesEnum.BEST_OF_FIVE);
        tournamentOlympic.setFinalsSetsToWin(SetTypesEnum.BEST_OF_FIVE);
        tournamentOlympic.setCreatedAt(LocalDate.now());
        tournamentOlympic.setPlayers(new HashSet<>());
        tournamentOlympic.setMatches(new HashSet<>());
    }



    @Nested
    @DisplayName("Tests for tournament name")
    class TournamentOlympicNameTests {

        @Test
        void testTournamentNameNull() {
            tournamentOlympic.setTournamentName(null);
            Set<ConstraintViolation<TournamentOlympic>> violations = validator.validate(tournamentOlympic);
            assertFalse(violations.isEmpty());
            assertEquals("Tournament name cannot be null or blank", violations.iterator().next().getMessage());
        }

        @Test
        void testTournamentNameBlank() {
            tournamentOlympic.setTournamentName("   ");
            Set<ConstraintViolation<TournamentOlympic>> violations = validator.validate(tournamentOlympic);
            assertFalse(violations.isEmpty());
            assertEquals("Tournament name cannot be null or blank", violations.iterator().next().getMessage());
        }
    }

    @Nested
    @DisplayName("Tests for max players")
    class MaxPlayersTests {

        @Test
        void testMaxPlayersNull() {
            tournamentOlympic.setMaxPlayers(null);
            Set<ConstraintViolation<TournamentOlympic>> violations = validator.validate(tournamentOlympic);
            assertFalse(violations.isEmpty());
            assertEquals("Max amount of players should be calculated", violations.iterator().next().getMessage());
        }
    }

    @Nested
    @DisplayName("Tests for tournament status")
    class TournamentOlympicStatusTests {

        @Test
        void testTournamentStatusNull() {
            tournamentOlympic.setTournamentStatus(null);
            Set<ConstraintViolation<TournamentOlympic>> violations = validator.validate(tournamentOlympic);
            assertFalse(violations.isEmpty());
            assertEquals("Tournament status cannot be null", violations.iterator().next().getMessage());
        }
    }

    @Nested
    @DisplayName("Tests for tournament type")
    class TournamentOlympicTypeTests {

        @Test
        void testTournamentTypeNull() {
            tournamentOlympic.setTournamentType(null);
            Set<ConstraintViolation<TournamentOlympic>> violations = validator.validate(tournamentOlympic);
            assertFalse(violations.isEmpty());
            assertEquals("Tournament type cannot be null", violations.iterator().next().getMessage());
        }
    }

    @Nested
    @DisplayName("Tests for tournament sets")
    class TournamentOlympicSetsTests {

        @Test
        void testSetsToWinNull() {
            tournamentOlympic.setSetsToWin(null);
            Set<ConstraintViolation<TournamentOlympic>> violations = validator.validate(tournamentOlympic);
            assertFalse(violations.isEmpty());
            assertEquals("Tournament sets cannot be null", violations.iterator().next().getMessage());
        }

        @Test
        void testSemifinalsSetsToWinNull() {
            tournamentOlympic.setSemifinalsSetsToWin(null);
            Set<ConstraintViolation<TournamentOlympic>> violations = validator.validate(tournamentOlympic);
            assertFalse(violations.isEmpty());
            assertEquals("Tournament semifinals sets cannot be null", violations.iterator().next().getMessage());
        }

        @Test
        void testFinalsSetsToWinNull() {
            tournamentOlympic.setFinalsSetsToWin(null);
            Set<ConstraintViolation<TournamentOlympic>> violations = validator.validate(tournamentOlympic);
            assertFalse(violations.isEmpty());
            assertEquals("Tournament final sets cannot be null", violations.iterator().next().getMessage());
        }
    }
}
