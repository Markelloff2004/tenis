package org.cedacri.pingpong.views.tournaments.v1.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.BaseTournament;
import org.cedacri.pingpong.enums.RoleEnum;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.utils.ViewUtils;

import java.util.HashSet;

@Slf4j
public class TournamentInfoDialog extends AbstractTournamentDialog {

    private final BaseTournament baseTournament;

    private final ComboBox<String> statusComboBox;

    public TournamentInfoDialog(PlayerService playerService, BaseTournament baseTournament) {
        super("Tournament Details:");

        this.baseTournament = baseTournament;


        this.selectedPlayersSet = baseTournament.getPlayers();
        this.availablePlayersSet = new HashSet<>();
        initializePlayerSets(playerService);
        initializeFields();

        statusComboBox = createStatusComboBox();
        configureComboBoxes();

        initializeGrids(false);


        add(createDialogLayoutWithStatus(), createPlayersLayout(), createDialogButtons());

        log.debug("Initializing fields for editing...");

        prefillFields(baseTournament);
        setReadOnlyForFields();
    }

    private void setReadOnlyForFields() {
        log.debug("Set readOnly for fields");
        tournamentNameField.setReadOnly(true);
        typeComboBox.setReadOnly(true);
        statusComboBox.setReadOnly(true);
        setsCountComboBox.setReadOnly(true);
        semifinalsSetsCountComboBox.setReadOnly(true);
        finalsSetsCountComboBox.setReadOnly(true);
    }

    @Override
    protected HorizontalLayout createPlayersLayout() {
        return ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.BETWEEN,
                ViewUtils.createVerticalLayout(FlexComponent.JustifyContentMode.CENTER, new Span("Selected Players"), selectedPlayersGrid)
        );
    }

    @Override
    protected HorizontalLayout createDialogButtons() {

        if (!baseTournament.getTournamentStatus().equals(TournamentStatusEnum.PENDING)) {
            Button saveButton = ViewUtils.createSecuredButton(
                    "Matches",
                    ViewUtils.COLORED_BUTTON,
                    this::onSave,
                    RoleEnum.ADMIN, RoleEnum.MANAGER
            );
            Button cancelButton = ViewUtils.createButton("Cancel", ViewUtils.BUTTON, this::onCancel);

            return ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.CENTER, saveButton, cancelButton);
        } else {
            Button cancelButton = ViewUtils.createButton("Cancel", ViewUtils.BUTTON, this::onCancel);

            return ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.CENTER, cancelButton);

        }

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
        comboBox.setValue(String.valueOf(baseTournament.getTournamentStatus()));
        comboBox.setWidth("20%");
        comboBox.setRequired(true);
        comboBox.setReadOnly(true);
        return comboBox;
    }

    private void prefillFields(BaseTournament baseTournament) {
        log.debug("Pre-fill fields with existing tournament data");
        tournamentNameField.setValue(baseTournament.getTournamentName());
        typeComboBox.setValue(baseTournament.getTournamentType());
        setsCountComboBox.setValue(baseTournament.getSetsToWin());
//        semifinalsSetsCountComboBox.setValue(baseTournament.getSemifinalsSetsToWin());
//        finalsSetsCountComboBox.setValue(baseTournament.getFinalsSetsToWin());
    }

    @Override
    protected void onSave() {
        getUI().ifPresent(ui -> ui.navigate("tournament/matches/" + baseTournament.getId()));
        log.info("Navigating to tournament details page, id: {}", baseTournament.getId());
        close();
    }
}
