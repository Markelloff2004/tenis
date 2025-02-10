package org.cedacri.pingpong.utils;

import jakarta.validation.ConstraintViolationException;
import org.cedacri.pingpong.exception.tournament.NotEnoughPlayersException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ExceptionUtils {

    private static final Logger log = LoggerFactory.getLogger(ExceptionUtils.class);

    public ResponseEntity<String> handleNotEnoughPlayersException(NotEnoughPlayersException ex) {
        log.error("Not enough players: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    public static String getExceptionMessage(Exception ex) {
        if (ex instanceof ConstraintViolationException) {
            String errorMessage = ((ConstraintViolationException) ex).getConstraintViolations()
                    .stream()
                    .map(error -> error.getMessage())
                    .findFirst()
                    .orElse("Validation error occurred");
            return errorMessage;
        }

        return ex.getMessage();
    }
}
