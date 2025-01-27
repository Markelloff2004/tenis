package org.cedacri.pingpong.views.playersview.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.utils.ViewUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerInfoDialog extends PlayerEditDialog {


    public PlayerInfoDialog(Player player, PlayerService playerService, Runnable onSaveCallback) {
        super(player, playerService, onSaveCallback);

        setReadOnlyForFields();
    }

    protected void setReadOnlyForFields() {
        nameField.setReadOnly(true);
        surnameField.setReadOnly(true);
        emailField.setReadOnly(true);
        addressField.setReadOnly(true);
        birthDatePicker.setReadOnly(true);
        handComboBox.setReadOnly(true);
        ratingField.setReadOnly(true);
        wonMatchesField.setReadOnly(true);
        lostMatchesField.setReadOnly(true);
        goalsLostField.setReadOnly(true);
        goalsScoredField.setReadOnly(true);
    }

    @Override
    protected HorizontalLayout createButtonsLayout() {
        Button cancelButton = ViewUtils.createButton(
                "Cancel",
                "button",
                this::close
        );

        return ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.CENTER, cancelButton);
    }



}
