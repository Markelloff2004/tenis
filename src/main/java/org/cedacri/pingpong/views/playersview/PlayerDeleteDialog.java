package org.cedacri.pingpong.views.playersview;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.utils.NotificationManager;
import org.cedacri.pingpong.utils.ViewUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactoryFriend;

public class PlayerDeleteDialog extends Dialog {

    private static final Logger logger = LoggerFactory.getLogger(PlayerDeleteDialog.class);

    public PlayerDeleteDialog(Player player, PlayerService playerService, Runnable onDeleteCallback) {
        logger.info("Initializing PlayerDeleteDialog for player: {} with Id: {}", player.getName() + " " + player.getSurname(), player.getId());

        setWidth("500px");
        setHeaderTitle("Confirm Delete");

        Span confirmationText = new Span("Are you sure you want to delete player " + player.getName() + " " +
                player.getSurname() + " with Id: " + player.getId() + "?");
        confirmationText.getStyle().set("margin", "10px 0");

        Button deleteButton = ViewUtils.createButton("Delete", "colored-button", () ->
        {
            logger.debug("Delete button clicked. Attempting to delete player: {} {} with Id: {}", player.getName(), player.getSurname(), player.getId());
            try {
                playerService.deleteById(player.getId());
                NotificationManager.showInfoNotification("Player " + player.getName() + " " + player.getSurname() +
                        ": " + player.getId() + " deleted!");
                close();

                logger.info("Player deleted successfully: {} {} , Id: {} ", player.getName(), player.getSurname(), player.getId());

                onDeleteCallback.run();
            }
            catch (Exception e)
            {
                logger.error("Error while deleting player {} {} with Id: {}", player.getName(), player.getSurname(), player.getId(), e);
                NotificationManager.showInfoNotification("Player cannot be deleted : " + e.getMessage());
            }
        });

        Button cancelButton = ViewUtils.createButton("Cancel", "button", () -> {
            logger.info("Cancel button clicked. Closing PlayerDeleteDialog.");
            close();
        } );

        HorizontalLayout buttonLayout = ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.CENTER, deleteButton, cancelButton);


//        add(ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.CENTER, confirmationText, buttonLayout, cancelButton));
        add(confirmationText, buttonLayout);
        logger.debug("PlayerDeleteDialog initialize completed.");
    }
}
