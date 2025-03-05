package org.cedacri.pingpong.exception.tournament;

import org.cedacri.pingpong.utils.Constants;

public class NotEnoughPlayersException  extends  Exception{

    public NotEnoughPlayersException(){
        super(Constants.NOT_ENOUGH_PLAYERS_MESSAGE);
    }

    public NotEnoughPlayersException(int playersCount, int minAmountPlayers) {
        super(Constants.NOT_ENOUGH_PLAYERS_MESSAGE +  " " + playersCount + ", should be at least " + minAmountPlayers);
    }
}
