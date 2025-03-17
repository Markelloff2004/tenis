package org.cedacri.pingpong.views.playersview.components;

import jakarta.validation.ConstraintViolationException;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.utils.Constants;
import org.cedacri.pingpong.utils.ExceptionUtils;
import org.cedacri.pingpong.utils.NotificationManager;

public class PlayerSaveDialog extends AbstractPlayerDialog {

    private final PlayerService playerService;
    private final Runnable onSaveCallback;
    private final Player player;

    public PlayerSaveDialog(PlayerService playerService, Runnable onSaveCallback) {
        super("Save Player");
        this.playerService = playerService;
        this.onSaveCallback = onSaveCallback;
        this.player = new Player();

        initializeFields();
        add(createFieldsLayout(), createButtonsLayout());

        log.debug("Initializing fields for editing...");
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

        try {
            playerService.savePlayer(player);
            onSaveCallback.run();
            close();
            NotificationManager.showInfoNotification(Constants.PLAYER_SAVE_SUCCESS);
        } catch (ConstraintViolationException e) {
            log.error(Constants.PLAYER_SAVE_ERROR + "{}", e.getMessage());
            NotificationManager.showErrorNotification(Constants.PLAYER_SAVE_ERROR + ExceptionUtils.getExceptionMessage(e));
        }
    }
}