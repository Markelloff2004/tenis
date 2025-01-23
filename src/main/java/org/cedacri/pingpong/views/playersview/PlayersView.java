package org.cedacri.pingpong.views.playersview;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.utils.Constraints;
import org.cedacri.pingpong.utils.NotificationManager;
import org.cedacri.pingpong.utils.ViewUtils;
import org.cedacri.pingpong.views.MainLayout;
import org.cedacri.pingpong.views.interfaces.PlayerViewManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.stream.Collectors;

@PageTitle("PlayersView")
@Route(value = "players", layout = MainLayout.class)
@CssImport("./themes/ping-pong-tournament/main-layout.css")
@Uses(Icon.class)
public class PlayersView extends VerticalLayout implements PlayerViewManagement
{

    private static final Logger logger = LoggerFactory.getLogger(PlayersView.class);

    private final PlayerService playerService;

    private Grid<Player> playersGrid;

    public PlayersView(PlayerService playerService)
    {
        this.playerService = playerService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        configureGrid();
        createPageHeader();

        add(playersGrid);

        logger.info("PlayersView initialized");

    }

    private void createPageHeader() {
        H1 title = new H1("Players list");
        title.addClassName("players-title");

        Button addPlayerButton = ViewUtils.createButton("New player", "colored-button", this::showCreatePlayer);

        add(ViewUtils.createHorizontalLayout(JustifyContentMode.START, title));
        add(ViewUtils.createHorizontalLayout(JustifyContentMode.END, addPlayerButton));
    }

    private void configureGrid()
    {
        playersGrid = new Grid<>(Player.class, false);
        playersGrid.setSizeFull();

        playersGrid.addColumn(Player::getRating).setHeader("Rating").setSortable(true).setKey("rating");
        playersGrid.addColumn(Player::getName).setHeader("Name").setSortable(true).setKey("playerName");
        playersGrid.addColumn(Player::getBirthDate).setHeader("Age").setSortable(true).setKey("age");
        playersGrid.addColumn(Player::getHand).setHeader("Playing Style").setSortable(true).setKey("hand");
        playersGrid.addColumn(Player::getWonMatches).setHeader("Won Matches").setSortable(true).setKey("wonMatches");
        playersGrid.addColumn(Player::getLostMatches).setHeader("Losed Matches").setSortable(true).setKey("lostMatches");

        playersGrid.addColumn(
                new ComponentRenderer<>(player ->
                {
                    Button detailsButton = ViewUtils.createButton("Details", "compact-button", () -> showDetailsPlayer(player));
                    Button editButton = ViewUtils.createButton("Edit", "compact-button", () -> showEditPlayer(player));
                    Button deleteButton = ViewUtils.createButton("Delete", "compact-button", () -> showDeletePlayer(player));

                    return ViewUtils.createHorizontalLayout(JustifyContentMode.CENTER, detailsButton, editButton, deleteButton);
                })
                )
                .setHeader("Actions")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.CENTER);

