package org.cedacri.pingpong.views.tournaments.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.enums.SetTypes;
import org.cedacri.pingpong.enums.TournamentType;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.Constraints;
import org.cedacri.pingpong.utils.NotificationManager;
import org.cedacri.pingpong.utils.ViewUtils;
import org.cedacri.pingpong.views.util.GridUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TournamentEditDialog extends Dialog {

    private static final Logger logger = LoggerFactory.getLogger(TournamentEditDialog.class);

    public TournamentEditDialog(TournamentService tournamentService, PlayerService playerService, Tournament tournament, Runnable onSaveCallback) {
        logger.info("Initializing TournamentEditDialog for tournament: {} with Id: {}", tournament.getTournamentName(), tournament.getId());

        setHeaderTitle("Edit Tournament");
        setWidth("50%");


        logger.debug("Initializing fields for editing...");

        TextField tournamentNameField = new TextField("Tournament Name");
        tournamentNameField.setWidth("50%");
        tournamentNameField.setRequired(true);
        tournamentNameField.setValue(tournament.getTournamentName());

        ComboBox<String> typeComboBox = new ComboBox<>("Type");
        typeComboBox.setItems(Arrays.stream(TournamentType.values())
                .map(Enum::toString)
                .collect(Collectors.toSet()));
        typeComboBox.setWidth("20%");
        typeComboBox.setRequired(true);
        typeComboBox.setValue(tournament.getTournamentType().toString());

        ComboBox<String> statusComboBox = new ComboBox<>("Status");
        statusComboBox.setItems(Constraints.TOURNAMENT_STATUSES);
        statusComboBox.setWidth("20%");
        statusComboBox.setRequired(true);
        statusComboBox.setValue(tournament.getTournamentStatus());

        ComboBox<String> setsCountComboBox = new ComboBox<>("Sets Count");
        setsCountComboBox.setItems(Arrays.stream(SetTypes.values())
                .map(Enum::toString)
                .collect(Collectors.toSet()));
        setsCountComboBox.setWidth("30%");
        setsCountComboBox.setRequired(true);
        setsCountComboBox.setValue(tournament.getSetsToWin().toString());

        ComboBox<String> semifinalsSetsCountComboBox= new ComboBox<>("Semifinals Sets Count");
        semifinalsSetsCountComboBox.setItems(Arrays.stream(SetTypes.values())
                .map(Enum::toString)
                .collect(Collectors.toSet()));
        semifinalsSetsCountComboBox.setWidth("30%");
        semifinalsSetsCountComboBox.setRequired(true);
        semifinalsSetsCountComboBox.setValue(tournament.getSetsToWin().toString());

        ComboBox<String> finalsSetsCountComboBox = new ComboBox<>("Finals Sets Count");
        finalsSetsCountComboBox.setItems(Arrays.stream(SetTypes.values())
                .map(Enum::toString)
                .collect(Collectors.toSet()));
        finalsSetsCountComboBox.setWidth("30%");
        finalsSetsCountComboBox.setRequired(true);
        finalsSetsCountComboBox.setValue(tournament.getSetsToWin().toString());


        Set<Player> selectedPlayersSet = new HashSet<>(tournament.getPlayers());

        Set<Player> availablePlayersSet = playerService.getAll()
                .filter(p -> !selectedPlayersSet.contains(p))
                .collect(Collectors.toSet());

        logger.debug("Edit tournament dialog initialized for tournament: {}", tournament.getId());

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
                ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.BETWEEN, tournamentNameField, typeComboBox, statusComboBox),
                ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.BETWEEN, setsCountComboBox, semifinalsSetsCountComboBox, finalsSetsCountComboBox)
        );
        dialogLayout.setSpacing(true);


        if(tournament.getTournamentStatus().equals(Constraints.STATUS_PENDING)){
            Grid<String> playersGrid = new Grid<>(String.class, false);
            playersGrid.addColumn(player -> player).setHeader("Player name");

            Dialog addPlayerDialog = new Dialog();
            addPlayerDialog.setHeaderTitle("Add Player");
        }

        Button saveButton = ViewUtils.createButton("Save", "button", () -> {
            logger.info("Save button clicked. Attempting to update tournament {}", tournament.getTournamentName());

            try{
                tournament.setTournamentName(tournamentNameField.getValue());
                tournament.setTournamentType(TournamentType.valueOf(typeComboBox.getValue().toUpperCase()));
                tournament.setTournamentStatus(statusComboBox.getValue());
                tournament.setPlayers(selectedPlayersSet);
                tournament.setSetsToWin(SetTypes.valueOf(setsCountComboBox.getValue().toUpperCase()));
                tournament.setSemifinalsSetsToWin(SetTypes.valueOf(semifinalsSetsCountComboBox.getValue().toUpperCase()));
                tournament.setFinalsSetsToWin(SetTypes.valueOf(finalsSetsCountComboBox.getValue().toUpperCase()));

                tournamentService.saveTournament(tournament);
                logger.info("Tournament updated successfully: {}", tournament.getId());

                onSaveCallback.run();

                close();

                NotificationManager.showInfoNotification(Constraints.TOURNAMENT_UPDATE_SUCCESS);
            }
            catch (Exception e){
                logger.error("Error updating tournament: {} {}", tournament.getId(), e.getMessage(), e);
                NotificationManager.showInfoNotification(Constraints.TOURNAMENT_UPDATE_ERROR + "\n" + e.getMessage());
            }
        });

        Button cancelButton = ViewUtils.createButton("Cancel", "button", () -> {
            logger.info("Cancel button clicked. Closing TournamentEditDialog.");
            close();
        } );

        HorizontalLayout dialogButtons = ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.END, saveButton, cancelButton);

        add(dialogLayout, playersLayout, dialogButtons);
    }
}
