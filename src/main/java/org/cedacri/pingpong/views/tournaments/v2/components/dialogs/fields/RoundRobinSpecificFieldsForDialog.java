package org.cedacri.pingpong.views.tournaments.v2.components.dialogs.fields;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import jakarta.validation.constraints.Min;
import org.cedacri.pingpong.utils.ViewUtils;

public class RoundRobinSpecificFieldsForDialog extends HorizontalLayout {

    private TextField playersPerGroup;
    private TextField stagesNumber;
    private TextField finalGroupSize;

    public RoundRobinSpecificFieldsForDialog(Component currentComponent) {

        playersPerGroup = new TextField("Players Per Group");
        playersPerGroup.setWidth("30%");
        playersPerGroup.setPattern("\\d+");
        playersPerGroup.setRequired(true);

        stagesNumber = new TextField("Stages Number");
        stagesNumber.setWidth("30%");
        stagesNumber.setPattern("\\d+");
        stagesNumber.setRequired(true);

        finalGroupSize = new TextField("Final Group Size");
        finalGroupSize.setWidth("30%");
        finalGroupSize.setPattern("\\d+");
        finalGroupSize.setRequired(true);

        add(ViewUtils.createHorizontalLayout(JustifyContentMode.BETWEEN,
                currentComponent, playersPerGroup, stagesNumber, finalGroupSize));
    }

    public int getPlayersPerGroup() {
        int value = Integer.parseInt(playersPerGroup.getValue());
        if (value < 2) {
            throw new IllegalArgumentException("Players per group cannot be null");
        }
        return value;
    }

    public int getStagesNumber() {
        int value = Integer.parseInt(stagesNumber.getValue());
        if (value < 1) {
            throw new IllegalArgumentException("Stages number cannot be null");
        }
        return value;
    }

    public int getFinalGroupSize() {
        int value = Integer.parseInt(finalGroupSize.getValue());
        if (value < 2) {
            throw new IllegalArgumentException("Final group size cannot be null");
        }
        return value;
    }

    public void setPlayersPerGroup(@Min(value = 2, message = "Players per group must be at least 2") int playersPerGroup) {
        this.playersPerGroup.setValue(String.valueOf(playersPerGroup));
    }

    public void setStagesNumber(@Min(value = 1, message = "Stages number must be at least 1") int stagesNumber) {
        this.stagesNumber.setValue(String.valueOf(stagesNumber));
    }

    public void setFinalGroupSize(@Min(value = 2, message = "Final group size must be at least 2") int finalGroupSize) {
        this.finalGroupSize.setValue(String.valueOf(finalGroupSize));
    }

    public void setReadOnly(boolean b) {
        playersPerGroup.setReadOnly(b);
        stagesNumber.setReadOnly(b);
        finalGroupSize.setReadOnly(b);
    }

    public void setValues(@Min(value = 2, message = "Players per group must be at least 2") int playersPerGroup,
                          @Min(value = 1, message = "Stages number must be at least 1") int stagesNumber,
                          @Min(value = 2, message = "Final group size must be at least 2") int finalGroupSize) {
        this.playersPerGroup.setValue(String.valueOf(playersPerGroup));
        this.stagesNumber.setValue(String.valueOf(stagesNumber));
        this.finalGroupSize.setValue(String.valueOf(finalGroupSize));
    }
}
