package org.cedacri.pingpong.views.tournaments.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.Constants;
import org.cedacri.pingpong.utils.ExceptionUtils;
import org.cedacri.pingpong.utils.NotificationManager;
import org.cedacri.pingpong.utils.TournamentUtils;
import org.cedacri.pingpong.utils.ViewUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

@Slf4j
public class TournamentEditDialog extends AbstractTournamentDialog {

    private final TournamentService tournamentService;
    private final PlayerService playerService;
    private final Runnable onSaveCallback;
    private Tournament tournament;

    private final ComboBox<String> statusComboBox;
    private final Checkbox startNowCheckbox = ViewUtils.createCheckBox("Start Now");

    public TournamentEditDialog(TournamentService tournamentService, PlayerService playerService, Tournament tournament, Runnable onSaveCallback) {
        super("Edit Tournament");

        this.tournamentService = tournamentService;
        this.playerService = playerService;
        this.tournament = tournament;
        this.onSaveCallback = onSaveCallback;
        this.selectedPlayersSet = tournament.getPlayers();
        this.availablePlayersSet = new HashSet<>();

        statusComboBox = createStatusComboBox();
        initializePlayerSets(playerService, selectedPlayersSet);
        initializeFields();
        configureComboBoxes();
        initializeGrids(true);
        add(createDialogLayoutWithStatus(), createPlayersLayout(), createDialogButtons());
        log.debug("Initializing fields for editing...");

        prefillFields(tournament);
        refreshGrids();
    }

    protected VerticalLayout createDialogLayoutWithStatus() {
        VerticalLayout dialogLayout = createDialogLayout();
        HorizontalLayout firstRow = (HorizontalLayout) dialogLayout.getComponentAt(0);
        int typeComboBoxIndex = firstRow.indexOf(typeComboBox);
        firstRow.addComponentAtIndex(typeComboBoxIndex + 1, statusComboBox);

        return dialogLayout;
    }

    private ComboBox<String> createStatusComboBox() {
        ComboBox<String> comboBox = new ComboBox<>("Status");
        comboBox.setItems(String.valueOf(TournamentStatusEnum.PENDING));
        comboBox.setValue(String.valueOf(tournament.getTournamentStatus()));
        comboBox.setWidth("20%");
        comboBox.setRequired(true);

        return comboBox;
    }

    private void prefillFields(Tournament tournament) {
        log.debug("Pre-fill fields with existing tournament data");
        tournamentNameField.setValue(tournament.getTournamentName());
        typeComboBox.setValue(tournament.getTournamentType());
        setsCountComboBox.setValue(tournament.getSetsToWin());
        semifinalsSetsCountComboBox.setValue(tournament.getSemifinalsSetsToWin());
        finalsSetsCountComboBox.setValue(tournament.getFinalsSetsToWin());
    }

    @Override
    protected HorizontalLayout createDialogButtons() {
        Button saveButton = ViewUtils.createButton("Save", "colored-button", this::onSave);
        Button cancelButton = ViewUtils.createButton("Cancel", "button", this::onCancel);

        return ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.CENTER, startNowCheckbox, saveButton, cancelButton);
    }

    @Override
    protected void onSave() {
            log.info("Save button clicked. Attempting to update tournament {}", tournament.getTournamentName());

        try
        {
            boolean startNow = startNowCheckbox.getValue();

            tournament = tournamentService.updateTournament(
                    tournament,
                    tournamentNameField.getValue(),
                    typeComboBox.getValue(),
                    setsCountComboBox.getValue(),
                    semifinalsSetsCountComboBox.getValue(),
                    finalsSetsCountComboBox.getValue(),
                    selectedPlayersSet,
                    startNow
            );

            log.info("Tournament saved successfully: {}", tournament.getId());
            onSaveCallback.run();
            NotificationManager.showInfoNotification(Constants.TOURNAMENT_UPDATE_SUCCESS);
            close();
        } catch (Exception e)
        {
            log.error("Error saving tournament: {}", e.getMessage(), e);
            NotificationManager.showErrorNotification(Constants.TOURNAMENT_UPDATE_ERROR + ExceptionUtils.getExceptionMessage(e));
        }
    }
}
