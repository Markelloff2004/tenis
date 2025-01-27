package org.cedacri.pingpong.views.playersview.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.utils.Constraints;
import org.cedacri.pingpong.utils.NotificationManager;
import org.cedacri.pingpong.utils.ViewUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class PlayerSaveDialog extends AbstractPlayerDialog {

    private PlayerService playerService;
    private Runnable onSaveCallback;
    private Player player;

    public PlayerSaveDialog(PlayerService playerService, Runnable onSaveCallback) {
        super("Save Player");
        this.playerService = playerService;
        this.onSaveCallback = onSaveCallback;
        this.player = new Player();

        initializeFields();
        add(createFieldsLayout(), createButtonsLayout());

        logger.debug("Initializing fields for editing...");

        populateFields(player);
    }

    @Override
    protected void onSave() {
        logger.info("Saving player: {} {}", player.getName(), player.getSurname());
        player.setName(nameField.getValue());
        player.setSurname(surnameField.getValue());
        player.setEmail(emailField.getValue());
        player.setAddress(addressField.getValue());
        player.setBirthDate(birthDatePicker.getValue());
        player.setHand(handComboBox.getValue());

        try {
            playerService.save(player);
            onSaveCallback.run();
            close();
            NotificationManager.showInfoNotification("Player updated successfully!");
        } catch (Exception e) {
            logger.error("Error saving player: {}", e.getMessage());
            Notification.show("Error updating player: " + e.getMessage());
        }
    }
}