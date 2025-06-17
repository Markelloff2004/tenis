package org.cedacri.pingpong.views.tournaments.v1.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.BaseTournament;
import org.cedacri.pingpong.entity.TournamentOlympic;
import org.cedacri.pingpong.entity.TournamentRoundRobin;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.*;

import java.util.HashSet;

@Slf4j
public class TournamentAddDialog extends AbstractTournamentDialog {


    private final TournamentService tournamentService;
    private final Runnable onSaveCallback;

    private final Checkbox startNowCheckbox = ViewUtils.createCheckBox("Start Now");

    public TournamentAddDialog(TournamentService tournamentService, PlayerService playerService, Runnable onSaveCallback) {
        super("Edit Tournament");

        this.tournamentService = tournamentService;
        this.onSaveCallback = onSaveCallback;
        this.selectedPlayersSet = new HashSet<>();
        this.availablePlayersSet = new HashSet<>();

        initializePlayerSets(playerService);
        initializeFields();
        configureComboBoxes();
        initializeGrids(true);
        add(createDialogLayout(), createPlayersLayout(), createDialogButtons());

        log.debug("Initializing fields for editing...");
        refreshGrids();
    }

    @Override
    protected HorizontalLayout createDialogButtons() {
        Button saveButton = ViewUtils.createButton("Save", ViewUtils.COLORED_BUTTON, this::onSave);
        Button cancelButton = ViewUtils.createButton("Cancel", ViewUtils.BUTTON, this::onCancel);

        return ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.CENTER, startNowCheckbox, saveButton, cancelButton);
    }

    @Override
    protected void onSave() {
        boolean startNow = false;

        log.info("Saving tournament with name: {}", tournamentNameField.getValue());

        BaseTournament baseTournament = null;

        if (typeComboBox.equals(TournamentTypeEnum.OLYMPIC)) {
            log.info("Tournament type is OLYMPIC, creating TournamentOlympic instance");
            baseTournament = new TournamentOlympic();
        } else if (typeComboBox.equals(TournamentTypeEnum.ROBIN_ROUND)) {
            log.info("Tournament type is not OLYMPIC, creating BaseTournament instance");
            baseTournament = new TournamentRoundRobin();
        } else {
            NotificationManager.showErrorNotification("Error durring create a object of class BaseTournament");
        }

        try {
            if (tournamentNameField.getValue() == null || tournamentNameField.getValue().trim().isEmpty()) {
                NotificationManager.showErrorNotification("Tournament name is required");
                return;
            }

            if (selectedPlayersSet == null || selectedPlayersSet.isEmpty()) {
                NotificationManager.showErrorNotification("At least one player must be selected");
                return;
            }

            startNow = Boolean.TRUE.equals(startNowCheckbox.getValue());

            // Set tournament properties with null checks
            baseTournament.setTournamentName(tournamentNameField.getValue().trim());
            baseTournament.setTournamentType(typeComboBox.getValue());
            baseTournament.setTournamentStatus(TournamentStatusEnum.PENDING);
            baseTournament.setSetsToWin(setsCountComboBox.getValue());
//        baseTournament.setSemifinalsSetsToWin(semifinalsSetsCountComboBox.getValue());
//        baseTournament.setFinalsSetsToWin(finalsSetsCountComboBox.getValue());
            baseTournament.setPlayers(selectedPlayersSet);
            baseTournament.setMaxPlayers(TournamentUtils.calculateMaxPlayers(baseTournament));
        } catch (Exception e) {
            log.error("Error preparing tournament data: {}", e.getMessage(), e);
            NotificationManager.showErrorNotification(Constants.TOURNAMENT_SAVE_ERROR + ExceptionUtils.getExceptionMessage(e));
            return;
        }

        // Save tournament
        try {
            baseTournament = tournamentService.saveTournament(baseTournament);
            log.info("Tournament saved successfully: {}", baseTournament.getId());
            NotificationManager.showInfoNotification(Constants.TOURNAMENT_SAVE_SUCCESS_MESSAGE);
        } catch (IllegalArgumentException e) {
            NotificationManager.showErrorNotification(Constants.TOURNAMENT_SAVE_ERROR + e.getMessage());
            return;
        }

        if (startNow) {
            tournamentService.startTournament(baseTournament);
            UI.getCurrent().navigate("tournament/matches/" + baseTournament.getId());
            NotificationManager.showInfoNotification(Constants.TOURNAMENT_START_SUCCESS_MESSAGE);
        }

        // Only execute callback and close if we reach this point (success)
        onSaveCallback.run();
        close();
    }
}