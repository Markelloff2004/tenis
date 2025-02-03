package org.cedacri.pingpong.views.playersview.components;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.utils.Constraints;
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
//    protected TextField createdAtField;



    public PlayerEditDialog(Player player, PlayerService playerService, Runnable onSaveCallback) {
        super("Edit Player");

        this.playerService = playerService;
        this.onSaveCallback = onSaveCallback;
        this.player = player;

        initializeFields();
        add(createFieldsLayout(), createButtonsLayout());

        logger.debug("Initializing fields for editing...");

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

        handComboBox = ViewUtils.createComboBox("Hand", Constraints.PLAYING_HAND);
        handComboBox.setRequired(true);

        ratingField = ViewUtils.createIntegerField("Rating");
        wonMatchesField = new IntegerField("Won Matches");
        lostMatchesField = new IntegerField("Lost Matches");
        goalsScoredField = new IntegerField("Goals Scored");
        goalsLostField = new IntegerField("Goals Lost");
//        createdAtField = ViewUtils.createTextField("Created At");
//        createdAtField.setReadOnly(true);

        logger.debug("Initialized fields for editing.");

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
//                ,createdAtField

        );

//        fields.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
//        fields.setSpacing(false);
        fields.setPadding(false);

        return fields;
    }
    @Override
    protected void onSave() {
        logger.info("Saving player: {} {}", player.getName(), player.getSurname());
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
            playerService.save(player);

            onSaveCallback.run();

            close();

            NotificationManager.showInfoNotification("Player updated successfully!");
        } catch (Exception e) {
            logger.error("Error saving player: {}", e.getMessage());
            Notification.show("Error updating player: " + e.getMessage());
        }
    }
}

