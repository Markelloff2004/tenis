package org.cedacri.pingpong.utils;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationManager {

    private NotificationManager() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    static Logger log = LoggerFactory.getLogger(NotificationManager.class);

    public static void showInfoNotification(String message) {
        log.debug("Notification manager -> showInfoNotificationMethod");
        Notification notification = Notification.show(message, Constants.INFO_NOTIFICATION_DURATION, Constants.INFO_NOTIFICATION_POSITION);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    public static void showErrorNotification(String message) {
        log.debug("Notification manager -> showErrorNotificationMethod");
        Notification notification = Notification.show(message, Constants.ERROR_NOTIFICATION_DURATION, Constants.ERROR_NOTIFICATION_POSITION);
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
    }
}
