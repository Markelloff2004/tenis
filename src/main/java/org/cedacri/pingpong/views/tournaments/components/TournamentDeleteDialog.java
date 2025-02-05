package org.cedacri.pingpong.views.tournaments.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.ExceptionUtils;
import org.cedacri.pingpong.utils.NotificationManager;
import org.cedacri.pingpong.utils.ViewUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TournamentDeleteDialog extends Dialog {

    public static final Logger logger = LoggerFactory.getLogger(TournamentEditDialog.class);

    public TournamentDeleteDialog(TournamentService tournamentService, Tournament tournamentDelete, Runnable onDeleteCallback) {
        logger.info("Initializing TournamentDeleteDialog for tournament: {} with Id: {}", tournamentDelete.getTournamentName(), tournamentDelete.getId());

        setHeaderTitle("Delete Tournament");
        add("Are you sure you want to delete the tournament \"" + tournamentDelete.getTournamentName() + "\"?");

        Button confirmButton = ViewUtils.createButton(
                "Delete",
                "colored-button",
                () -> {
                    logger.debug("Delete button clicked. Attempting to delete tournament: {} with Id: {}", tournamentDelete.getTournamentName(), tournamentDelete.getId());

                    int id = tournamentDelete.getId();
                    try {
                        tournamentService.deleteById(id);
                        logger.info("Tournament deleted successfully! id: {}", id);

                        NotificationManager.showInfoNotification("Tournament deleted successfully! id: " + id);
                        onDeleteCallback.run();
                        close();
                    } catch (Exception e) {
                        NotificationManager.showErrorNotification("Error deleting tournament: " + ExceptionUtils.getExceptionMessage(e));
                        logger.error("Error deleting tournament: {}, {}", id, e.getMessage());
                    }
                }
        );

        Button cancelButton = ViewUtils.createButton("Cancel", "button", () -> {
            logger.info("Cancel button clicked. Closing TournamentDeleteDialog.");
            close();
        } );

        add(ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.CENTER, confirmButton, cancelButton));
    }
}
