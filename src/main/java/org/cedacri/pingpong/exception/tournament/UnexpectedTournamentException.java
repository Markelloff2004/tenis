package org.cedacri.pingpong.exception.tournament;

import org.cedacri.pingpong.utils.Constants;

public class UnexpectedTournamentException extends Exception {
    public UnexpectedTournamentException(String message) {
        super(message);
    }

    public UnexpectedTournamentException(String message, Throwable cause) {
        super(message, cause);
    }
}
