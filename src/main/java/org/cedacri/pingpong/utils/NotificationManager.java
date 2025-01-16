package org.cedacri.pingpong.utils;

import com.vaadin.flow.component.notification.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationManager {

    static Logger logger = LoggerFactory.getLogger(NotificationManager.class);

    public static void showInfoNotification(String message) {

        logger.debug("Notification manager -> showInfoNotificationMethod");
        Notification.show(message, Constraints.INFO_NOTIFICATION_DURATION, Constraints.INFO_NOTIFICATION_POSITION);
    }
}
