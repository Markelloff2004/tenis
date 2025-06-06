package org.cedacri.pingpong.views.tournaments.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.exception.tournament.NotEnoughPlayersException;
import org.cedacri.pingpong.service.primary.PlayerService;
import org.cedacri.pingpong.service.primary.TournamentService;
import org.cedacri.pingpong.utils.Constants;
import org.cedacri.pingpong.utils.ExceptionUtils;
import org.cedacri.pingpong.utils.NotificationManager;
import org.cedacri.pingpong.utils.TournamentUtils;
import org.cedacri.pingpong.utils.ViewUtils;

import java.util.HashSet;

@Slf4j
public class TournamentEditDialog extends AbstractTournamentDialog
{

    private final TournamentService tournamentService;
    private final Runnable onSaveCallback;
    private Tournament tournament;

    private final ComboBox<String> statusComboBox;
    private final Checkbox startNowCheckbox = ViewUtils.createCheckBox("Start Now");

    public TournamentEditDialog(TournamentService tournamentService, PlayerService playerService, Tournament tournament, Runnable onSaveCallback)
    {
        super("Edit Tournament");

        this.tournamentService = tournamentService;
        this.tournament = tournament;
        this.onSaveCallback = onSaveCallback;
        this.selectedPlayersSet = tournament.getPlayers();
        this.availablePlayersSet = new HashSet<>();

        statusComboBox = createStatusComboBox();
        initializePlayerSets(playerService);
        initializeFields();
        configureComboBoxes();
        initializeGrids(true);
        add(createDialogLayoutWithStatus(), createPlayersLayout(), createDialogButtons());
        log.debug("Initializing fields for editing...");

        prefillFields(tournament);
        refreshGrids();
    }

    protected VerticalLayout createDialogLayoutWithStatus()
    {
        VerticalLayout dialogLayout = createDialogLayout();
        HorizontalLayout firstRow = (HorizontalLayout) dialogLayout.getComponentAt(0);
        int typeComboBoxIndex = firstRow.indexOf(typeComboBox);
        firstRow.addComponentAtIndex(typeComboBoxIndex + 1, statusComboBox);

        return dialogLayout;
    }

    private ComboBox<String> createStatusComboBox()
    {
        ComboBox<String> comboBox = new ComboBox<>("Status");
        comboBox.setItems(String.valueOf(TournamentStatusEnum.PENDING));
        comboBox.setValue(String.valueOf(tournament.getTournamentStatus()));
        comboBox.setWidth("20%");
        comboBox.setRequired(true);

        return comboBox;
    }

    private void prefillFields(Tournament tournament)
    {
        log.debug("Pre-fill fields with existing tournament data");
        tournamentNameField.setValue(tournament.getTournamentName());
        typeComboBox.setValue(tournament.getTournamentType());
        setsCountComboBox.setValue(tournament.getSetsToWin());
        semifinalsSetsCountComboBox.setValue(tournament.getSemifinalsSetsToWin());
        finalsSetsCountComboBox.setValue(tournament.getFinalsSetsToWin());
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
        log.info("Save button clicked. Attempting to update tournament {}", tournament.getTournamentName());

        boolean startNow;
        try
        {
            startNow = startNowCheckbox.getValue();

            tournament.setTournamentName(tournamentNameField.getValue());
            tournament.setTournamentType(typeComboBox.getValue());
            tournament.setTournamentStatus(TournamentStatusEnum.PENDING);
            tournament.setSetsToWin(setsCountComboBox.getValue());
            tournament.setSemifinalsSetsToWin(semifinalsSetsCountComboBox.getValue());
            tournament.setFinalsSetsToWin(finalsSetsCountComboBox.getValue());
            tournament.setPlayers(selectedPlayersSet);
            tournament.setMaxPlayers(TournamentUtils.calculateMaxPlayers(tournament));

        }
        catch (Exception e)
        {
            log.error("Error fetching data from Vaadin Components for saving tournament: {}", e.getMessage(), e);
            NotificationManager.showErrorNotification(Constants.TOURNAMENT_UPDATE_ERROR + ExceptionUtils.getExceptionMessage(e));

            return;
        }

        try
        {
            tournament = tournamentService.saveTournament(tournament);

            log.info("Tournament saved successfully: {}", tournament.getId());
            NotificationManager.showInfoNotification(Constants.TOURNAMENT_UPDATE_SUCCESS_MESSAGE);
        }
        catch (IllegalArgumentException illegalArgumentException)
        {
            NotificationManager.showErrorNotification(Constants.TOURNAMENT_UPDATE_ERROR + illegalArgumentException.getMessage());

            return;
        }

        if (startNow)
        {
            try
            {
                tournamentService.startTournament(tournament);

                UI.getCurrent().navigate("tournament/matches/" + tournament.getId());

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
