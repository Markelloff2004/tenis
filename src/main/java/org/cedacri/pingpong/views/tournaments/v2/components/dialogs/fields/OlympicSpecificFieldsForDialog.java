package org.cedacri.pingpong.views.tournaments.v2.components.dialogs.fields;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import jakarta.validation.constraints.NotNull;
import org.cedacri.pingpong.model.enums.SetsTypesEnum;
import org.cedacri.pingpong.utils.ViewUtils;

import java.util.HashMap;
import java.util.Map;

public class OlympicSpecificFieldsForDialog extends HorizontalLayout {

    private Map<String, Component> components = new HashMap<>();

    private ComboBox<SetsTypesEnum> semifinalsSetsCountComboBox;
    private ComboBox<SetsTypesEnum> finalsSetsCountComboBox;

    public OlympicSpecificFieldsForDialog(Component setsCountComboBox) {

        semifinalsSetsCountComboBox = new ComboBox<>("Semifinals Sets Count");
        semifinalsSetsCountComboBox.setWidth("30%");
        semifinalsSetsCountComboBox.setRequired(true);
        semifinalsSetsCountComboBox.setItems(SetsTypesEnum.values());

        finalsSetsCountComboBox = new ComboBox<>("Finals Sets Count");
        finalsSetsCountComboBox.setWidth("30%");
        finalsSetsCountComboBox.setRequired(true);
        finalsSetsCountComboBox.setItems(SetsTypesEnum.values());

        add( ViewUtils.createHorizontalLayout(JustifyContentMode.BETWEEN,
                setsCountComboBox, semifinalsSetsCountComboBox, finalsSetsCountComboBox));
    }

    public @NotNull(message = "Tournament semifinals sets cannot be null") SetsTypesEnum getSemifinalsSetsCount() {
        SetsTypesEnum value = semifinalsSetsCountComboBox.getValue();
        if (value == null) {
            throw new IllegalArgumentException("Tournament semifinals sets cannot be null");
        }
        return value;
    }

    public @NotNull(message = "Tournament final sets cannot be null") SetsTypesEnum getFinalsSetsCount() {
        SetsTypesEnum value = finalsSetsCountComboBox.getValue();
        if (value == null) {
            throw new IllegalArgumentException("Tournament final sets cannot be null");
        }
        return value;
    }

    public void setSemifinalsSetsCount(@NotNull(message = "Tournament semifinals sets cannot be null") SetsTypesEnum semifinalsSetsToWin) {
        semifinalsSetsCountComboBox.setValue(semifinalsSetsToWin);
    }

    public void setFinalsSetsCount(@NotNull(message = "Tournament final sets cannot be null") SetsTypesEnum finalsSetsToWin) {
        finalsSetsCountComboBox.setValue(finalsSetsToWin);
    }

    public void setReadOnly(boolean b) {
        semifinalsSetsCountComboBox.setReadOnly(b);
        finalsSetsCountComboBox.setReadOnly(b);
    }

    public void setValues(@NotNull(message = "Tournament semifinals sets cannot be null") SetsTypesEnum semifinalsSetsToWin,
                          @NotNull(message = "Tournament final sets cannot be null") SetsTypesEnum finalsSetsToWin) {
            this.semifinalsSetsCountComboBox.setValue(semifinalsSetsToWin);
            this.finalsSetsCountComboBox.setValue(finalsSetsToWin);
    }
}
