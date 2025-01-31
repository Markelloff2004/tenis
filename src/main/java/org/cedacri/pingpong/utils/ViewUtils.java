package org.cedacri.pingpong.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ViewUtils
{

    private static final Logger logger = LoggerFactory.getLogger(ViewUtils.class);

    public static Button createButton(String text, String className, Runnable clickListener) {

        logger.debug("Creating button with text: '{}' and className: '{}'", text, className);

        Button button = new Button(text);
        button.addClassName(className);
        button.addClickListener(e -> {
            logger.info("Button with text: '{}' clicker", text);
            clickListener.run();
        });
        logger.debug("Button created: {}", button);
        return button;
    }

    public static <T extends Component> void highlightSelectedComponentFromComponentsList(List<T> components, int selectedIndex, String selectedClass) {
        if (selectedIndex < 0 || selectedIndex >= components.size()) {
            return;
        }

        for (int i = 0; i < components.size(); i++) {
            if (i == selectedIndex) {
                components.get(i).addClassName(selectedClass);
            } else {
                components.get(i).removeClassName(selectedClass);
            }
        }
    }


    public static TextField createTextField(String label) {
        logger.debug("Creating TextField with label: '{}' and width: '{}'", label);

        TextField textField = new TextField(label);
        textField.setWidth("250px");
        logger.debug("TextField created: {}", textField);
        return textField;
    }

    public static ComboBox<String> createComboBox(String label, List<String> items) {
        logger.debug("Creating ComboBox with label: '{}' and items: '{}'", label, items);

        ComboBox<String> comboBox = new ComboBox<>(label);
        comboBox.setItems(items);
        comboBox.setWidth("250px");
        logger.debug("ComboBox created: {}", comboBox);
        return comboBox;
    }

    public static Checkbox createCheckBox(String label) {
        logger.debug("Creating Checkbox with label: '{}' ",label);

        Checkbox checkbox = new Checkbox(label);
        checkbox.setWidth("250px");
        logger.debug("Checkbox created: {}", checkbox);
        return checkbox;
    }

    public static DatePicker createDatePicker(String label) {
        logger.debug("Creating DatePicker with label: '{}'", label);

        DatePicker datePicker = new DatePicker(label);
        datePicker.setWidth("250px");
        logger.debug("DatePicker created: {}", datePicker);
        return datePicker;
    }

    public static IntegerField createIntegerField(String label) {
        logger.debug("Creating IntegerField with label: '{}'", label);

        IntegerField integerField = new IntegerField(label);
        integerField.setWidth("250px");
        logger.debug("IntegerField created: {}", integerField);
        return integerField;
    }

    /*public static ComboBox<TournamentType> createComboBox(String label, TournamentType type) {
        logger.debug("Creating ComboBox with label: '{}' and type: '{}'", label, type);

        ComboBox<TournamentType> comboBox = new ComboBox<>(label);
        comboBox.setItems(TournamentType.values());
        comboBox.setWidth("250px");
        logger.debug("ComboBox created: {}", comboBox);
        return comboBox;
    }*/

    public static TextField createReadOnlyField(String label, String value) {
        logger.debug("Creating ReadOnly TextField with label: '{}' and value: '{}'", label, value);
        TextField textField = new TextField(label);
        textField.setValue(value != null ? value : "N/A");
        textField.setReadOnly(true);
        textField.setWidth("300px");
        logger.debug("ReadOnly TextField created: {}", textField);
        return textField;
    }

    public static HorizontalLayout createHorizontalLayout(FlexComponent.JustifyContentMode justifyContentMode ,com.vaadin.flow.component.Component... components) {
        logger.debug("Creating HorizontalLayout with justifyContentMode: '{}' and components: {}", justifyContentMode, components);
        HorizontalLayout layout = new HorizontalLayout(components);
        layout.setSpacing(true);
        layout.setWidthFull();
        layout.setJustifyContentMode(justifyContentMode);
        layout.setAlignItems(FlexComponent.Alignment.BASELINE);
        logger.debug("HorizontalLayout created: {}", layout);
        return layout;
    }

    public static VerticalLayout createVerticalLayout(FlexComponent.JustifyContentMode justifyContentMode , com.vaadin.flow.component.Component... components) {
        logger.debug("Creating HorizontalLayout with justifyContentMode: '{}' and components: {}", justifyContentMode, components);
        VerticalLayout layout = new VerticalLayout(components);
        layout.setSpacing(true);
        layout.setWidthFull();
        layout.setJustifyContentMode(justifyContentMode);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        logger.debug("HorizontalLayout created: {}", layout);
        return layout;
    }

    public static boolean showValidationError(String message) {
        logger.error("Validation error: {}", message);
        Notification.show(message, 3000, Notification.Position.MIDDLE);
        return false;
    }
}
