package org.cedacri.pingpong.views.playersview.components;

import jakarta.validation.ConstraintViolationException;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.utils.Constraints;
import org.cedacri.pingpong.utils.ExceptionUtils;
import org.cedacri.pingpong.utils.NotificationManager;

public class PlayerSaveDialog extends AbstractPlayerDialog {

    private PlayerService playerService;
    private Runnable onSaveCallback;
    private Player player;

    public PlayerSaveDialog(PlayerService playerService, Runnable onSaveCallback) {
        super("Save Player");
        this.playerService = playerService;
        this.onSaveCallback = onSaveCallback;
        this.player = new Player();

        initializeFields();
        add(createFieldsLayout(), createButtonsLayout());

        logger.debug("Initializing fields for editing...");
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

        try {
            playerService.save(player);
            onSaveCallback.run();
            close();
            NotificationManager.showInfoNotification("Player saved successfully!");
        } catch (ConstraintViolationException e) {
            logger.error(Constraints.PLAYER_UPDATE_ERROR + "{}", e.getMessage());
            NotificationManager.showErrorNotification(Constraints.PLAYER_UPDATE_ERROR + ExceptionUtils.getExceptionMessage(e));
        }
    }
}