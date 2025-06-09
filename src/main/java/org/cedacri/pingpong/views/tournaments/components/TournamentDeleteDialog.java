package org.cedacri.pingpong.views.tournaments.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.TournamentOlympic;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.ExceptionUtils;
import org.cedacri.pingpong.utils.NotificationManager;
import org.cedacri.pingpong.utils.ViewUtils;

@Slf4j
public class TournamentDeleteDialog extends Dialog
{

    public TournamentDeleteDialog(TournamentService tournamentService, TournamentOlympic tournamentOlympicDelete, Runnable onDeleteCallback)
    {
        log.info("Initializing TournamentDeleteDialog for tournament: {} with Id: {}", tournamentOlympicDelete.getTournamentName(), tournamentOlympicDelete.getId());

        setHeaderTitle("Delete Tournament");
        add("Are you sure you want to delete the tournament \"" + tournamentOlympicDelete.getTournamentName() + "\"?");

        Button confirmButton = ViewUtils.createButton(
                "Delete",
                ViewUtils.COLORED_BUTTON,
                () ->
                {
                    log.debug("Delete button clicked. Attempting to delete tournament: {} with Id: {}", tournamentOlympicDelete.getTournamentName(), tournamentOlympicDelete.getId());

                    int id = tournamentOlympicDelete.getId();
                    try
                    {
                        tournamentService.deleteTournamentById(id);
                        log.info("Tournament deleted successfully! id: {}", id);

                        NotificationManager.showInfoNotification("Tournament deleted successfully! id: " + id);
                        onDeleteCallback.run();
                        close();
                    }
                    catch (Exception e)
                    {
                        NotificationManager.showErrorNotification("Error deleting tournament: " + ExceptionUtils.getExceptionMessage(e));
                        log.error("Error deleting tournament: {}, {}", id, e.getMessage());
                    }
                }
        );

        Button cancelButton = ViewUtils.createButton("Cancel", ViewUtils.BUTTON, () ->
        {
            log.info("Cancel button clicked. Closing TournamentDeleteDialog.");
            close();
        });

        add(ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.CENTER, confirmButton, cancelButton));
    }
}
