package org.cedacri.pingpong.views.tournaments.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.Constraints;
import org.cedacri.pingpong.utils.ExceptionUtils;
import org.cedacri.pingpong.utils.NotificationManager;
import org.cedacri.pingpong.utils.TournamentUtils;
import org.cedacri.pingpong.utils.ViewUtils;

import java.util.HashSet;

@Slf4j
public class TournamentAddDialog extends AbstractTournamentDialog {


    private final TournamentService tournamentService;
    private final Runnable onSaveCallback;

    private Tournament tournament = new Tournament();

    private final Checkbox startNowCheckbox = ViewUtils.createCheckBox("Start Now");

    public TournamentAddDialog(TournamentService tournamentService, PlayerService playerService, Runnable onSaveCallback) {
        super("Edit Tournament");

        this.tournamentService = tournamentService;
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
    protected void onSave()
    {
        try
        {
            tournament.setTournamentName(tournamentNameField.getValue());
            tournament.setTournamentType(typeComboBox.getValue());
            tournament.setSetsToWin(setsCountComboBox.getValue());
            tournament.setSemifinalsSetsToWin(semifinalsSetsCountComboBox.getValue());
            tournament.setFinalsSetsToWin(finalsSetsCountComboBox.getValue());

            boolean startNow = startNowCheckbox.getValue();

            tournament = tournamentService.saveTournamentWithPlayers(tournament, selectedPlayersSet, startNow);

            if(startNow) {
                UI.getCurrent().navigate("tournament/matches/" + tournament.getId());
            }

            log.info("Tournament saved successfully: {}", tournament.getId());

            onSaveCallback.run();
            close();
            NotificationManager.showInfoNotification(Constraints.TOURNAMENT_UPDATE_SUCCESS);

        } catch (Exception e)
        {
            logger.error("Error saving tournament: {}", e.getMessage(), e);
            NotificationManager.showErrorNotification(Constraints.TOURNAMENT_UPDATE_ERROR + ExceptionUtils.getExceptionMessage(e));
        }
    }
}
