package org.cedacri.pingpong.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.springframework.security.core.parameters.P;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    private static Validator validator;
    private Player player;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUpValidPlayer() {

        player = new Player();

        player.setName("John");
        player.setSurname("Doe");
        player.setBirthDate(LocalDate.of(1990, 1, 1));
        player.setAddress("123 Main St");
        player.setEmail("john.doe@example.com");
        player.setHand("RIGHT");
    }

    @Test
    void testValidPlayer() {
        Set<ConstraintViolation<Player>> violations = validator.validate(player);

        assertTrue(violations.isEmpty());
    }

    @Nested
    @DisplayName("Tests for invalid name")
    class InvalidNameTests {

        @Test
        void testNameTooShort() {
            player.setName("J");

            Set<ConstraintViolation<Player>> violations = validator.validate(player);

            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            assertEquals("Name must be between 2 and 50 characters", violations.iterator().next().getMessage());
        }

        @Test
        void testNameTooLong() {
            player.setName("UvuvwevwevweonyetenvewveugwemubwemvwevweonyetenvewveugwemubwemOssas");

            Set<ConstraintViolation<Player>> violations = validator.validate(player);

            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            assertEquals("Name must be between 2 and 50 characters", violations.iterator().next().getMessage());
        }

        @Test
        void testNameEmpty() {
            player.setName("");

            Set<ConstraintViolation<Player>> violations = validator.validate(player);

            violations.forEach(error -> System.out.println(error.getMessage()));

            assertFalse(violations.isEmpty());
            assertEquals(3, violations.size());

            List<String> errorMessages = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .toList();

            assertTrue(errorMessages.contains("Name cannot be null or blank"));
            assertTrue(errorMessages.contains("Name must contain only letters"));
            assertTrue(errorMessages.contains("Name must be between 2 and 50 characters"));
        }

        @Test
        void testNameBlank() {
            player.setName("      ");

            Set<ConstraintViolation<Player>> violations = validator.validate(player);

            assertFalse(violations.isEmpty());
            assertEquals(2, violations.size());

            List<String> errorMessages = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .toList();

            assertTrue(errorMessages.contains("Name cannot be null or blank"));
            assertTrue(errorMessages.contains("Name must contain only letters"));
        }

        @Test
        void testNameNull() {
            player.setName(null);

            Set<ConstraintViolation<Player>> violations = validator.validate(player);

            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            assertEquals("Name cannot be null or blank", violations.iterator().next().getMessage());
        }

        @Test
        void testNameWithControlCharacters() {
            player.setName("\n\t");

            Set<ConstraintViolation<Player>> violations = validator.validate(player);

            assertFalse(violations.isEmpty());

            assertEquals(2, violations.size());

            List<String> errorMessages = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .toList();

            assertTrue(errorMessages.contains("Name cannot be null or blank"));
            assertTrue(errorMessages.contains("Name must contain only letters"));
        }
    }

    @Nested
    @DisplayName("Tests for invalid surname")
    class InvalidSurnameTests {

        @Test
        void testSurnameTooShort() {
            player.setSurname("D");

            Set<ConstraintViolation<Player>> violations = validator.validate(player);

            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            assertEquals("Surname must be between 2 and 50 characters", violations.iterator().next().getMessage());
        }

        @Test
        void testSurnameBlank() {
            player.setSurname("    ");
            Set<ConstraintViolation<Player>> violations = validator.validate(player);
            assertFalse(violations.isEmpty());

            assertEquals(2, violations.size());

            List<String> errorMessages = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .toList();

            assertTrue(errorMessages.contains("Surname cannot be null or blank"));
            assertTrue(errorMessages.contains("Surname must contain only letters"));
        }

        @Test
        void testSurnameNull() {
            player.setSurname(null);
            Set<ConstraintViolation<Player>> violations = validator.validate(player);
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            assertEquals("Surname cannot be null or blank", violations.iterator().next().getMessage());
        }

        @Test
        void testSurnameWithControlCharacters() {
            player.setSurname("\n\t");

            Set<ConstraintViolation<Player>> violations = validator.validate(player);

            violations.forEach(error -> System.out.println(error.getMessage()));

            assertFalse(violations.isEmpty());
            assertEquals(2, violations.size());

            List<String> errorMessages = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .toList();

            assertTrue(errorMessages.contains("Surname cannot be null or blank"));
            assertTrue(errorMessages.contains("Surname must contain only letters"));
        }

    }

    @Nested
    @DisplayName("Tests for invalid email")
    class InvalidEmailTests {

        @Test
        void testInvalidEmail() {
            player.setEmail("invalid-email");
            Set<ConstraintViolation<Player>> violations = validator.validate(player);
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            assertEquals("Invalid email format", violations.iterator().next().getMessage());
        }

        @Test
        void testEmailBlank() {
            player.setEmail("");
            Set<ConstraintViolation<Player>> violations = validator.validate(player);
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            assertEquals("Email cannot be null or blank", violations.iterator().next().getMessage());
        }

        @Test
        void testEmailNull() {
            player.setEmail(null);
            Set<ConstraintViolation<Player>> violations = validator.validate(player);
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            assertEquals("Email cannot be null or blank", violations.iterator().next().getMessage());
        }
    }

    @Nested
    @DisplayName("Tests for invalid hand")
    class InvalidHandTests {

        @Test
        void testInvalidHand() {
            player.setHand("RightHand");
            Set<ConstraintViolation<Player>> violations = validator.validate(player);
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            assertEquals("Hand must be either 'RIGHT' or 'LEFT'", violations.iterator().next().getMessage());
        }

        @Test
        void testNullHand() {
            player.setHand(null);
            Set<ConstraintViolation<Player>> violations = validator.validate(player);
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            assertEquals("Select a hand playing style", violations.iterator().next().getMessage());
        }

        @Test
        void testBlankHand() {
            player.setHand("");
            Set<ConstraintViolation<Player>> violations = validator.validate(player);
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            assertEquals("Hand must be either 'RIGHT' or 'LEFT'", violations.iterator().next().getMessage());
        }
    }

    @Nested
    @DisplayName("Tests for invalid rating")
    class InvalidRatingTests {

        @Test
        void testNegativeRating() {
            player.setRating(-1);
            Set<ConstraintViolation<Player>> violations = validator.validate(player);
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            assertEquals("Rating cannot be negative", violations.iterator().next().getMessage());
        }

        @Test
        void testZeroRating() {
            player.setRating(0);
            Set<ConstraintViolation<Player>> violations = validator.validate(player);
            assertTrue(violations.isEmpty());
        }
    }

    @Nested
    @DisplayName("Tests for invalid matches")
    class InvalidMatchesTests {

        @Test
        void testNegativeWonMatches() {
            player.setWonMatches(-1);
            Set<ConstraintViolation<Player>> violations = validator.validate(player);
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            assertEquals("Won matches cannot be negative", violations.iterator().next().getMessage());
        }

        @Test
        void testNegativeLostMatches() {
            player.setLostMatches(-1);
            Set<ConstraintViolation<Player>> violations = validator.validate(player);
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            assertEquals("Lost matches cannot be negative", violations.iterator().next().getMessage());
        }

        @Test
        void testNegativeGoalsScored() {
            player.setGoalsScored(-1);
            Set<ConstraintViolation<Player>> violations = validator.validate(player);
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            assertEquals("Goals scored cannot be negative", violations.iterator().next().getMessage());
        }

        @Test
        void testNegativeGoalsLost() {
            player.setGoalsLost(-1);
            Set<ConstraintViolation<Player>> violations = validator.validate(player);
            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            assertEquals("Goals lost cannot be negative", violations.iterator().next().getMessage());
        }
    }

}
