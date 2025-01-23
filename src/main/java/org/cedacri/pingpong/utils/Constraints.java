package org.cedacri.pingpong.utils;

import com.vaadin.flow.component.notification.Notification;

import java.util.List;

public class Constraints {

    public static final List<String> STATUS_OF_TOURNAMENTS = List.of("PENDING", "ONGOING", "FINISHED");
    public static final List<String> PLAYING_HAND = List.of("LEFT", "RIGHT");

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_ONGOING = "ONGOING";
    public static final String STATUS_FINISHED = "FINISHED";
    public static final List<String> TOURNAMENT_STATUSES = List.of(STATUS_PENDING, STATUS_ONGOING, STATUS_FINISHED);

    public static final String TOURNAMENT_UPDATE_SUCCESS = "Tournament updated successfully";
    public static final String TOURNAMENT_UPDATE_ERROR = "Encountered an error while updating tournament, please check the data!";

    //Notification params
    //INFO
    public static final int INFO_NOTIFICATION_DURATION = 5000;
    public static final Notification.Position INFO_NOTIFICATION_POSITION = Notification.Position.BOTTOM_CENTER;

    public static final int PAGE_SIZE = 10;

}