        showAllPlayers();
//        refreshGridData();
    }

    @Override
    public void showAllPlayers()
    {
        logger.info("Loading all players");
        playersGrid.setItems(playerService.getAll().collect(Collectors.toSet()));
    }

    @Override
    public void showCreatePlayer()
    {
        logger.info("Opening dialog to create new player");

        Dialog dialog = new Dialog();
        dialog.setWidth("300px");
        dialog.setHeight("auto");

        TextField nameField = new TextField();
        nameField.setRequired(true);
        nameField.setWidthFull();

        TextField surnameField = new TextField();
        surnameField.setRequired(true);
        surnameField.setWidthFull();

        DatePicker birthDateTimePicker = new DatePicker();
        birthDateTimePicker.setWidthFull();

        TextField emailField = new TextField();
        emailField.setWidthFull();

        ComboBox<String> handComboBox = new ComboBox<>();
        handComboBox.setItems(Constraints.PLAYING_HAND);
        handComboBox.setWidthFull();

        FormLayout formLayout = new FormLayout();
        formLayout.addFormItem(nameField, "Name").getStyle().set("flex-direction", "column").set("margin-bottom", "5px");
        formLayout.addFormItem(surnameField, "Surname").getStyle().set("flex-direction", "column").set("margin-bottom", "5px");
        formLayout.addFormItem(birthDateTimePicker, "Birth Date").getStyle().set("flex-direction", "column").set("margin-bottom", "5px");
        formLayout.addFormItem(emailField, "Email").getStyle().set("flex-direction", "column").set("margin-bottom", "5px");
        formLayout.addFormItem(handComboBox, "Playing Hand").getStyle().set("flex-direction", "column").set("margin-bottom", "5px");


        Button saveButton = ViewUtils.createButton("Save", "colored-button", () ->
        {
            String name = nameField.getValue();
            String surname = surnameField.getValue();
            LocalDate birthDate = birthDateTimePicker.getValue();
            String email = emailField.getValue();
            String hand = handComboBox.getValue();

            if (name.isBlank() || name.isEmpty() || surname.isEmpty() || birthDate.toString().isEmpty() || email.isEmpty() || hand.isEmpty()) {
                NotificationManager.showInfoNotification("Please fill in all required fields.");
                logger.warn("Player creation failed: Empty fields are present");
                return;
            }

            Player newPlayer = new Player(name, surname, birthDate, email, Date.from(Instant.now()), 0, hand, 0, 0, 0, 0);

            try
            {
                playerService.save(newPlayer);

                dialog.close();
                showAllPlayers();
                NotificationManager.showInfoNotification("Player added successfully: " + newPlayer.getName() + " " + newPlayer.getSurname());
                logger.info("Player added successfully: {} {}", newPlayer.getName(), newPlayer.getSurname());
            }catch (Exception e)
            {
               NotificationManager.showInfoNotification("Player cannot be added : " + e.getMessage());
                logger.error("Error creating player : {}", e.getMessage(), e);
            }
        });
        saveButton.setWidth("100px");

        Button cancelButton = ViewUtils.createButton("Cancel", "button", dialog::close);
        cancelButton.setWidth("100px");


        // Add buttons to a HorizontalLayout and center them
        HorizontalLayout buttonLayout = ViewUtils.createHorizontalLayout(JustifyContentMode.CENTER, saveButton, cancelButton);
        buttonLayout.getStyle().set("margin-top", "10px"); // Reduced space above buttons

        // Add form layout and buttons to the dialog
        dialog.add(formLayout, buttonLayout);

        dialog.open();
    }

    @Override
    public void showDetailsPlayer(Player player)
    {
        logger.info("Showing details player : {} {}:{}", player.getName(), player.getSurname(), getId());

        Dialog playerDetailsDialog = new Dialog();
        playerDetailsDialog.setWidth("400px");

        FormLayout formLayout = new FormLayout();
        formLayout.getStyle().set("gap", "5px");

        formLayout.addFormItem(new Span(player.getName()), "Name");
        formLayout.addFormItem(new Span(player.getSurname()), "Surname");
        formLayout.addFormItem(new Span(player.getEmail()), "Email");
        formLayout.addFormItem(new Span(player.getBirthDate() != null ? player.getBirthDate().toString() : "N/A"), "Age");
        formLayout.addFormItem(new Span(player.getHand()), "Playing Hand");
        formLayout.addFormItem(new Span(player.getRating() != null ? player.getRating().toString() : "N/A"), "Rating");
        formLayout.addFormItem(new Span(player.getWonMatches() != null ? player.getWonMatches().toString() : "N/A"), "Won Matches");
        formLayout.addFormItem(new Span(player.getLostMatches() != null ? player.getLostMatches().toString() : "N/A"), "Lost Matches");
        formLayout.addFormItem(new Span(player.getGoalsScored() != null ? player.getGoalsScored().toString() : "N/A"), "Goals Scored");
        formLayout.addFormItem(new Span(player.getGoalsLost() != null ? player.getGoalsLost().toString() : "N/A"), "Goals Lost");
        formLayout.addFormItem(new Span(player.getCreatedAt() != null ? player.getCreatedAt().toString() : "N/A"), "Created At");

        Button closeButton = ViewUtils.createButton("Close", "button", playerDetailsDialog::close);

        HorizontalLayout buttonLayout = ViewUtils.createHorizontalLayout(JustifyContentMode.CENTER ,closeButton);

        playerDetailsDialog.add(formLayout, buttonLayout);

        playerDetailsDialog.open();
    }

    @Override
    public void showEditPlayer(Player player)
    {
        logger.info("Editing player: {} with Id: {}", player.getName() + " " + player.getSurname(), player.getId());
        Dialog playerEditDialog = new Dialog();
        playerEditDialog.setWidth("600px");

        TextField nameField = new TextField();
        nameField.setValue(player.getName() != null ? player.getName() : "");
        nameField.setWidth("300px");

        TextField surnameField = new TextField();
        nameField.setValue(player.getSurname() != null ? player.getSurname() : "");
        nameField.setWidth("300px");

        TextField emailField = new TextField();
        emailField.setValue(player.getEmail() != null ? player.getEmail() : "");
        emailField.setWidth("300px");

        DatePicker birthDatePicker = new DatePicker();
        birthDatePicker.setValue(player.getBirthDate() != null ? player.getBirthDate() : LocalDate.now());
        birthDatePicker.setWidth("300px");

        TextField handField = new TextField();
        handField.setValue(player.getHand() != null ? player.getHand() : "");
        handField.setWidth("300px");

        IntegerField ratingField = new IntegerField();
        ratingField.setValue(player.getRating() != null ? player.getRating() : 0);
        ratingField.setWidth("300px");

        IntegerField wonMatchesField = new IntegerField();
        wonMatchesField.setValue(player.getWonMatches() != null ? player.getWonMatches() : 0);
        wonMatchesField.setWidth("300px");

        IntegerField lostMatchesField = new IntegerField();
        lostMatchesField.setValue(player.getLostMatches() != null ? player.getLostMatches() : 0);
        lostMatchesField.setWidth("300px");

        IntegerField goalsScoredField = new IntegerField();
        goalsScoredField.setValue(player.getGoalsScored() != null ? player.getGoalsScored() : 0);
        goalsScoredField.setWidth("300px");

        IntegerField goalsLostField = new IntegerField();
        goalsLostField.setValue(player.getGoalsLost() != null ? player.getGoalsLost() : 0);
        goalsLostField.setWidth("300px");

        TextField createdAtField = new TextField();
        createdAtField.setValue(player.getCreatedAt() != null ? player.getCreatedAt().toString() : "N/A");
        createdAtField.setReadOnly(true);
        createdAtField.setWidth("300px");

        FormLayout formLayout = new FormLayout();
        formLayout.addFormItem(nameField, "Name");
        formLayout.addFormItem(surnameField, "Surname");
        formLayout.addFormItem(emailField, "Email");
        formLayout.addFormItem(birthDatePicker, "Birth");
        formLayout.addFormItem(handField, "Playing Hand");
        formLayout.addFormItem(ratingField, "Rating");
        formLayout.addFormItem(wonMatchesField, "Won Matches");
        formLayout.addFormItem(lostMatchesField, "Lost Matches");
        formLayout.addFormItem(goalsScoredField, "Goals Scored");
        formLayout.addFormItem(goalsLostField, "Goals Lost");
        formLayout.addFormItem(createdAtField, "Created At");

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );

        Button saveButton = ViewUtils.createButton("Save", "colored-button", () ->
        {
            player.setName(nameField.getValue());
            player.setSurname(surnameField.getValue());
            player.setEmail(emailField.getValue());
            player.setBirthDate(birthDatePicker.getValue());
            player.setHand(handField.getValue());
            player.setRating(ratingField.getValue());
            player.setWonMatches(wonMatchesField.getValue());
            player.setLostMatches(lostMatchesField.getValue());
            player.setGoalsScored(goalsScoredField.getValue());
            player.setGoalsLost(goalsLostField.getValue());

            try {
                playerService.save(player);
                showAllPlayers();

                playerEditDialog.close();

                NotificationManager.showInfoNotification("Player updated successfully: " + player.getName() + " " +
                        player.getSurname() + ": " + player.getId());
            }
            catch (Exception e)
            {
                NotificationManager.showInfoNotification("Player cannot be updated : " + e.getMessage());
            }
        });

        Button cancelButton = ViewUtils.createButton("Cancel", "button", playerEditDialog::close);

        playerEditDialog.add(formLayout, ViewUtils.createHorizontalLayout(JustifyContentMode.CENTER, saveButton, cancelButton));
        playerEditDialog.open();
    }

    @Override
    public void showDeletePlayer(Player player)
    {
        logger.info("Preparing to delete player: {}, Id: {}", player.getName() + " " + player.getSurname(), player.getId());
        Dialog playerDeleteDialog = new Dialog();
        playerDeleteDialog.setWidth("500px");
        playerDeleteDialog.setHeaderTitle("Confirm Delete");

        Span confirmationText = new Span("Are you sure you want to delete " + player.getName() + " " +
                player.getSurname() + ": " + player.getId() + "?");
        confirmationText.getStyle().set("margin", "10px 0");

        Button deleteButton = ViewUtils.createButton("Delete", "colored-button", () ->
        {
            try {
                playerService.deleteById(player.getId());
                NotificationManager.showInfoNotification("Player " + player.getName() + " " + player.getSurname() +
                        ": " + player.getId() + " deleted!");
                playerDeleteDialog.close();
                showAllPlayers();
                logger.info("Player deleted successfully: {}, Id: {} {}", player.getId(), player.getName(), player.getSurname());
            }
            catch (Exception e)
            {
                NotificationManager.showInfoNotification("Player cannot be deleted : " + e.getMessage());
            }
        });

        Button cancelButton = ViewUtils.createButton("Cancel", "button", playerDeleteDialog::close);

        HorizontalLayout buttonLayout = ViewUtils.createHorizontalLayout(JustifyContentMode.CENTER, deleteButton, cancelButton);

        playerDeleteDialog.add(confirmationText, buttonLayout);
        playerDeleteDialog.open();
    }
}
