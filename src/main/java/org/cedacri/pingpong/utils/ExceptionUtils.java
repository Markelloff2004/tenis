package org.cedacri.pingpong.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.exception.tournament.NotEnoughPlayersException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
public class ExceptionUtils {

    public ResponseEntity<String> handleNotEnoughPlayersException(NotEnoughPlayersException ex) {
        log.error("Not enough players: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    public static String getExceptionMessage(Exception ex) {
        if (ex instanceof ConstraintViolationException) {
            return ((ConstraintViolationException) ex).getConstraintViolations()
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .findFirst()
                    .orElse("Validation error occurred");
        }

        return ex.getMessage();
    }
}
