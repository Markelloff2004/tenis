package org.cedacri.pingpong.views.tournaments.v2.components.dialogs;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.model.enums.SetsTypesEnum;
import org.cedacri.pingpong.model.enums.TournamentStatusEnum;
import org.cedacri.pingpong.model.enums.TournamentTypeEnum;
import org.cedacri.pingpong.model.player.Player;
import org.cedacri.pingpong.model.tournament.BaseTournament;
import org.cedacri.pingpong.model.tournament.TournamentOlympic;
import org.cedacri.pingpong.model.tournament.TournamentRoundRobin;
import org.cedacri.pingpong.service.players.PlayerService;
import org.cedacri.pingpong.utils.GridUtils;
import org.cedacri.pingpong.utils.NotificationManager;
import org.cedacri.pingpong.utils.ViewUtils;
import org.cedacri.pingpong.views.tournaments.v2.components.dialogs.fields.OlympicSpecificFieldsForDialog;
import org.cedacri.pingpong.views.tournaments.v2.components.dialogs.fields.RoundRobinSpecificFieldsForDialog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractTournamentDialog extends Dialog {

    protected enum Mode { ADD, EDIT, VIEW }

    protected final PlayerService playerService;
    protected Mode currentMode = Mode.ADD;

    protected Set<Player> selectedPlayers = new HashSet<>();
    protected Set<Player> availablePlayers = new HashSet<>();

    protected Component tournamentSpecificFields;
    protected TextField tournamentNameField;
    protected ComboBox<TournamentTypeEnum> typeComboBox;
    protected ComboBox<SetsTypesEnum> setsCountComboBox;
    protected Grid<Player> selectedPlayersGrid;
    protected Grid<Player> availablePlayersGrid;
    protected Checkbox startNowCheckbox;

    protected BaseTournament tournament;

    protected AbstractTournamentDialog(String headerTitle, PlayerService playerService, boolean withActionButtons, BaseTournament tournament) {
        this.playerService = playerService;
        this.tournament = tournament;
        log.info("Initializing {}", headerTitle);

        initializeFields();
        configureComboBoxes();
        initializePlayerSets();
        initializeGrids(withActionButtons);

        setHeaderTitle(headerTitle);
        add(createDialogLayout(), createSpecificFieldsLayout(),
                createPlayersLayout(), createDialogButtons());
        setWidth("80%");
    }

    protected void initializeFields() {
        tournamentNameField = new TextField("Tournament Name");
        tournamentNameField.setWidth("60%");
        tournamentNameField.setRequired(true);

        typeComboBox = new ComboBox<>("Type");
        typeComboBox.setWidth("35%");
        typeComboBox.setRequired(true);
        typeComboBox.addValueChangeListener(e -> manageTournamentTypeChange(e.getValue()));

        setsCountComboBox = new ComboBox<>("Sets Count");
        setsCountComboBox.setWidth("30%");
        setsCountComboBox.setRequired(true);
        setsCountComboBox.setItems(SetsTypesEnum.values());
    }

    protected void configureComboBoxes() {
        typeComboBox.setItems(TournamentTypeEnum.values());
    }

    protected void initializePlayerSets() {
        if( tournament != null ){
            selectedPlayers.addAll(tournament.getPlayers());
        }

        availablePlayers.addAll(playerService.findAllPlayers());
        availablePlayers.removeAll(selectedPlayers);
    }

    protected void initializeGrids(boolean withActionButtons) {
        selectedPlayersGrid = GridUtils.createPlayerGrid(selectedPlayers, "Selected Players");
        availablePlayersGrid = GridUtils.createPlayerGrid(availablePlayers, "Available Players");

        if (withActionButtons) {
            GridUtils.configurePlayerGridWithActionButtons(selectedPlayersGrid, selectedPlayers, availablePlayers, "Remove", this::refreshGrids);
            GridUtils.configurePlayerGridWithActionButtons(availablePlayersGrid, availablePlayers, selectedPlayers, "Add", this::refreshGrids);
        } else {
            GridUtils.configurePlayerGrid(selectedPlayersGrid, selectedPlayers);
            GridUtils.configurePlayerGrid(availablePlayersGrid, availablePlayers);
        }
    }

    protected VerticalLayout createDialogLayout() {
        return new VerticalLayout(
                ViewUtils.createHorizontalLayout(
                        FlexComponent.JustifyContentMode.BETWEEN,
                        tournamentNameField,
                        typeComboBox
                )
        );
    }

    protected VerticalLayout createSpecificFieldsLayout() {
        return new VerticalLayout(
                ViewUtils.createHorizontalLayout(
                        FlexComponent.JustifyContentMode.BETWEEN,
                        setsCountComboBox
                )
        );
    }

    protected HorizontalLayout createPlayersLayout() {
        return ViewUtils.createHorizontalLayout(
                FlexComponent.JustifyContentMode.BETWEEN,
                createPlayerSection("Selected Players", selectedPlayersGrid),
                createPlayerSection("Available Players", availablePlayersGrid)
        );
    }

    protected VerticalLayout createPlayerSection(String title, Grid<Player> grid) {
        return ViewUtils.createVerticalLayout(
                FlexComponent.JustifyContentMode.CENTER,
                new Span(title),
                grid
        );
    }

    protected HorizontalLayout createDialogButtons() {
        startNowCheckbox = ViewUtils.createCheckBox("Start Now");
        Button saveButton = ViewUtils.createButton("Save", ViewUtils.COLORED_BUTTON, this::onSave);
        Button cancelButton = ViewUtils.createButton("Cancel", ViewUtils.BUTTON, this::closeDialog);
        return ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.CENTER, startNowCheckbox, saveButton, cancelButton);
    }

    protected void refreshGrids() {
        log.info("Refreshing player grids");
        selectedPlayersGrid.setItems(selectedPlayers);
        availablePlayersGrid.setItems(availablePlayers);
    }

    protected void closeDialog() {
        log.info("Closing tournament dialog");
        close();
    }

    protected void manageTournamentTypeChange(TournamentTypeEnum type) {
        removeExistingSpecificFields();

        switch (type) {
            case OLYMPIC -> tournamentSpecificFields = new OlympicSpecificFieldsForDialog(setsCountComboBox);
            case ROUND_ROBIN -> tournamentSpecificFields = new RoundRobinSpecificFieldsForDialog(setsCountComboBox);
            default -> {
                NotificationManager.showErrorNotification("Unsupported tournament type");
                return;
            }
        }
        addComponentAtIndex(1, tournamentSpecificFields);
    }

    private void removeExistingSpecificFields() {
        if (tournamentSpecificFields != null) {
            getElement().removeChild(tournamentSpecificFields.getElement());
        }
    }

    protected void configureReadOnlyState(boolean readOnly) {
        tournamentNameField.setReadOnly(readOnly);
        typeComboBox.setReadOnly(readOnly);
        setsCountComboBox.setReadOnly(readOnly);

        if (tournamentSpecificFields instanceof OlympicSpecificFieldsForDialog olympicFields) {
            olympicFields.setReadOnly(readOnly);
        }
        else if (tournamentSpecificFields instanceof RoundRobinSpecificFieldsForDialog roundRobinFields) {
            roundRobinFields.setReadOnly(readOnly);
        }
    }

    protected BaseTournament createTournamentFromFields() {
        TournamentTypeEnum type = typeComboBox.getValue();

        return switch (type) {
            case OLYMPIC -> createOlympicTournament();
            case ROUND_ROBIN -> createRoundRobinTournament();
            default -> null;
        };
    }

    private BaseTournament createOlympicTournament() {
        TournamentOlympic olympic = new TournamentOlympic();
        olympic.setTournamentName(tournamentNameField.getValue());
        olympic.setTournamentType(TournamentTypeEnum.OLYMPIC);
        olympic.setSetsToWin(setsCountComboBox.getValue());
        olympic.setTournamentStatus(TournamentStatusEnum.PENDING);
        olympic.setPlayers(selectedPlayers);

        if (tournamentSpecificFields instanceof OlympicSpecificFieldsForDialog olympicFields) {
            olympic.setSemifinalsSetsToWin(olympicFields.getSemifinalsSetsCount());
            olympic.setFinalsSetsToWin(olympicFields.getFinalsSetsCount());
        } else {
            NotificationManager.showErrorNotification("Olympic configuration missing");
            return null;
        }
        return olympic;
    }

    private BaseTournament createRoundRobinTournament() {
        TournamentRoundRobin robin = new TournamentRoundRobin();
        robin.setTournamentName(tournamentNameField.getValue());
        robin.setTournamentType(TournamentTypeEnum.ROUND_ROBIN);
        robin.setSetsToWin(setsCountComboBox.getValue());
        robin.setTournamentStatus(TournamentStatusEnum.PENDING);
        robin.setPlayers(selectedPlayers);

        if (tournamentSpecificFields instanceof RoundRobinSpecificFieldsForDialog roundRobinFields) {
            robin.setPlayersPerGroup(roundRobinFields.getPlayersPerGroup());
            robin.setStagesNumber(roundRobinFields.getStagesNumber());
            robin.setFinalGroupSize(roundRobinFields.getFinalGroupSize());
        } else {
            NotificationManager.showErrorNotification("Round Robin configuration missing");
            return null;
        }
        return robin;
    }

    protected void populateFieldsFromTournament(BaseTournament tournament) {
        if (tournament == null) {
            NotificationManager.showErrorNotification("Invalid tournament data");
            return;
        }

        tournamentNameField.setValue(tournament.getTournamentName());
        typeComboBox.setValue(tournament.getTournamentType());
        setsCountComboBox.setValue(tournament.getSetsToWin());

        removeExistingSpecificFields();

        if (tournament instanceof TournamentOlympic olympic) {
            tournamentSpecificFields = new OlympicSpecificFieldsForDialog(setsCountComboBox);
            ((OlympicSpecificFieldsForDialog) tournamentSpecificFields)
                    .setValues(olympic.getSemifinalsSetsToWin(), olympic.getFinalsSetsToWin());
        }
        else if (tournament instanceof TournamentRoundRobin robin) {
            tournamentSpecificFields = new RoundRobinSpecificFieldsForDialog(setsCountComboBox);
            ((RoundRobinSpecificFieldsForDialog) tournamentSpecificFields)
                    .setValues(robin.getPlayersPerGroup(), robin.getStagesNumber(), robin.getFinalGroupSize());
        }
        else {
            NotificationManager.showErrorNotification("Unsupported tournament type");
            return;
        }
        addComponentAtIndex(1, tournamentSpecificFields);
    }

    protected abstract void onSave();
}