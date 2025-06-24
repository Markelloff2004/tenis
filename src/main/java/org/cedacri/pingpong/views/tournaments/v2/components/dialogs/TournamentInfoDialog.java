package org.cedacri.pingpong.views.tournaments.v2.components.dialogs;

import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.cedacri.pingpong.model.tournament.BaseTournament;
import org.cedacri.pingpong.service.players.PlayerService;
import org.cedacri.pingpong.service.tournaments.BaseTournamentService;
import org.cedacri.pingpong.utils.ViewUtils;

public class TournamentInfoDialog extends AbstractTournamentDialog {

    public TournamentInfoDialog(BaseTournamentService<BaseTournament> tournamentService,
                                PlayerService playerService,
                                BaseTournament tournament) {
        super("Tournament Details", playerService, false, tournament);

        currentMode = Mode.VIEW;
        populateFieldsFromTournament(this.tournament);
        configureReadOnlyState(true);
    }

    @Override
    protected HorizontalLayout createDialogButtons() {
        return ViewUtils.createHorizontalLayout(
                FlexComponent.JustifyContentMode.CENTER,
                ViewUtils.createButton("Close", ViewUtils.BUTTON, this::closeDialog)
        );
    }

    @Override
    protected HorizontalLayout createPlayersLayout() {
        return ViewUtils.createHorizontalLayout(
                FlexComponent.JustifyContentMode.BETWEEN,
                createPlayerSection("Participants", selectedPlayersGrid)
        );
    }


    @Override
    protected void onSave() {
        throw new UnsupportedOperationException("Save operation not supported in view mode");
    }
}