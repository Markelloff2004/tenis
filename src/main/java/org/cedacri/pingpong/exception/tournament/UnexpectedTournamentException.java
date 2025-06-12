package org.cedacri.pingpong.exception.tournament;

public class UnexpectedTournamentException extends Exception {
    public UnexpectedTournamentException(String message) {
        super(message);
    }

    public UnexpectedTournamentException(String message, Throwable cause) {
        super(message, cause);
    }
}
