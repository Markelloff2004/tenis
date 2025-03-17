package org.cedacri.pingpong.utils;

import com.vaadin.flow.component.notification.Notification;

import java.util.List;
import java.util.Map;

public class Constants
{

    private Constants()
    {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static final List<String> PLAYING_HAND = List.of("LEFT", "RIGHT");

    public static final String TOURNAMENT_UPDATE_SUCCESS_MESSAGE = "Tournament updated successfully";
    public static final String TOURNAMENT_UPDATE_ERROR = "Error updating tournament: ";

    public static final String TOURNAMENT_SAVE_SUCCESS_MESSAGE = "Tournament saved successfully";
    public static final String TOURNAMENT_SAVE_ERROR = "Error saving tournament: ";

    public static final String TOURNAMENT_START_SUCCESS_MESSAGE = "Tournament started successfully";
    public static final String TOURNAMENT_START_ERROR = "Error starting tournament: ";

    public static final String PLAYER_UPDATE_SUCCESS = "Player updated successfully";
    public static final String PLAYER_UPDATE_ERROR = "Error updating player: ";
    public static final String PLAYER_SAVE_SUCCESS = "Player saved successfully";
    public static final String PLAYER_SAVE_ERROR = "Error saving player: ";

    public static final String MATCH_UPDATE_SUCCESS = "Match updated successfully";
    public static final String MATCH_UPDATE_ERROR = "Error updating match: ";

    //Notification params
    //INFO
    public static final int INFO_NOTIFICATION_DURATION = 5000;
    public static final Notification.Position INFO_NOTIFICATION_POSITION = Notification.Position.TOP_CENTER;
    //ERROR
    public static final int ERROR_NOTIFICATION_DURATION = 8000;
    public static final Notification.Position ERROR_NOTIFICATION_POSITION = Notification.Position.TOP_CENTER;

    public static final int PAGE_SIZE = 10;
    public static final int MINIMAL_POINTS_IN_SET = 11;
    public static final int MINIMAL_DIFFERENCE_OF_POINTS_IN_SET = 2;

    //Exceptions
    public static final String NOT_ENOUGH_PLAYERS_MESSAGE = "Not enough players!";

    public static final String TOURNAMENT_CANNOT_BE_NULL = "Tournament cannot be null";
    public static final int MINIMAL_AMOUNT_OF_PLAYER_FOR_OLYMPIC = 8;
    public static final int MINIMAL_AMOUNT_OF_PLAYER_FOR_ROBIN_ROUND = 4;
    public static final String TOURNAMENT_WINNER_HAS_BEEN_DETERMINATED = "Tournament winner has been determinated: ";
    public static final String TOURNAMENT_WINNER_CANT_BE_DETERMINATED = "Tournament winner cant be determinated: ";

    // OLYMPIC_POSITIONS maps the number of players to their predefined seeding positions
    // for the first round in an Olympic-style tournament bracket.
    public static final Map<Integer, int[]> OLYMPIC_POSITIONS = Map.of(
            8, new int[]{1, 4, 2, 3},
            16, new int[]{1, 8, 4, 5, 2, 7, 3, 6},
            32, new int[]{1, 16, 9, 13, 4, 12, 5, 8, 2, 15, 10, 14, 3, 11, 6, 7},
            64, new int[]{1, 32, 16, 17, 9, 24, 13, 20, 4, 29, 12, 21, 5, 28, 8, 25,
                    2, 31, 15, 18, 10, 23, 14, 19, 3, 30, 11, 22, 6, 27, 7, 26},
            128, new int[]{1, 64, 32, 33, 16, 49, 17, 48, 9, 56, 24, 41, 25, 40, 8, 57,
                    5, 60, 28, 37, 12, 53, 21, 44, 13, 52, 20, 45, 29, 36, 4, 61, 3, 62, 30,
                    35, 14, 51, 19, 46, 11, 54, 22, 43, 27, 38, 6, 59, 7, 58, 26, 39, 10, 55,
                    23, 42, 15, 50, 18, 47, 31, 34, 63, 2
            }
    );

}
