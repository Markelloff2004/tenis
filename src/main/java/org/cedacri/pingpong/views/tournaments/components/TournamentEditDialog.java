package org.cedacri.pingpong.views.tournaments.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.enums.SetTypesEnum;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.Constraints;
import org.cedacri.pingpong.utils.ExceptionUtils;
import org.cedacri.pingpong.utils.NotificationManager;
import org.cedacri.pingpong.utils.TournamentUtils;
import org.cedacri.pingpong.utils.ViewUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

public class TournamentEditDialog extends AbstractTournamentDialog {

    private static final Logger logger = LoggerFactory.getLogger(TournamentEditDialog.class);

    private final TournamentService tournamentService;
    private final Runnable onSaveCallback;
    private Tournament tournament;

    private final ComboBox<String> statusComboBox;

    private final Checkbox startNowCheckbox = ViewUtils.createCheckBox("Start Now");

    public TournamentEditDialog(TournamentService tournamentService, PlayerService playerService, Tournament tournament, Runnable onSaveCallback) {
        super("Edit Tournament");

        this.tournamentService = tournamentService;
        this.tournament = tournament;
        this.onSaveCallback = onSaveCallback;


        this.selectedPlayersSet = new HashSet<>();
        this.availablePlayersSet = new HashSet<>();
        initializePlayerSets(playerService, tournament.getPlayers());
        initializeFields();

        statusComboBox = createStatusComboBox();
        configureComboBoxes();

        initializeGrids(true);

        add(createDialogLayoutWithStaus(), createPlayersLayout(), createDialogButtons());

        logger.debug("Initializing fields for editing...");

        prefillFields(tournament);
        refreshGrids();
    }

    protected VerticalLayout createDialogLayoutWithStaus() {
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
        logger.debug("Pre-fill fields with existing tournament data");
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
            logger.info("Save button clicked. Attempting to update tournament {}", tournament.getTournamentName());

        try
        {
            //extract data
            tournament.setTournamentName(tournamentNameField.getValue());
            tournament.setTournamentType(typeComboBox.getValue());
            tournament.setSetsToWin(setsCountComboBox.getValue());
            tournament.setSemifinalsSetsToWin(semifinalsSetsCountComboBox.getValue());
            tournament.setFinalsSetsToWin(finalsSetsCountComboBox.getValue());

            tournament.setTournamentStatus(TournamentStatusEnum.PENDING);
            tournament.setMaxPlayers(TournamentUtils.calculateMaxPlayers( selectedPlayersSet.size()) );

            tournament = tournamentService.saveTournament(tournament);

            for (Player player : selectedPlayersSet) {
                player.getTournaments().add(tournament);
                tournament.getPlayers().add(player);
            }

            tournament = tournamentService.saveTournament(tournament);


            if (startNowCheckbox.getValue()) {

                tournamentService.startTournament(tournament);

                UI.getCurrent().getPage().setLocation("tournament/matches/" + tournament.getId());
            }

            logger.info("Tournament saved successfully: {}", tournament.getId());
            onSaveCallback.run();
            close();
            NotificationManager.showInfoNotification(Constraints.TOURNAMENT_UPDATE_SUCCESS);

        } catch (Exception e)
        {
            logger.error("Error saving tournament: {}", e.getMessage(), e);
            NotificationManager.showInfoNotification(Constraints.TOURNAMENT_UPDATE_ERROR + ExceptionUtils.getExceptionMessage(e));
        }
    }
}
