package org.cedacri.pingpong.entity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.mockito.Mockito;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class MatchTest {

    private static Validator validator;
    private Match match;

    private final Tournament mockTournament = Mockito.mock(Tournament.class);;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUpValidMatch() {

        match = new Match();
        match.setTournament(mockTournament);
        match.setRound(1);
    }

    @Nested
    @DisplayName("Tests for round field")
    class RoundTests {

        @Test
        void testValidRound() {
            match.setRound(5);
            Set<ConstraintViolation<Match>> violations = validator.validate(match);
            assertTrue(violations.isEmpty());
        }

        @Test
        void testNullRound() {
            match.setRound(null);
            Set<ConstraintViolation<Match>> violations = validator.validate(match);
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            assertEquals("Round cannot be null or empty", violations.iterator().next().getMessage());
        }

        @Test
        void testNegativeRound() {
            match.setRound(-1);
            Set<ConstraintViolation<Match>> violations = validator.validate(match);
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            assertEquals("Round cannot be less than 1", violations.iterator().next().getMessage());
        }
    }

    @Nested
    @DisplayName("Tests for tournament field")
    class TournamentTests {
        @Test
        void testNullTournament() {
            match.setTournament(null);
            Set<ConstraintViolation<Match>> violations = validator.validate(match);
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            assertEquals("TournamentId cannot be null", violations.iterator().next().getMessage());
        }
    }

    @Nested
    @DisplayName("Tests for players")
    class PlayerTests {

        @Test
        void testNullTopPlayer() {
            match.setTopPlayer(null);
            Set<ConstraintViolation<Match>> violations = validator.validate(match);
            assertTrue(violations.isEmpty()); // No explicit constraint on topPlayer
        }

        @Test
        void testNullBottomPlayer() {
            match.setBottomPlayer(null);
            Set<ConstraintViolation<Match>> violations = validator.validate(match);
            assertTrue(violations.isEmpty()); // No explicit constraint on bottomPlayer
        }
    }
}
