package org.cedacri.pingpong.views.tournaments.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.extern.slf4j.Slf4j;
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

import java.util.HashSet;

@Slf4j
public class TournamentAddDialog extends AbstractTournamentDialog {


    private final TournamentService tournamentService;
    private final PlayerService playerService;
    private final Runnable onSaveCallback;

    private Checkbox startNowCheckbox = ViewUtils.createCheckBox("Start Now");

    public TournamentAddDialog(TournamentService tournamentService, PlayerService playerService, Runnable onSaveCallback) {
        super("Edit Tournament");

        this.tournamentService = tournamentService;
        this.playerService = playerService;
        this.onSaveCallback = onSaveCallback;


        this.selectedPlayersSet = new HashSet<>();
        this.availablePlayersSet = new HashSet<>();
        initializePlayerSets(playerService, new HashSet<>());
        initializeFields();

        configureComboBoxes();

        initializeGrids(true);

        add(createDialogLayout(), createPlayersLayout(), createDialogButtons());

        logger.debug("Initializing fields for editing...");

        refreshGrids();
    }

    @Override
    protected HorizontalLayout createDialogButtons() {
        Button saveButton = ViewUtils.createButton("Save", "colored-button", this::onSave);
        Button cancelButton = ViewUtils.createButton("Cancel", "button", this::onCancel);

        return ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.CENTER, startNowCheckbox, saveButton, cancelButton);
    }


   @Override
protected void onSave() {
    logger.info("Save button clicked. Attempting to add new tournament");

    if (selectedPlayersSet.size() < 8) {
        NotificationManager.showInfoNotification("Please enter more than 8 players!");
    } else {
        try {
            Tournament tournament = new Tournament();

            tournament.setTournamentName(tournamentNameField.getValue());
            tournament.setTournamentType(TournamentTypeEnum.valueOf(typeComboBox.getValue().toUpperCase()));
            tournament.setTournamentStatus(TournamentStatusEnum.PENDING);
            tournament.setMaxPlayers(TournamentUtils.calculateMaxPlayers(selectedPlayersSet.size()));
            tournament.setSetsToWin(SetTypesEnum.valueOf(setsCountComboBox.getValue().toUpperCase()));
            tournament.setSemifinalsSetsToWin(SetTypesEnum.valueOf(semifinalsSetsCountComboBox.getValue().toUpperCase()));
            tournament.setFinalsSetsToWin(SetTypesEnum.valueOf(finalsSetsCountComboBox.getValue().toUpperCase()));

            tournament = tournamentService.saveTournament(tournament);

            for (Player player : selectedPlayersSet) {
                Player p = playerService.findById(player.getId());
                p.getTournaments().add(tournament);
                tournament.getPlayers().add(p);
            }

            tournament = tournamentService.saveTournament(tournament);

            if (startNowCheckbox.getValue()) {
                tournamentService.startTournament(tournament);
            }

            logger.info("Tournament saved successfully: {}", tournament.getId());

            onSaveCallback.run();
            close();

            NotificationManager.showInfoNotification(Constraints.TOURNAMENT_UPDATE_SUCCESS);
        } catch (Exception e) {
            logger.error("Error saving tournament: {}", e.getMessage(), e);
            NotificationManager.showInfoNotification(Constraints.TOURNAMENT_UPDATE_ERROR + "\n" + e.getMessage());
        }
    }
}
}
