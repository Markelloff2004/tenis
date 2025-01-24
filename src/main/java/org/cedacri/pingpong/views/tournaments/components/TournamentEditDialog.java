package org.cedacri.pingpong.views.tournaments.components;

import com.vaadin.flow.component.combobox.ComboBox;
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

import java.util.stream.Collectors;

public class TournamentEditDialog extends TournamentDialog {

    private static final Logger logger = LoggerFactory.getLogger(TournamentEditDialog.class);

    private Tournament tournament;
    private TournamentService tournamentService;

    private Runnable onSaveCallback;

    protected ComboBox<String> statusComboBox;

    public TournamentEditDialog(TournamentService tournamentService, PlayerService playerService, Tournament tournament, Runnable onSaveCallback) {
        super("Edit Tournament");

        this.tournamentService = tournamentService;
        this.tournament = tournament;
        this.onSaveCallback = onSaveCallback;

        logger.debug("Initializing fields for editing...");

        prefillFields(tournament);


        selectedPlayersSet.addAll(tournament.getPlayers());
        availablePlayersSet.addAll(
                playerService.getAll()
                        .filter(p -> !selectedPlayersSet.contains(p))
                        .collect(Collectors.toSet())
        );

        refreshGrids();

        ComboBox<String> statusComboBox = new ComboBox<>("Status");
        statusComboBox.setItems(Constraints.TOURNAMENT_STATUSES);
        statusComboBox.setValue(String.valueOf(tournament.getTournamentStatus()));
        statusComboBox.setWidth("20%");
        statusComboBox.setRequired(true);
        createDialogButtons().addComponentAtIndex(0, statusComboBox);

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
