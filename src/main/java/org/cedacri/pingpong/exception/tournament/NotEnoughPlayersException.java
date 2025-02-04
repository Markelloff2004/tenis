package org.cedacri.pingpong.exception.tournament;

import org.cedacri.pingpong.utils.Constraints;

public class NotEnoughPlayersException  extends  Exception{

    public NotEnoughPlayersException(){
        super(Constraints.NOT_ENOUGH_PLAYERS_MESSAGE);
    }

    public NotEnoughPlayersException(int playersCount) {
        super(Constraints.NOT_ENOUGH_PLAYERS_MESSAGE +  " " + playersCount + ", should be at least 8");
    }
}
