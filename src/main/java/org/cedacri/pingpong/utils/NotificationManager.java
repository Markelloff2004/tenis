package org.cedacri.pingpong.utils;

import com.vaadin.flow.component.notification.Notification;

public class NotificationManager {
    public static void showInfoNotification(String message) {
        Notification.show(message, Constraints.INFO_NOTIFICATION_DURATION, Constraints.INFO_NOTIFICATION_POSITION);
    }
}
