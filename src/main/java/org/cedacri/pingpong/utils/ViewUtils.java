package org.cedacri.pingpong.utils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.ValueProvider;

import java.util.List;

public class ViewUtils
{
    public static Button createButton(String text, String className, Runnable clickListener) {
        Button button = new Button(text);
        button.addClassName(className);
        button.addClickListener(e -> clickListener.run());
        return button;
    }

    public static TextField createTextField(String label) {
        TextField textField = new TextField(label);
        textField.setWidth("250px");
        return textField;
    }

    public static IntegerField createIntegerField(String label) {
        IntegerField integerField = new IntegerField(label);
        integerField.setWidth("250px");
        integerField.setMin(0);
        return integerField;
    }

    public static ComboBox<String> createComboBox(String label, List<String> items) {
        ComboBox<String> comboBox = new ComboBox<>(label);
        comboBox.setItems(items);
        comboBox.setWidth("250px");
        return comboBox;
    }

    public static HorizontalLayout createHorizontalLayout(FlexComponent.JustifyContentMode justifyContentMode ,com.vaadin.flow.component.Component... components) {
        HorizontalLayout layout = new HorizontalLayout(components);
        layout.setSpacing(true);
        layout.setWidthFull();
        layout.setJustifyContentMode(justifyContentMode);
        return layout;
    }

    public static boolean showValidationError(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE);
        return false;
    }
}
