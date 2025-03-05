package org.cedacri.pingpong.views.playersview.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.utils.ExceptionUtils;
import org.cedacri.pingpong.utils.NotificationManager;
import org.cedacri.pingpong.utils.ViewUtils;

@Slf4j
public class PlayerDeleteDialog extends Dialog {

    public PlayerDeleteDialog(Player player, PlayerService playerService, Runnable onDeleteCallback) {
        log.info("Initializing PlayerDeleteDialog for player: {} with Id: {}", player.getName() + " " + player.getSurname(), player.getId());

        setWidth("500px");
        setHeaderTitle("Confirm Delete");

        Span confirmationText = new Span("Are you sure you want to delete player " + player.getName() + " " +
                player.getSurname() + " with Id: " + player.getId() + "?");
        confirmationText.getStyle().set("margin", "10px 0");

        Button deleteButton = ViewUtils.createButton("Delete", "colored-button", () ->
        {
            log.debug("Delete button clicked. Attempting to delete player: {} {} with Id: {}", player.getName(), player.getSurname(), player.getId());
            try {
                playerService.deletePlayerById(player.getId());

                log.info("Player deleted successfully: {} {} , Id: {} ", player.getName(), player.getSurname(), player.getId());

                close();

                NotificationManager.showInfoNotification("Player " + player.getName() + " " + player.getSurname() +
                        ": " + player.getId() + " deleted!");

                onDeleteCallback.run();
            } catch (Exception  e) {
                log.warn("Cannot delete player {} {} with Id: {}", player.getName(), player.getSurname(), player.getId());
                NotificationManager.showErrorNotification("Player cannot be deleted : " + ExceptionUtils.getExceptionMessage(e));
            }
        });

        Button cancelButton = ViewUtils.createButton("Cancel", "button", () -> {
            log.info("Cancel button clicked. Closing PlayerDeleteDialog.");
            close();
        });

        HorizontalLayout buttonLayout = ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.CENTER, deleteButton, cancelButton);


        add(confirmationText, buttonLayout);
        log.debug("PlayerDeleteDialog initialize completed.");
    }
}
