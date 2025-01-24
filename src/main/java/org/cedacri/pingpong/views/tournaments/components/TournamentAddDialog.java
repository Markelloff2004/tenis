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
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.enums.SetTypesEnum;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.Constraints;
import org.cedacri.pingpong.utils.NotificationManager;
import org.cedacri.pingpong.utils.TournamentUtils;
import org.cedacri.pingpong.utils.ViewUtils;
import org.cedacri.pingpong.views.util.GridUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TournamentAddDialog extends Dialog {

    private static final Logger logger = LoggerFactory.getLogger(TournamentAddDialog.class);

    public TournamentAddDialog(TournamentService tournamentService, PlayerService playerService, Runnable onSaveCallback) {
        logger.info("Initializing TournamentAddDialog for new tournamen.");

        setHeaderTitle("Add Tournament");
        setWidth("80%");


        logger.debug("Initializing fields for editing...");

        TextField tournamentNameField = new TextField("Tournament Name");
        tournamentNameField.setWidth("60%");
        tournamentNameField.setRequired(true);

        ComboBox<String> typeComboBox = new ComboBox<>("Type");
        typeComboBox.setItems(Arrays.stream(TournamentTypeEnum.values())
                .map(Enum::toString)
                .collect(Collectors.toSet()));
        typeComboBox.setWidth("35%");
        typeComboBox.setRequired(true);


        ComboBox<String> setsCountComboBox = new ComboBox<>("Sets Count");
        setsCountComboBox.setItems(Arrays.stream(SetTypesEnum.values())
                .map(Enum::toString)
                .collect(Collectors.toSet()));
        setsCountComboBox.setWidth("30%");
        setsCountComboBox.setRequired(true);

        ComboBox<String> semifinalsSetsCountComboBox= new ComboBox<>("Semifinals Sets Count");
        semifinalsSetsCountComboBox.setItems(Arrays.stream(SetTypesEnum.values())
                .map(Enum::toString)
                .collect(Collectors.toSet()));
        semifinalsSetsCountComboBox.setWidth("30%");
        semifinalsSetsCountComboBox.setRequired(true);

        ComboBox<String> finalsSetsCountComboBox = new ComboBox<>("Finals Sets Count");
        finalsSetsCountComboBox.setItems(Arrays.stream(SetTypesEnum.values())
                .map(Enum::toString)
                .collect(Collectors.toSet()));
        finalsSetsCountComboBox.setWidth("30%");
        finalsSetsCountComboBox.setRequired(true);

        Set<Player> selectedPlayersSet = new HashSet<>();
        Set<Player> availablePlayersSet = playerService.getAll().collect(Collectors.toSet());

        logger.debug("AddTournamentDialog initialized for new tournament");

        Grid<Player> selectedPlayersGrid = new Grid<>(Player.class, false);
        Grid<Player> availablePlayersGrid = new Grid<>(Player.class, false);

        selectedPlayersGrid.setItems(selectedPlayersSet);
        availablePlayersGrid.setItems(availablePlayersSet);

        Runnable refreshGrids = () -> {
            selectedPlayersGrid.setItems(selectedPlayersSet);
            availablePlayersGrid.setItems(availablePlayersSet);
        };

        GridUtils.configurePlayerGrid(selectedPlayersGrid, selectedPlayersSet, availablePlayersSet, "Remove", refreshGrids);
        GridUtils.configurePlayerGrid(availablePlayersGrid, availablePlayersSet, selectedPlayersSet, "Add", refreshGrids);

        HorizontalLayout playersLayout = ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.BETWEEN,
                selectedPlayersGrid, availablePlayersGrid);

        VerticalLayout dialogLayout = new VerticalLayout(
                ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.BETWEEN, tournamentNameField, typeComboBox),
                ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.BETWEEN, setsCountComboBox, semifinalsSetsCountComboBox, finalsSetsCountComboBox)
        );
        dialogLayout.setSpacing(true);

        Button saveButton = ViewUtils.createButton("Save", "button", () -> {
            logger.info("Save button clicked. Attempting to add new tournament");

            try{
                Tournament tournament = new Tournament();

                tournament.setTournamentName(tournamentNameField.getValue());
                tournament.setTournamentType(TournamentTypeEnum.valueOf(typeComboBox.getValue().toUpperCase()));
                tournament.setTournamentStatus(TournamentStatusEnum.PENDING);
                tournament.setMaxPlayers(TournamentUtils.calculateMaxPlayers(selectedPlayersSet.size()));
                tournament.setPlayers(selectedPlayersSet);
                tournament.setSetsToWin(SetTypesEnum.valueOf(setsCountComboBox.getValue().toUpperCase()));
                tournament.setSemifinalsSetsToWin(SetTypesEnum.valueOf(semifinalsSetsCountComboBox.getValue().toUpperCase()));
                tournament.setFinalsSetsToWin(SetTypesEnum.valueOf(finalsSetsCountComboBox.getValue().toUpperCase()));

                tournamentService.saveTournament(tournament);
                logger.info("Tournament saved successfully: {}", tournament.getId());

                onSaveCallback.run();

                close();

                NotificationManager.showInfoNotification(Constraints.TOURNAMENT_UPDATE_SUCCESS);
            }
            catch (Exception e){
                logger.error("Error saving tournament: {}", e.getMessage(), e);
                NotificationManager.showInfoNotification(Constraints.TOURNAMENT_UPDATE_ERROR + "\n" + e.getMessage());
            }
        });

        Button cancelButton = ViewUtils.createButton("Cancel", "button", () -> {
            logger.info("Cancel button clicked. Closing TournamentAddDialog.");
            close();
        } );

        HorizontalLayout dialogButtons = ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.END, saveButton, cancelButton);

        add(dialogLayout, playersLayout, dialogButtons);
    }
}
