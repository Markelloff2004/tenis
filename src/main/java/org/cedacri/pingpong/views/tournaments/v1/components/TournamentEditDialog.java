//package org.cedacri.pingpong.views.tournaments.v1.components;
//
//import com.vaadin.flow.component.UI;
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.checkbox.Checkbox;
//import com.vaadin.flow.component.combobox.ComboBox;
//import com.vaadin.flow.component.orderedlayout.FlexComponent;
//import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import lombok.extern.slf4j.Slf4j;
//import org.cedacri.pingpong.entity.BaseTournament;
//import org.cedacri.pingpong.enums.TournamentStatusEnum;
//import org.cedacri.pingpong.service.PlayerService;
//import org.cedacri.pingpong.service.TournamentService;
//import org.cedacri.pingpong.utils.*;
//
//import java.util.HashSet;
//
//@Slf4j
//public class TournamentEditDialog extends AbstractTournamentDialog {
//
//    private final TournamentService tournamentService;
//    private final Runnable onSaveCallback;
//    private final ComboBox<String> statusComboBox;
//    private final Checkbox startNowCheckbox = ViewUtils.createCheckBox("Start Now");
//    private BaseTournament baseTournament;
//
//    public TournamentEditDialog(TournamentService tournamentService, PlayerService playerService, BaseTournament tournamentOlympic, Runnable onSaveCallback) {
//        super("Edit Tournament");
//
//        this.tournamentService = tournamentService;
//        this.baseTournament = tournamentOlympic;
//        this.onSaveCallback = onSaveCallback;
//        this.selectedPlayersSet = tournamentOlympic.getPlayers();
//        this.availablePlayersSet = new HashSet<>();
//
//        statusComboBox = createStatusComboBox();
//        initializePlayerSets(playerService);
//        initializeFields();
//        configureComboBoxes();
//        initializeGrids(true);
//        add(createDialogLayoutWithStatus(), createPlayersLayout(), createDialogButtons());
//        log.debug("Initializing fields for editing...");
//
//        prefillFields(tournamentOlympic);
//        refreshGrids();
//    }
//
//    protected VerticalLayout createDialogLayoutWithStatus() {
//        VerticalLayout dialogLayout = createDialogLayout();
//        HorizontalLayout firstRow = (HorizontalLayout) dialogLayout.getComponentAt(0);
//        int typeComboBoxIndex = firstRow.indexOf(typeComboBox);
//        firstRow.addComponentAtIndex(typeComboBoxIndex + 1, statusComboBox);
//
//        return dialogLayout;
//    }
//
//    private ComboBox<String> createStatusComboBox() {
//        ComboBox<String> comboBox = new ComboBox<>("Status");
//        comboBox.setItems(String.valueOf(TournamentStatusEnum.PENDING));
//        comboBox.setValue(String.valueOf(baseTournament.getTournamentStatus()));
//        comboBox.setWidth("20%");
//        comboBox.setRequired(true);
//
//        return comboBox;
//    }
//
//    private void prefillFields(BaseTournament tournamentOlympic) {
//        log.debug("Pre-fill fields with existing tournament data");
//        tournamentNameField.setValue(tournamentOlympic.getTournamentName());
//        typeComboBox.setValue(tournamentOlympic.getTournamentType());
//        setsCountComboBox.setValue(tournamentOlympic.getSetsToWin());
////        semifinalsSetsCountComboBox.setValue(tournamentOlympic.getSemifinalsSetsToWin());
////        finalsSetsCountComboBox.setValue(tournamentOlympic.getFinalsSetsToWin());
//    }
//
//    @Override
//    protected HorizontalLayout createDialogButtons() {
//        Button saveButton = ViewUtils.createButton("Save", ViewUtils.COLORED_BUTTON, this::onSave);
//        Button cancelButton = ViewUtils.createButton("Cancel", ViewUtils.BUTTON, this::onCancel);
//
//        return ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.CENTER, startNowCheckbox, saveButton, cancelButton);
//    }
//
//    @Override
//    protected void onSave() {
//        log.info("Save button clicked. Attempting to update tournament {}", baseTournament.getTournamentName());
//
//        boolean startNow;
//        try {
//            startNow = startNowCheckbox.getValue();
//
//            baseTournament.setTournamentName(tournamentNameField.getValue());
//            baseTournament.setTournamentType(typeComboBox.getValue());
//            baseTournament.setTournamentStatus(TournamentStatusEnum.PENDING);
//            baseTournament.setSetsToWin(setsCountComboBox.getValue());
////            baseTournament.setSemifinalsSetsToWin(semifinalsSetsCountComboBox.getValue());
////            baseTournament.setFinalsSetsToWin(finalsSetsCountComboBox.getValue());
//            baseTournament.setPlayers(selectedPlayersSet);
//            baseTournament.setMaxPlayers(TournamentUtils.calculateMaxPlayers(baseTournament));
//
//        } catch (Exception e) {
//            log.error("Error fetching data from Vaadin Components for saving tournament: {}", e.getMessage(), e);
//            NotificationManager.showErrorNotification(Constants.TOURNAMENT_UPDATE_ERROR + ExceptionUtils.getExceptionMessage(e));
//
//            return;
//        }
//
//        try {
//            baseTournament = tournamentService.saveTournament(baseTournament);
//
//            log.info("Tournament saved successfully: {}", baseTournament.getId());
//            NotificationManager.showInfoNotification(Constants.TOURNAMENT_UPDATE_SUCCESS_MESSAGE);
//        } catch (IllegalArgumentException illegalArgumentException) {
//            NotificationManager.showErrorNotification(Constants.TOURNAMENT_UPDATE_ERROR + illegalArgumentException.getMessage());
//
//            return;
//        }
//
//        if (startNow) {
//            tournamentService.startTournament(baseTournament);
//
//            UI.getCurrent().navigate("tournament/matches/" + baseTournament.getId());
//
//            NotificationManager.showInfoNotification(Constants.TOURNAMENT_START_SUCCESS_MESSAGE);
//        }
//
//        onSaveCallback.run();
//        close();
//    }
//}
