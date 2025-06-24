package org.cedacri.pingpong.views.tournaments.v2.components.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.model.tournament.BaseTournament;
import org.cedacri.pingpong.service.tournaments.UnifiedTournamentService;
import org.cedacri.pingpong.utils.ExceptionUtils;
import org.cedacri.pingpong.utils.NotificationManager;
import org.cedacri.pingpong.utils.ViewUtils;

@Slf4j
public class TournamentDeleteDialog extends Dialog {

    public TournamentDeleteDialog(UnifiedTournamentService tournamentService,
                                  BaseTournament tournament,
                                  Runnable deleteCallback) {
        setHeaderTitle("Delete Tournament");
        add("Are you sure you want to delete \"" + tournament.getTournamentName() + "\"?");

        Button confirmButton = createDeleteButton(tournamentService, tournament, deleteCallback);
        Button cancelButton = ViewUtils.createButton("Cancel", ViewUtils.BUTTON, this::close);

        add(ViewUtils.createHorizontalLayout(
                FlexComponent.JustifyContentMode.CENTER,
                confirmButton,
                cancelButton
        ));
    }

    private Button createDeleteButton(UnifiedTournamentService tournamentService,
                                      BaseTournament tournament,
                                      Runnable callback) {
        return ViewUtils.createButton("Delete", ViewUtils.COLORED_BUTTON, () -> {
            try {
                tournamentService.getDefaultCrudService().deleteTournamentById(tournament.getId());
                callback.run();
                close();
                NotificationManager.showInfoNotification("Tournament deleted successfully");
            } catch (Exception e) {
                NotificationManager.showErrorNotification(
                        "Deletion failed: " + ExceptionUtils.getExceptionMessage(e)
                );
                log.error(e.getMessage(), e);
            }
        });
    }
}