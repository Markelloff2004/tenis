package org.cedacri.pingpong.views.playersview.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
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

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

public class PlayerSaveDialog extends Dialog {

    private static final Logger logger = LoggerFactory.getLogger(PlayerSaveDialog.class);

    public PlayerSaveDialog(PlayerService playerService, Runnable onSaveCallback) {
        logger.info("Initializing PlayerSaveDialog for new player ");

        setWidth("400px");
        setHeight("auto");
        setHeaderTitle("Save Player");

        logger.debug("Initializing fields for player...");

        TextField nameField = new TextField();
        nameField.setRequired(true);
        nameField.setWidth("300px");

        TextField surnameField = new TextField();
        surnameField.setRequired(true);
        surnameField.setWidth("300px");

        DatePicker birthDateTimePicker = new DatePicker();
        birthDateTimePicker.setWidth("300px");

        TextField emailField = new TextField();
        emailField.setWidth("300px");

        TextField addressField = new TextField();
        addressField.setWidth("300px");

        ComboBox<String> handComboBox = new ComboBox<>();
        handComboBox.setItems(Constraints.PLAYING_HAND);
        handComboBox.setWidth("300px");

        FormLayout formLayout = new FormLayout();
        formLayout.addFormItem(nameField, "Name").getStyle().set("flex-direction", "column").set("margin-bottom", "5px");
        formLayout.addFormItem(surnameField, "Surname").getStyle().set("flex-direction", "column").set("margin-bottom", "5px");
        formLayout.addFormItem(birthDateTimePicker, "Birth Date").getStyle().set("flex-direction", "column").set("margin-bottom", "5px");
        formLayout.addFormItem(addressField, "Address").getStyle().set("flex-direction", "column").set("margin-bottom", "5px");
        formLayout.addFormItem(emailField, "Email").getStyle().set("flex-direction", "column").set("margin-bottom", "5px");
        formLayout.addFormItem(handComboBox, "Playing Hand").getStyle().set("flex-direction", "column").set("margin-bottom", "5px");


        Button saveButton = ViewUtils.createButton("Save", "colored-button", () ->
        {
            logger.info("Save button clicked. Attempting to save player ");

            String name = nameField.getValue();
            String surname = surnameField.getValue();
            String email = emailField.getValue();
            LocalDate birthDate = birthDateTimePicker.getValue();
            String address = addressField.getValue();
            String hand = handComboBox.getValue();

//            if (name.isBlank() || name.isEmpty() || surname.isEmpty() || birthDate.toString().isEmpty() || email.isEmpty() || hand.isEmpty()) {
//                NotificationManager.showInfoNotification("Please fill in all required fields.");
//                logger.warn("Player creation failed: Empty fields are present");
//                return;
//            }

            Player newPlayer = new Player(name, surname, birthDate, email, address, Date.from(Instant.now()), 0, hand, 0, 0, 0, 0);

            try
            {
                playerService.save(newPlayer);
                logger.info("Player saved successfully: {} {}", name, surname);

                onSaveCallback.run();

                close();

                NotificationManager.showInfoNotification("Player added successfully: " + newPlayer.getName() + " " + newPlayer.getSurname());
            }
            catch (Exception e)
            {
                logger.error("Error creating player {} {} : {}", name, surname, e.getMessage(), e);
                NotificationManager.showInfoNotification("Player cannot be added : " + e.getMessage());
            }
        });
        saveButton.setWidth("100px");

        Button cancelButton = ViewUtils.createButton("Cancel", "button", () -> {
            logger.info("Cancel button clicked. Closing PlayerSaveDialog.");
            this.close();
        } );

        cancelButton.setWidth("100px");

        HorizontalLayout buttonLayout = ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.CENTER, saveButton, cancelButton);
        buttonLayout.getStyle().set("margin-top", "10px");

        add(formLayout, buttonLayout);
    }
}
