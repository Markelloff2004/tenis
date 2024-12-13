package org.cedacri.pingpong.utils;

public class TournamentUtils {

    public static int calculateMaxPlayers(int num) {
        if (num < 1) {
            throw new IllegalArgumentException("Number of players must be at least 1.");
        }

        // Find the nearest power of 2
        int maxPlayers = 1;
        while (maxPlayers < num) {
            maxPlayers *= 2;
        }

        return maxPlayers;
    }
}
