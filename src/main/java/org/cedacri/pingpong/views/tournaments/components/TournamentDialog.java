package org.cedacri.pingpong.views.tournaments.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.enums.SetTypes;
import org.cedacri.pingpong.enums.TournamentType;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.ViewUtils;
import org.cedacri.pingpong.views.util.GridUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class TournamentDialog extends Dialog {

    protected static final Logger logger = LoggerFactory.getLogger(TournamentDialog.class);

    protected TextField tournamentNameField;
    protected ComboBox<String> typeComboBox;
    protected ComboBox<String> setsCountComboBox;
    protected ComboBox<String> semifinalsSetsCountComboBox;
    protected ComboBox<String> finalsSetsCountComboBox;
    protected Set<Player> selectedPlayersSet;
    protected Set<Player> availablePlayersSet;
    protected Grid<Player> selectedPlayersGrid;
    protected Grid<Player> availablePlayersGrid;

    protected TournamentDialog(String headerTitle, TournamentService tournamentService, PlayerService playerService) {
        logger.info("Initializing {}", headerTitle);

        setHeaderTitle(headerTitle);
        setWidth("80%");

        initializeFields();
        configureComboBoxes();
        initializeGrids(playerService);

        VerticalLayout dialogLayout = createDialogLayout();
        HorizontalLayout playersLayout = createPlayersLayout();
        HorizontalLayout dialogButtons = createDialogButtons();

        add(dialogLayout, playersLayout, dialogButtons);
    }

    private void initializeFields() {
        tournamentNameField = new TextField("Tournament Name");
        tournamentNameField.setWidth("60%");
        tournamentNameField.setRequired(true);

        typeComboBox = new ComboBox<>("Type");
        typeComboBox.setWidth("35%");
        typeComboBox.setRequired(true);

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

    private void configureComboBoxes() {
        typeComboBox.setItems(Arrays.stream(TournamentType.values())
                .map(Enum::toString)
                .collect(Collectors.toSet()));

        setsCountComboBox.setItems(Arrays.stream(SetTypes.values())
                .map(Enum::toString)
                .collect(Collectors.toSet()));

        semifinalsSetsCountComboBox.setItems(Arrays.stream(SetTypes.values())
                .map(Enum::toString)
                .collect(Collectors.toSet()));

        finalsSetsCountComboBox.setItems(Arrays.stream(SetTypes.values())
                .map(Enum::toString)
                .collect(Collectors.toSet()));
    }

    private void initializeGrids(PlayerService playerService) {
        selectedPlayersGrid = new Grid<>(Player.class, false);
        availablePlayersGrid = new Grid<>(Player.class, false);

        selectedPlayersSet = new HashSet<>();
        availablePlayersSet = playerService.getAll().collect(Collectors.toSet());

        availablePlayersGrid.setItems(availablePlayersSet);
        selectedPlayersGrid.setItems(selectedPlayersSet);

        Runnable refreshGrids = () -> {
            selectedPlayersGrid.setItems(selectedPlayersSet);
            availablePlayersGrid.setItems(availablePlayersSet);
        };

        GridUtils.configurePlayerGrid(selectedPlayersGrid, selectedPlayersSet, availablePlayersSet, "Remove", refreshGrids);
        GridUtils.configurePlayerGrid(availablePlayersGrid, availablePlayersSet, selectedPlayersSet, "Add", refreshGrids);
    }

    private VerticalLayout createDialogLayout() {
        return new VerticalLayout(
                ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.BETWEEN, tournamentNameField, typeComboBox),
                ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.BETWEEN, setsCountComboBox, semifinalsSetsCountComboBox, finalsSetsCountComboBox)
        );
    }

    private HorizontalLayout createPlayersLayout() {
        return ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.BETWEEN,
                selectedPlayersGrid, availablePlayersGrid);
    }

    protected HorizontalLayout createDialogButtons() {
        Button saveButton = ViewUtils.createButton("Save", "button", this::onSave);
        Button cancelButton = ViewUtils.createButton("Cancel", "button", this::onCancel);

        return ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.END, saveButton, cancelButton);
    }

    protected abstract void onSave();

    protected void onCancel() {
        logger.info("Cancel button clicked. Closing dialog.");
        close();
    }
}
