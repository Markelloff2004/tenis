package org.cedacri.pingpong.utils;

import com.vaadin.flow.component.notification.Notification;

import java.util.List;

public class Constants {

    private Constants() {
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
    public static final String ROBIN_ROUND_NOT_SUPPORTED = "At moment matches for this type of tournament cannot be generated";

    public static final String TOURNAMENT_CANNOT_BE_NULL = "Tournament cannot be null";
    public static final int MINIMAL_AMOUNT_OF_PLAYER_FOR_OLYMPIC = 8;
    public static final int MINIMAL_AMOUNT_OF_PLAYER_FOR_ROBIN_ROUND = 4;
    public static final String TOURNAMENT_WINNER_HAS_BEEN_DETERMINATED = "Tournament winner has benn determinated: ";
}
