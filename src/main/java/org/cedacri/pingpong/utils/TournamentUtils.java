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

    public static Integer getRoundCount(String type) {
        if (type == null || type.isEmpty())
        {
            throw new IllegalArgumentException("Tournament Type cannot be null or empty");
        }

        switch (type.toUpperCase()) {
            case "BESTOFTHREE": return 3;
            case "BESTOFFIVE": return 5;
            case "BESTOFSEVEN": return 7;
            default: return 1;
        }
    }
}
