package org.cedacri.pingpong.interfaces;

public interface ITournamentRoundRobin extends ITournament {

    Integer getMaxAmountOfPlayersInGroup();

    Integer getMaxAmountOfPlayersInFinals();

    Integer getNumOfSteps();

}
