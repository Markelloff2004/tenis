package org.cedacri.pingpong.views.tournaments.v2.components.dialogs;

import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.model.tournament.BaseTournament;
import org.cedacri.pingpong.service.players.PlayerService;
import org.cedacri.pingpong.service.tournaments.BaseTournamentService;
import org.cedacri.pingpong.service.tournaments.UnifiedTournamentService;
import org.cedacri.pingpong.utils.ExceptionUtils;
import org.cedacri.pingpong.utils.NotificationManager;

@Slf4j
public class TournamentAddDialog extends AbstractTournamentDialog {

    private final UnifiedTournamentService tournamentService;
    private final Runnable saveCallback;

    public TournamentAddDialog(UnifiedTournamentService tournamentService,
                               PlayerService playerService,
                               Runnable saveCallback) {
        super("Add Tournament", playerService, true, null);
        this.tournamentService = tournamentService;
        this.saveCallback = saveCallback;
    }

    @Override
    protected void onSave() {
        BaseTournament tournament = createTournamentFromFields();
        if (tournament == null) return;

        try {
            tournamentService.getDefaultCrudService().createTournament(tournament);

            if(startNowCheckbox.getValue()) {

                NotificationManager.showInfoNotification("Tournament started successfully");
            }

            saveCallback.run();
            closeDialog();
            NotificationManager.showInfoNotification("Tournament created successfully");
        } catch (Exception e) {
            NotificationManager.showErrorNotification("Creation failed: " + ExceptionUtils.getExceptionMessage(e));
            log.error(e.getMessage(), e);
        }
    }
}