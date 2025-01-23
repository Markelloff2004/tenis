package org.cedacri.pingpong.views.playersview;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.utils.NotificationManager;
import org.cedacri.pingpong.utils.ViewUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class PlayerEditDialog extends Dialog {

    private static final Logger logger = LoggerFactory.getLogger(PlayerEditDialog.class);

    public PlayerEditDialog(Player player, PlayerService playerService, Runnable onSaveCallback) {
        logger.info("Initializing PlayerEditDialog for player: {} with Id: {}", player.getName() + " " + player.getSurname(), player.getId());

        setWidth("600px");
        setHeight("auto");
        setHeaderTitle("Player Edit Dialog");

        logger.debug("Initializing fields for editing...");
        TextField nameField = new TextField();
        nameField.setValue(player.getName() != null ? player.getName() : "");
        nameField.setWidth("300px");

        TextField surnameField = new TextField();
        surnameField.setValue(player.getSurname() != null ? player.getSurname() : "");
        surnameField.setWidth("300px");

        TextField emailField = new TextField();
        emailField.setValue(player.getEmail() != null ? player.getEmail() : "");
        emailField.setWidth("300px");

        DatePicker birthDatePicker = new DatePicker();
        birthDatePicker.setValue(player.getBirthDate() != null ? player.getBirthDate() : LocalDate.now());
        birthDatePicker.setWidth("300px");

        TextField addressField = new TextField();
        addressField.setValue(player.getAddress() != null ? player.getAddress() : "");
        addressField.setWidth("300px");

        TextField handField = new TextField();
        handField.setValue(player.getHand() != null ? player.getHand() : "");
        handField.setWidth("300px");

        IntegerField ratingField = new IntegerField();
        ratingField.setValue(player.getRating() != null ? player.getRating() : 0);
        ratingField.setWidth("300px");

        IntegerField wonMatchesField = new IntegerField();
        wonMatchesField.setValue(player.getWonMatches() != null ? player.getWonMatches() : 0);
        wonMatchesField.setWidth("300px");

        IntegerField lostMatchesField = new IntegerField();
        lostMatchesField.setValue(player.getLostMatches() != null ? player.getLostMatches() : 0);
        lostMatchesField.setWidth("300px");

        IntegerField goalsScoredField = new IntegerField();
        goalsScoredField.setValue(player.getGoalsScored() != null ? player.getGoalsScored() : 0);
        goalsScoredField.setWidth("300px");

        IntegerField goalsLostField = new IntegerField();
        goalsLostField.setValue(player.getGoalsLost() != null ? player.getGoalsLost() : 0);
        goalsLostField.setWidth("300px");

        TextField createdAtField = new TextField();
        createdAtField.setValue(player.getCreatedAt() != null ? player.getCreatedAt().toString() : "N/A");
        createdAtField.setReadOnly(true);
        createdAtField.setWidth("300px");
        logger.debug("Initialized fields for editing.");

        logger.debug("Initializing a form layout for player: {} with Id: {}.", player.getName(), player.getId());
        FormLayout formLayout = new FormLayout();
        formLayout.addFormItem(nameField, "Name");
        formLayout.addFormItem(surnameField, "Surname");
        formLayout.addFormItem(emailField, "Email");
        formLayout.addFormItem(birthDatePicker, "Birth");
        formLayout.addFormItem(addressField, "Address");
        formLayout.addFormItem(handField, "Playing Hand");
        formLayout.addFormItem(ratingField, "Rating");
        formLayout.addFormItem(wonMatchesField, "Won Matches");
        formLayout.addFormItem(lostMatchesField, "Lost Matches");
        formLayout.addFormItem(goalsScoredField, "Goals Scored");
        formLayout.addFormItem(goalsLostField, "Goals Lost");
        formLayout.addFormItem(createdAtField, "Created At");

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );

        Button saveButton = ViewUtils.createButton("Save", "colored-button", () -> {
            logger.info("Save button clicked. Updating player details...");

            player.setName(nameField.getValue());
            player.setSurname(surnameField.getValue());
            player.setEmail(emailField.getValue());
            player.setAddress(addressField.getValue());
            player.setBirthDate(birthDatePicker.getValue());
            player.setHand(handField.getValue());
            player.setRating(ratingField.getValue());
            player.setWonMatches(wonMatchesField.getValue());
            player.setLostMatches(lostMatchesField.getValue());
            player.setGoalsScored(goalsScoredField.getValue());
            player.setGoalsLost(goalsLostField.getValue());

            try {
                playerService.save(player);
                logger.info("Player saved successfully: {} {}", player.getName(), player.getSurname());

                onSaveCallback.run();

                close();

                NotificationManager.showInfoNotification("Player updated successfully: " + player.getName() + " " +
                        player.getSurname() + ": " + player.getId());
            } catch (Exception e) {
                logger.error("Error saving player: {}", e.getMessage(), e);
                Notification.show("Player cannot be updated: " + e.getMessage(), 3000, Notification.Position.TOP_CENTER);
            }
        });

        Button cancelButton = ViewUtils.createButton("Cancel", "button", () -> {
            logger.info("Cancel button clicked. Closing PlayerEditDialog.");
            close();
        } );

        add(formLayout, ViewUtils.createHorizontalLayout(JustifyContentMode.CENTER, saveButton, cancelButton));
        logger.debug("PlayerEditDialog initialize completed.");
    }
}

