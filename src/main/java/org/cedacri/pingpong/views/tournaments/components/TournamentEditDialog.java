package org.cedacri.pingpong.views.tournaments.components;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.enums.SetTypesEnum;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.Constraints;
import org.cedacri.pingpong.utils.NotificationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

public class TournamentEditDialog extends AbstractTournamentDialog {

    private static final Logger logger = LoggerFactory.getLogger(TournamentEditDialog.class);

    private final TournamentService tournamentService;
    private final Runnable onSaveCallback;
    private final Tournament tournament;

    private final ComboBox<String> statusComboBox;

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

        add(createDialogLayoutWithStatus(), createPlayersLayout(), createDialogButtons());

        logger.debug("Initializing fields for editing...");

        prefillFields(tournament);
        refreshGrids();
    }

    private VerticalLayout createDialogLayoutWithStatus() {
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
        typeComboBox.setValue(tournament.getTournamentType().toString());
        setsCountComboBox.setValue(tournament.getSetsToWin().toString());
        semifinalsSetsCountComboBox.setValue(tournament.getSemifinalsSetsToWin().toString());
        finalsSetsCountComboBox.setValue(tournament.getFinalsSetsToWin().toString());
    }

    @Override
    protected void onSave() {
            logger.info("Save button clicked. Attempting to update tournament {}", tournament.getTournamentName());

            try{
                tournament.setTournamentName(tournamentNameField.getValue());
                tournament.setTournamentType(TournamentTypeEnum.valueOf(typeComboBox.getValue().toUpperCase()));
                tournament.setTournamentStatus(TournamentStatusEnum.valueOf(statusComboBox.getValue()));
                tournament.setPlayers(selectedPlayersSet);
                tournament.setSetsToWin(SetTypesEnum.valueOf(setsCountComboBox.getValue().toUpperCase()));
                tournament.setSemifinalsSetsToWin(SetTypesEnum.valueOf(semifinalsSetsCountComboBox.getValue().toUpperCase()));
                tournament.setFinalsSetsToWin(SetTypesEnum.valueOf(finalsSetsCountComboBox.getValue().toUpperCase()));

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
    }
}
