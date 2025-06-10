package org.cedacri.pingpong.views.tournaments.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.enums.SetTypesEnum;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.utils.GridUtils;
import org.cedacri.pingpong.utils.ViewUtils;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractTournamentDialog extends Dialog
{

    protected TextField tournamentNameField;
    protected ComboBox<TournamentTypeEnum> typeComboBox;
    protected ComboBox<SetTypesEnum> setsCountComboBox;
    protected ComboBox<SetTypesEnum> semifinalsSetsCountComboBox;
    protected ComboBox<SetTypesEnum> finalsSetsCountComboBox;
    protected Set<Player> selectedPlayersSet;
    protected Set<Player> availablePlayersSet;
    protected Grid<Player> selectedPlayersGrid;
    protected Grid<Player> availablePlayersGrid;

    private Registration setsCountListenerRegistration;

    protected AbstractTournamentDialog(String headerTitle)
    {
        log.info("Initializing {}", headerTitle);

        setHeaderTitle(headerTitle);
        setWidth("80%");

    }

    protected void initializeFields()
    {
        tournamentNameField = new TextField("Tournament Name");
        tournamentNameField.setWidth("60%");
        tournamentNameField.setRequired(true);

        typeComboBox = new ComboBox<>("Type");
        typeComboBox.setWidth("35%");
        typeComboBox.setRequired(true);
        typeComboBox.addValueChangeListener(value -> manageSetFields(value.getValue()));

        setsCountComboBox = new ComboBox<>("Sets Count");
        setsCountComboBox.setWidth("30%");
        setsCountComboBox.setRequired(true);

        semifinalsSetsCountComboBox = new ComboBox<>("Semifinals Sets Count");
        semifinalsSetsCountComboBox.setWidth("30%");
        semifinalsSetsCountComboBox.setRequired(true);

        finalsSetsCountComboBox = new ComboBox<>("Finals Sets Count");
        finalsSetsCountComboBox.setWidth("30%");
        finalsSetsCountComboBox.setRequired(true);

    }

    protected void configureComboBoxes()
    {

        typeComboBox.setItems(TournamentTypeEnum.values());

        setsCountComboBox.setItems(SetTypesEnum.values());
        semifinalsSetsCountComboBox.setItems(SetTypesEnum.values());
        finalsSetsCountComboBox.setItems(SetTypesEnum.values());

    }

    protected void initializePlayerSets(PlayerService playerService)
    {
        availablePlayersSet.addAll(
                playerService.findAllPlayers()
                        .stream()
                        .filter(p -> !selectedPlayersSet.contains(p))
                        .collect(Collectors.toSet())
        );
    }

    protected void initializeGrids(boolean withButtonActions)
    {
        selectedPlayersGrid = new Grid<>(Player.class, false);
        availablePlayersGrid = new Grid<>(Player.class, false);

        availablePlayersGrid.setItems(availablePlayersSet);
        selectedPlayersGrid.setItems(selectedPlayersSet);

        if (withButtonActions)
        {
            GridUtils.configurePlayerGridWithActionButtons(selectedPlayersGrid, selectedPlayersSet, availablePlayersSet, "Remove", this::refreshGrids);
            GridUtils.configurePlayerGridWithActionButtons(availablePlayersGrid, availablePlayersSet, selectedPlayersSet, "Add", this::refreshGrids);
        }
        else
        {
            GridUtils.configurePlayerGrid(selectedPlayersGrid, selectedPlayersSet);
            GridUtils.configurePlayerGrid(availablePlayersGrid, availablePlayersSet);
        }
    }

    protected void refreshGrids()
    {
        selectedPlayersGrid.setItems(selectedPlayersSet);
        availablePlayersGrid.setItems(availablePlayersSet);
    }

    protected VerticalLayout createDialogLayout()
    {
        return new VerticalLayout(
                ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.BETWEEN, tournamentNameField, typeComboBox),
                ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.BETWEEN, setsCountComboBox, semifinalsSetsCountComboBox, finalsSetsCountComboBox)
        );
    }

    protected HorizontalLayout createPlayersLayout()
    {
        return ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.BETWEEN,
                ViewUtils.createVerticalLayout(FlexComponent.JustifyContentMode.CENTER, new Span("Selected Players"), selectedPlayersGrid),
                ViewUtils.createVerticalLayout(FlexComponent.JustifyContentMode.CENTER, new Span("Available Players"), availablePlayersGrid)
        );
    }

    protected HorizontalLayout createDialogButtons()
    {
        Button saveButton = ViewUtils.createButton("Save", ViewUtils.COLORED_BUTTON, this::onSave);
        Button cancelButton = ViewUtils.createButton("Cancel", ViewUtils.BUTTON, this::onCancel);

        return ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.CENTER, saveButton, cancelButton);
    }

    protected abstract void onSave();

    protected void onCancel()
    {
        log.info("Cancel button clicked. Closing dialog.");
        close();
    }

    private void manageSetFields(TournamentTypeEnum tournamentType)
    {
        if (setsCountListenerRegistration != null)
        {
            setsCountListenerRegistration.remove();
            setsCountListenerRegistration = null;
        }

        if (tournamentType == TournamentTypeEnum.ROBIN_ROUND)
        {
            semifinalsSetsCountComboBox.setReadOnly(true);
            semifinalsSetsCountComboBox.setValue(setsCountComboBox.getValue());

            finalsSetsCountComboBox.setReadOnly(true);
            finalsSetsCountComboBox.setValue(setsCountComboBox.getValue());

            setsCountListenerRegistration = setsCountComboBox.addValueChangeListener(event ->
            {
                semifinalsSetsCountComboBox.setValue(event.getValue());
                finalsSetsCountComboBox.setValue(event.getValue());
            });

        }
        else
        {
            semifinalsSetsCountComboBox.setReadOnly(false);
            finalsSetsCountComboBox.setReadOnly(false);
        }
    }
}
