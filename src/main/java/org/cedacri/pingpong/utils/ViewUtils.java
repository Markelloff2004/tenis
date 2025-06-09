package org.cedacri.pingpong.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.config.security.utils.SecurityUtils;
import org.cedacri.pingpong.enums.RoleEnum;

import java.util.List;

@Slf4j
public class ViewUtils
{

    public static final String BUTTON = "button";
    public static final String COLORED_BUTTON = "colored-button";
    public static final String COMPACT_BUTTON = "compact-button";

    private ViewUtils()
    {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static Button createButton(String text, String className, Runnable clickListener)
    {

        log.debug("Creating button with text: '{}' and className: '{}'", text, className);

        Button button = new Button(text);
        button.addClassName(className);
        button.addClickListener(e ->
        {
            log.info("Button with text: '{}' clicked", text);
            clickListener.run();
        });
        log.debug("Button created: {}", button);
        return button;
    }

    public static Button createSecuredButton(String text,
                                             String className,
                                             Runnable clickListener,
                                             RoleEnum... allowedRoles)
    {
        log.debug("Creating secured button with text: '{}' and className: '{}'", text, className);

        Button button = new Button(text);
        button.addClassName(className);
        button.setVisible(SecurityUtils.hasAnyRole(allowedRoles));
        button.addClickListener(e ->
        {
            log.info("Secured button with text: '{}' clicked", text);
            clickListener.run();
        });
        log.debug("Secured button created: {}", button);
        return button;
    }

    public static <T extends Component> void highlightSelectedComponentFromComponentsList(List<T> components, int selectedIndex, String selectedClass)
    {
        if (selectedIndex < 0 || selectedIndex >= components.size())
        {
            return;
        }

        for (int i = 0; i < components.size(); i++)
        {
            if (i == selectedIndex)
            {
                components.get(i).addClassName(selectedClass);
            }
            else
            {
                components.get(i).removeClassName(selectedClass);
            }
        }
    }


    public static TextField createTextField(String label)
    {
        log.debug("Creating TextField with label: '{}'", label);

        TextField textField = new TextField(label);
        textField.setWidth("250px");
        log.debug("TextField created: {}", textField);
        return textField;
    }

    public static ComboBox<String> createComboBox(String label, List<String> items)
    {
        log.debug("Creating ComboBox with label: '{}' and items: '{}'", label, items);

        ComboBox<String> comboBox = new ComboBox<>(label);
        comboBox.setItems(items);
        comboBox.setWidth("250px");
        log.debug("ComboBox created: {}", comboBox);
        return comboBox;
    }

    public static Checkbox createCheckBox(String label)
    {
        log.debug("Creating Checkbox with label: '{}' ", label);

        Checkbox checkbox = new Checkbox(label);
        checkbox.setWidth("250px");
        log.debug("Checkbox created: {}", checkbox);
        return checkbox;
    }

    public static DatePicker createDatePicker(String label)
    {
        log.debug("Creating DatePicker with label: '{}'", label);

        DatePicker datePicker = new DatePicker(label);
        datePicker.setWidth("250px");
        log.debug("DatePicker created: {}", datePicker);
        return datePicker;
    }

    public static IntegerField createIntegerField(String label)
    {
        log.debug("Creating IntegerField with label: '{}'", label);

        IntegerField integerField = new IntegerField(label);
        integerField.setWidth("250px");
        log.debug("IntegerField created: {}", integerField);
        return integerField;
    }

    public static HorizontalLayout createHorizontalLayout(FlexComponent.JustifyContentMode justifyContentMode, com.vaadin.flow.component.Component... components)
    {
        log.debug("Creating HorizontalLayout with justifyContentMode: '{}' and components: {}", justifyContentMode, components);
        HorizontalLayout layout = new HorizontalLayout(components);
        layout.setSpacing(true);
        layout.setWidthFull();
        layout.setJustifyContentMode(justifyContentMode);
        layout.setAlignItems(FlexComponent.Alignment.BASELINE);
        log.debug("HorizontalLayout created: {}", layout);
        return layout;
    }

    public static VerticalLayout createVerticalLayout(FlexComponent.JustifyContentMode justifyContentMode, com.vaadin.flow.component.Component... components)
    {
        log.debug("Creating HorizontalLayout with justifyContentMode: '{}' and components: {}", justifyContentMode, components);
        VerticalLayout layout = new VerticalLayout(components);
        layout.setSpacing(true);
        layout.setWidthFull();
        layout.setJustifyContentMode(justifyContentMode);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        log.debug("HorizontalLayout created: {}", layout);
        return layout;
    }

    public static TextField createScoreField(Integer value)
    {
        TextField score = new TextField();

        score.setMaxLength(2);
        score.setAllowedCharPattern("^[0-9]+$");

        score.setWidth("50px");

        if (value != null)
            score.setValue(value.toString());
        else score.setPlaceholder("-");

        return score;
    }


    public static Span createPlayerLabel(String content)
    {
        Span span = new Span(content);

        span.getStyle().setFontSize("18px");
        span.getStyle().setTop("15px");
        span.setMaxWidth("300px");
        span.setWidth("250px");

        return span;
    }
}
