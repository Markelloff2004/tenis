package org.cedacri.pingpong.views.playersview.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.utils.Constants;
import org.cedacri.pingpong.utils.ViewUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public abstract class AbstractPlayerDialog extends Dialog {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractPlayerDialog.class);

    protected TextField nameField;
    protected TextField surnameField;
    protected TextField emailField;
    protected TextField addressField;
    protected DatePicker birthDatePicker;
    protected ComboBox<String> handComboBox;

    public AbstractPlayerDialog(String headerTitle) {
        logger.info("Initializing {}", headerTitle);

        setHeaderTitle(headerTitle);
        setWidth("500px");
    }


    protected void initializeFields() {
        nameField = ViewUtils.createTextField("Name");
        nameField.setRequired(true);

        surnameField = ViewUtils.createTextField("Surname");
        surnameField.setRequired(true);

        emailField = ViewUtils.createTextField("Email");
        emailField.setRequired(true);

        addressField = ViewUtils.createTextField("Address");
        addressField.setRequired(true);

        birthDatePicker = ViewUtils.createDatePicker("Birth Date");
        birthDatePicker.setRequired(true);

        handComboBox = ViewUtils.createComboBox("Hand", Constants.PLAYING_HAND);
        handComboBox.setRequired(true);
    }

    protected void populateFields(Player player) {
        logger.info("Populating fields with player data: {} {}", player.getName(), player.getSurname());
        nameField.setValue(player.getName() != null ? player.getName() : "");
        surnameField.setValue(player.getSurname() != null ? player.getSurname() : "");
        emailField.setValue(player.getEmail() != null ? player.getEmail() : "");
        addressField.setValue(player.getAddress() != null ? player.getAddress() : "");
        birthDatePicker.setValue(player.getBirthDate() != null ? player.getBirthDate() : LocalDate.now());
        handComboBox.setValue(player.getHand() != null ? player.getHand() : "");
    }

    protected VerticalLayout createFieldsLayout() {
        VerticalLayout fields = ViewUtils.createVerticalLayout(FlexComponent.JustifyContentMode.START,
                nameField,
                surnameField,
                emailField,
                addressField,
                birthDatePicker,
                handComboBox
        );
//        fields.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
//        fields.setSpacing(false);
        fields.setPadding(false);

        return fields;
    }

    protected HorizontalLayout createButtonsLayout() {
        Button saveButton = ViewUtils.createButton("Save", "colored-button", this::onSave);
        Button cancelButton = ViewUtils.createButton("Cancel", "button", this::onCancel);

        return ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.CENTER, saveButton, cancelButton);
    }

    protected abstract void onSave();

    private void onCancel() {
        logger.info("Cancel button clicked. Closing dialog.");
        close();
    }
}

