package org.cedacri.pingpong.views.tournaments.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.TournamentOlympic;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.exception.tournament.NotEnoughPlayersException;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.Constants;
import org.cedacri.pingpong.utils.ExceptionUtils;
import org.cedacri.pingpong.utils.NotificationManager;
import org.cedacri.pingpong.utils.ViewUtils;

import java.util.HashSet;

@Slf4j
public class TournamentAddDialog extends AbstractTournamentDialog
{


    private final TournamentService tournamentService;
    private final Runnable onSaveCallback;

    private final Checkbox startNowCheckbox = ViewUtils.createCheckBox("Start Now");

    public TournamentAddDialog(TournamentService tournamentService, PlayerService playerService, Runnable onSaveCallback)
    {
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
    protected HorizontalLayout createDialogButtons()
    {
        Button saveButton = ViewUtils.createButton("Save", ViewUtils.COLORED_BUTTON, this::onSave);
        Button cancelButton = ViewUtils.createButton("Cancel", ViewUtils.BUTTON, this::onCancel);

        return ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.CENTER, startNowCheckbox, saveButton, cancelButton);
    }

    @Override
    protected void onSave()
    {

        TournamentOlympic tournamentOlympic = new TournamentOlympic();
        boolean startNow;
        try
        {
            startNow = startNowCheckbox.getValue();

            tournamentOlympic.setTournamentName(tournamentNameField.getValue());
            tournamentOlympic.setTournamentType(typeComboBox.getValue());
            tournamentOlympic.setTournamentStatus(TournamentStatusEnum.PENDING);
            tournamentOlympic.setSetsToWin(setsCountComboBox.getValue());
            tournamentOlympic.setSemifinalsSetsToWin(semifinalsSetsCountComboBox.getValue());
            tournamentOlympic.setFinalsSetsToWin(finalsSetsCountComboBox.getValue());
            tournamentOlympic.setPlayers(selectedPlayersSet);
            tournamentOlympic.setMaxPlayers(TournamentUtils.calculateMaxPlayers(tournamentOlympic));

        }
        catch (Exception e)
        {
            log.error("Error fetching data from Vaadin Components for saving tournament: {}", e.getMessage(), e);
            NotificationManager.showErrorNotification(Constants.TOURNAMENT_SAVE_ERROR + ExceptionUtils.getExceptionMessage(e));

            return;
        }

        try
        {
            tournamentOlympic = tournamentService.saveTournament(tournamentOlympic);

            log.info("Tournament saved successfully: {}", tournamentOlympic.getId());
            NotificationManager.showInfoNotification(Constants.TOURNAMENT_SAVE_SUCCESS_MESSAGE);
        }
        catch (IllegalArgumentException illegalArgumentException)
        {
            NotificationManager.showErrorNotification(Constants.TOURNAMENT_SAVE_ERROR + illegalArgumentException.getMessage());

            return;
        }

        if (startNow)
        {
            try
            {
                tournamentService.startTournament(tournamentOlympic);

                UI.getCurrent().navigate("tournament/matches/" + tournamentOlympic.getId());

                NotificationManager.showInfoNotification(Constants.TOURNAMENT_START_SUCCESS_MESSAGE);
            }
            catch (NotEnoughPlayersException notEnoughPlayersException)
            {
                NotificationManager.showErrorNotification(Constants.TOURNAMENT_START_ERROR + " " + notEnoughPlayersException.getMessage());
            }
        }

        onSaveCallback.run();
        close();

    }
}
