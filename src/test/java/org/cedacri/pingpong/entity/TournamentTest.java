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

class TournamentTest {

    private static Validator validator;
    private Tournament tournament;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUpValidTournament() {
        tournament = new Tournament();
        tournament.setTournamentName("Grand Championship");
        tournament.setMaxPlayers(16);
        tournament.setTournamentStatus(TournamentStatusEnum.ONGOING);
        tournament.setTournamentType(TournamentTypeEnum.OLYMPIC);
        tournament.setSetsToWin(SetTypesEnum.BEST_OF_THREE);
        tournament.setSemifinalsSetsToWin(SetTypesEnum.BEST_OF_FIVE);
        tournament.setFinalsSetsToWin(SetTypesEnum.BEST_OF_FIVE);
        tournament.setCreatedAt(LocalDate.now());
        tournament.setPlayers(new HashSet<>());
        tournament.setMatches(new HashSet<>());
    }



    @Nested
    @DisplayName("Tests for tournament name")
    class TournamentNameTests {

        @Test
        void testTournamentNameNull() {
            tournament.setTournamentName(null);
            Set<ConstraintViolation<Tournament>> violations = validator.validate(tournament);
            assertFalse(violations.isEmpty());
            assertEquals("Tournament name cannot be null or blank", violations.iterator().next().getMessage());
        }

        @Test
        void testTournamentNameBlank() {
            tournament.setTournamentName("   ");
            Set<ConstraintViolation<Tournament>> violations = validator.validate(tournament);
            assertFalse(violations.isEmpty());
            assertEquals("Tournament name cannot be null or blank", violations.iterator().next().getMessage());
        }
    }

    @Nested
    @DisplayName("Tests for max players")
    class MaxPlayersTests {

        @Test
        void testMaxPlayersNull() {
            tournament.setMaxPlayers(null);
            Set<ConstraintViolation<Tournament>> violations = validator.validate(tournament);
            assertFalse(violations.isEmpty());
            assertEquals("Max amount of players should be calculated", violations.iterator().next().getMessage());
        }
    }

    @Nested
    @DisplayName("Tests for tournament status")
    class TournamentStatusTests {

        @Test
        void testTournamentStatusNull() {
            tournament.setTournamentStatus(null);
            Set<ConstraintViolation<Tournament>> violations = validator.validate(tournament);
            assertFalse(violations.isEmpty());
            assertEquals("Tournament status cannot be null", violations.iterator().next().getMessage());
        }
    }

    @Nested
    @DisplayName("Tests for tournament type")
    class TournamentTypeTests {

        @Test
        void testTournamentTypeNull() {
            tournament.setTournamentType(null);
            Set<ConstraintViolation<Tournament>> violations = validator.validate(tournament);
            assertFalse(violations.isEmpty());
            assertEquals("Tournament type cannot be null", violations.iterator().next().getMessage());
        }
    }

    @Nested
    @DisplayName("Tests for tournament sets")
    class TournamentSetsTests {

        @Test
        void testSetsToWinNull() {
            tournament.setSetsToWin(null);
            Set<ConstraintViolation<Tournament>> violations = validator.validate(tournament);
            assertFalse(violations.isEmpty());
            assertEquals("Tournament sets cannot be null", violations.iterator().next().getMessage());
        }

        @Test
        void testSemifinalsSetsToWinNull() {
            tournament.setSemifinalsSetsToWin(null);
            Set<ConstraintViolation<Tournament>> violations = validator.validate(tournament);
            assertFalse(violations.isEmpty());
            assertEquals("Tournament semifinals sets cannot be null", violations.iterator().next().getMessage());
        }

        @Test
        void testFinalsSetsToWinNull() {
            tournament.setFinalsSetsToWin(null);
            Set<ConstraintViolation<Tournament>> violations = validator.validate(tournament);
            assertFalse(violations.isEmpty());
            assertEquals("Tournament final sets cannot be null", violations.iterator().next().getMessage());
        }
    }
}
