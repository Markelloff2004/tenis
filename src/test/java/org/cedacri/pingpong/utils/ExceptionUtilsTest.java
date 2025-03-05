package org.cedacri.pingpong.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.cedacri.pingpong.exception.tournament.NotEnoughPlayersException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExceptionUtilsTest {

    private final ExceptionUtils exceptionUtils = new ExceptionUtils();

    @Test
    void testHandleNotEnoughPlayersException() {
        NotEnoughPlayersException ex = new NotEnoughPlayersException(5, 8);

        ResponseEntity<String> response = exceptionUtils.handleNotEnoughPlayersException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Not enough players! 5, should be at least 8", response.getBody());
    }

    @Test
    void testGetExceptionMessageWithConstraintViolationException() {
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        when(violation1.getMessage()).thenReturn("Player name must not be empty.");

        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
        when(violation2.getMessage()).thenReturn("Player rating must be positive.");

        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation1, violation2));

        String message = ExceptionUtils.getExceptionMessage(ex);

        assertEquals("Player name must not be empty.", message);
    }

    @Test
    void testGetExceptionMessageWithEmptyConstraintViolationException() {
        ConstraintViolationException ex = new ConstraintViolationException(Set.of());

        String message = ExceptionUtils.getExceptionMessage(ex);

        assertEquals("Validation error occurred", message);
    }

    @Test
    void testGetExceptionMessageWithGenericException() {
        Exception ex = new Exception("An unexpected error occurred.");

        String message = ExceptionUtils.getExceptionMessage(ex);

        assertEquals("An unexpected error occurred.", message);
    }
}
