package org.cedacri.pingpong.views.playersview.components;

import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import org.cedacri.pingpong.model.player.Player;
import org.cedacri.pingpong.service.players.PlayerService;
import org.cedacri.pingpong.utils.Constants;
import org.cedacri.pingpong.utils.ExceptionUtils;
import org.cedacri.pingpong.utils.NotificationManager;
import org.cedacri.pingpong.utils.ViewUtils;

public class PlayerEditDialog extends AbstractPlayerDialog {

    private final PlayerService playerService;
    private final Runnable onSaveCallback;
    private final Player player;

    protected IntegerField ratingField;
    protected IntegerField wonMatchesField;
    protected IntegerField lostMatchesField;
    protected IntegerField goalsScoredField;
    protected IntegerField goalsLostField;

    public PlayerEditDialog(Player player, PlayerService playerService, Runnable onSaveCallback) {
        super("Edit Player");

        this.playerService = playerService;
        this.onSaveCallback = onSaveCallback;
        this.player = player;

        initializeFields();
        add(createFieldsLayout(), createButtonsLayout());

        log.debug("Initializing fields for editing...");

        populateFields(player);
    }

    @Override
    public void initializeFields() {
        nameField = ViewUtils.createTextField("Name");
        nameField.setRequired(true);

        surnameField = ViewUtils.createTextField("Surname");
        surnameField.setRequired(true);

        emailField = ViewUtils.createTextField("Email");
        emailField.setRequired(true);

        addressField = ViewUtils.createTextField("Address");
        addressField.setRequired(true);

        birthDatePicker = ViewUtils.createDatePicker("Birth Date");
        birthDatePicker.setRequired(true);

        handComboBox = ViewUtils.createComboBox("Hand", Constants.PLAYING_HAND);
        handComboBox.setRequired(true);

        ratingField = ViewUtils.createIntegerField("Rating");
        wonMatchesField = new IntegerField("Won Matches");
        lostMatchesField = new IntegerField("Lost Matches");
        goalsScoredField = new IntegerField("Goals Scored");
        goalsLostField = new IntegerField("Goals Lost");

        log.debug("Initialized fields for editing.");

    }

    @Override
    protected void populateFields(Player player) {
        super.populateFields(player);

        ratingField.setValue(player.getRating() != null ? player.getRating() : 0);
        wonMatchesField.setValue(player.getWonMatches() != null ? player.getWonMatches() : 0);
        lostMatchesField.setValue(player.getLostMatches() != null ? player.getLostMatches() : 0);
        goalsScoredField.setValue(player.getGoalsScored() != null ? player.getGoalsScored() : 0);
        goalsLostField.setValue(player.getGoalsLost() != null ? player.getGoalsLost() : 0);

    }

    @Override
    protected VerticalLayout createFieldsLayout() {
        VerticalLayout fields = ViewUtils.createVerticalLayout(FlexComponent.JustifyContentMode.START,
                ratingField,
                nameField,
                surnameField,
                emailField,
                addressField,
                birthDatePicker,
                handComboBox,
                wonMatchesField,
                lostMatchesField,
                goalsScoredField,
                goalsLostField
        );

        fields.setPadding(false);

        return fields;
    }

    @Override
    protected void onSave() {
        log.info("Saving player: {} {}", player.getName(), player.getSurname());
        player.setName(nameField.getValue());
        player.setSurname(surnameField.getValue());
        player.setEmail(emailField.getValue());
        player.setAddress(addressField.getValue());
        player.setBirthDate(birthDatePicker.getValue());
        player.setHand(handComboBox.getValue());
        player.setRating(ratingField.getValue());
        player.setWonMatches(wonMatchesField.getValue());
        player.setLostMatches(lostMatchesField.getValue());
        player.setGoalsScored(goalsScoredField.getValue());
        player.setGoalsLost(goalsLostField.getValue());

        try {
            playerService.savePlayer(player);

            onSaveCallback.run();

            close();

            NotificationManager.showInfoNotification(Constants.PLAYER_UPDATE_SUCCESS);
        } catch (Exception e) {
            log.error(Constants.PLAYER_UPDATE_ERROR + "{}", e.getMessage());
            NotificationManager.showErrorNotification(Constants.PLAYER_UPDATE_ERROR + ExceptionUtils.getExceptionMessage(e));
        }
    }
}

