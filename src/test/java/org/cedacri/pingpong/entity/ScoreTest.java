package org.cedacri.pingpong.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Tests for valid Score")
    class ValidScoreTests {

        @Test
        void testValidScore() {
            Score score = new Score(3, 2);
            Set<ConstraintViolation<Score>> violations = validator.validate(score);
            assertTrue(violations.isEmpty(), "There should be no validation errors for a valid score");
        }
    }

    @Nested
    @DisplayName("Tests for invalid Score")
    class InvalidScoreTests {

        @Test
        void testNegativeTopPlayerScore() {
            Score score = new Score(-1, 2);
            Set<ConstraintViolation<Score>> violations = validator.validate(score);
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            assertEquals("Score cannot be negative", violations.iterator().next().getMessage());
        }

        @Test
        void testNegativeBottomPlayerScore() {
            Score score = new Score(2, -1);
            Set<ConstraintViolation<Score>> violations = validator.validate(score);
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            assertEquals("Score cannot be negative", violations.iterator().next().getMessage());
        }

        @Test
        void testBothScoresNegative() {
            Score score = new Score(-3, -2);
            Set<ConstraintViolation<Score>> violations = validator.validate(score);
            assertFalse(violations.isEmpty());
            assertEquals(2, violations.size());
        }
    }
}
