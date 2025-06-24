package org.cedacri.pingpong.views.tournaments.v2.components.dialogs;

import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.model.tournament.BaseTournament;
import org.cedacri.pingpong.service.players.PlayerService;
import org.cedacri.pingpong.service.tournaments.UnifiedTournamentService;
import org.cedacri.pingpong.utils.ExceptionUtils;
import org.cedacri.pingpong.utils.NotificationManager;

@Slf4j
public class TournamentEditDialog extends AbstractTournamentDialog {

    private final UnifiedTournamentService tournamentService;
    private final Runnable saveCallback;

    public TournamentEditDialog(UnifiedTournamentService tournamentService,
                                PlayerService playerService,
                                BaseTournament tournament,
                                Runnable saveCallback) {
        super("Edit Tournament", playerService, true, tournament);
        this.tournamentService = tournamentService;
        this.saveCallback = saveCallback;

        currentMode = Mode.EDIT;
        typeComboBox.setReadOnly(true);
        populateFieldsFromTournament(this.tournament);
    }


    @Override
    protected void onSave() {
        BaseTournament updatedTournament = createTournamentFromFields();
        if (updatedTournament == null) return;

        updatedTournament.setId(tournament.getId());

        try {
            tournamentService.getDefaultCrudService().updateTournament(updatedTournament);
            closeDialog();
            NotificationManager.showInfoNotification("Tournament updated successfully");
            saveCallback.run();
        } catch (Exception e) {
            NotificationManager.showErrorNotification("Update failed: " + ExceptionUtils.getExceptionMessage(e));
            log.error(e.getMessage(), e);
        }
    }
}