package org.cedacri.pingpong.views.playersview;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.utils.TournamentConstraints;
import org.cedacri.pingpong.utils.TournamentUtils;
import org.cedacri.pingpong.utils.ViewUtils;
import org.cedacri.pingpong.views.MainLayout;
import org.cedacri.pingpong.views.interfaces.PlayerViewManagement;

import java.time.Instant;
import java.util.stream.Collectors;

@PageTitle("PlayersView")
@Route(value = "players", layout = MainLayout.class)
@CssImport("./themes/ping-pong-tournament/main-layout.css")
@Uses(Icon.class)
public class PlayersView extends VerticalLayout implements PlayerViewManagement
{

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

    }

    private void createPageHeader() {
        H1 title = new H1("Players list");
        title.addClassName("players-title");

        Button addPlayerButton = ViewUtils.createButton("New player", "colored-button", () -> showCreatePlayer());

        add(ViewUtils.createHorizontalLayout(JustifyContentMode.START, title));
        add(ViewUtils.createHorizontalLayout(JustifyContentMode.END, addPlayerButton));
    }

    private void configureGrid()
    {
        playersGrid = new Grid<>(Player.class, false);
        playersGrid.setSizeFull();

        playersGrid.addColumn(Player::getRating).setHeader("Rating").setSortable(true).setKey("rating");
        playersGrid.addColumn(Player::getPlayerName).setHeader("Name").setSortable(true).setKey("playerName");
        playersGrid.addColumn(Player::getAge).setHeader("Age").setSortable(true).setKey("age");
        playersGrid.addColumn(Player::getPlayingHand).setHeader("Playing Style").setSortable(true).setKey("playingHand");
        playersGrid.addColumn(Player::getWinnedMatches).setHeader("Won Matches").setSortable(true).setKey("winnedMatches");
        playersGrid.addColumn(Player::getLosedMatches).setHeader("Losed Matches").setSortable(true).setKey("losedMatches");

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
        playersGrid.setItems(playerService.getAll().collect(Collectors.toSet()));
//        refreshGridData();
    }

    @Override
    public void showCreatePlayer()
    {
        Dialog dialog = new Dialog();
        dialog.setWidth("300px");
        dialog.setHeight("auto");

        TextField nameField = new TextField();
        nameField.setRequired(true);
        nameField.setWidthFull();

        IntegerField ageField = new IntegerField();
        ageField.setMin(0);
        ageField.setMax(99);
        ageField.setWidthFull();

        TextField emailField = new TextField();
        emailField.setWidthFull();

        ComboBox<String> playingHandComboBox = new ComboBox<>();
        playingHandComboBox.setItems(TournamentConstraints.PLAYING_HAND);
        playingHandComboBox.setWidthFull();


        FormLayout formLayout = new FormLayout();
        formLayout.addFormItem(nameField, "Name").getStyle().set("flex-direction", "column").set("margin-bottom", "5px");
        formLayout.addFormItem(ageField, "Age").getStyle().set("flex-direction", "column").set("margin-bottom", "5px");
        formLayout.addFormItem(emailField, "Email").getStyle().set("flex-direction", "column").set("margin-bottom", "5px");
        formLayout.addFormItem(playingHandComboBox, "Playing Hand").getStyle().set("flex-direction", "column").set("margin-bottom", "5px");


        Button saveButton = ViewUtils.createButton("Save", "colored-button", () ->
        {
            String name = nameField.getValue();
            Integer age = ageField.getValue();
            String email = emailField.getValue();
            String playingHand = playingHandComboBox.getValue();

            if (name.isBlank() || name.isEmpty()) {
                Notification.show("Please fill in all required fields.");
                return;
            }

            Player newPlayer = new Player(name, age, email, Instant.now(), 0, playingHand, 0, 0, 0, 0);

            try
            {
                playerService.save(newPlayer);

                dialog.close();
                showAllPlayers();
//                refreshGridData();
                Notification.show("Player added successfully: " + newPlayer.getPlayerName());
            }catch (Exception e)
            {
                Notification.show("Player cannot be added : " + e.getMessage());
            }
        });
        saveButton.setWidth("100px");

        Button cancelButton = ViewUtils.createButton("Cancel", "button", () -> dialog.close());
        cancelButton.setWidth("100px");


        // Add buttons to a HorizontalLayout and center them
        HorizontalLayout buttonLayout = ViewUtils.createHorizontalLayout(JustifyContentMode.CENTER, saveButton, cancelButton);
//                new HorizontalLayout(saveButton, cancelButton);
//        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER); // Center buttons horizontally
//        buttonLayout.setWidthFull();
        buttonLayout.getStyle().set("margin-top", "10px"); // Reduced space above buttons

        // Add form layout and buttons to the dialog
        dialog.add(formLayout, buttonLayout);

        dialog.open();
    }

    @Override
    public void showDetailsPlayer(Player player)
    {

        Dialog playerDetailsDialog = new Dialog();
        playerDetailsDialog.setWidth("400px");

        FormLayout formLayout = new FormLayout();
        formLayout.getStyle().set("gap", "5px");

        formLayout.addFormItem(new Span(player.getPlayerName()), "Name");
        formLayout.addFormItem(new Span(player.getEmail()), "Email");
        formLayout.addFormItem(new Span(player.getAge() != null ? player.getAge().toString() : "N/A"), "Age");
        formLayout.addFormItem(new Span(player.getPlayingHand()), "Playing Hand");
        formLayout.addFormItem(new Span(player.getRating() != null ? player.getRating().toString() : "N/A"), "Rating");
        formLayout.addFormItem(new Span(player.getWinnedMatches() != null ? player.getWinnedMatches().toString() : "N/A"), "Won Matches");
        formLayout.addFormItem(new Span(player.getLosedMatches() != null ? player.getLosedMatches().toString() : "N/A"), "Lost Matches");
        formLayout.addFormItem(new Span(player.getGoalsScored() != null ? player.getGoalsScored().toString() : "N/A"), "Goals Scored");
        formLayout.addFormItem(new Span(player.getGoalsLosed() != null ? player.getGoalsLosed().toString() : "N/A"), "Goals Lost");
        formLayout.addFormItem(new Span(player.getCreatedAt() != null ? player.getCreatedAt().toString() : "N/A"), "Created At");

        Button closeButton = ViewUtils.createButton("Close", "button", () -> playerDetailsDialog.close());

        HorizontalLayout buttonLayout = ViewUtils.createHorizontalLayout(JustifyContentMode.CENTER ,closeButton);

        playerDetailsDialog.add(formLayout, buttonLayout);

        playerDetailsDialog.open();
    }

    @Override
    public void showEditPlayer(Player player)
    {
        Dialog playerEditDialog = new Dialog();
        playerEditDialog.setWidth("600px");

        TextField nameField = new TextField();
        nameField.setValue(player.getPlayerName() != null ? player.getPlayerName() : "");
        nameField.setWidth("300px");

        TextField emailField = new TextField();
        emailField.setValue(player.getEmail() != null ? player.getEmail() : "");
        emailField.setWidth("300px");

        IntegerField ageField = new IntegerField();
        ageField.setValue(player.getAge() != null ? player.getAge() : 0);
        ageField.setWidth("300px");

        TextField playingHandField = new TextField();
        playingHandField.setValue(player.getPlayingHand() != null ? player.getPlayingHand() : "");
        playingHandField.setWidth("300px");

        IntegerField ratingField = new IntegerField();
        ratingField.setValue(player.getRating() != null ? player.getRating() : 0);
        ratingField.setWidth("300px");

        IntegerField wonMatchesField = new IntegerField();
        wonMatchesField.setValue(player.getWinnedMatches() != null ? player.getWinnedMatches() : 0);
        wonMatchesField.setWidth("300px");

        IntegerField lostMatchesField = new IntegerField();
        lostMatchesField.setValue(player.getLosedMatches() != null ? player.getLosedMatches() : 0);
        lostMatchesField.setWidth("300px");

        IntegerField goalsScoredField = new IntegerField();
        goalsScoredField.setValue(player.getGoalsScored() != null ? player.getGoalsScored() : 0);
        goalsScoredField.setWidth("300px");

        IntegerField goalsLostField = new IntegerField();
        goalsLostField.setValue(player.getGoalsLosed() != null ? player.getGoalsLosed() : 0);
        goalsLostField.setWidth("300px");

        TextField createdAtField = new TextField();
        createdAtField.setValue(player.getCreatedAt() != null ? player.getCreatedAt().toString() : "N/A");
        createdAtField.setReadOnly(true);
        createdAtField.setWidth("300px");

        FormLayout formLayout = new FormLayout();
        formLayout.addFormItem(nameField, "Name");
        formLayout.addFormItem(emailField, "Email");
        formLayout.addFormItem(ageField, "Age");
        formLayout.addFormItem(playingHandField, "Playing Hand");
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
            player.setPlayerName(nameField.getValue());
            player.setEmail(emailField.getValue());
            player.setAge(ageField.getValue());
            player.setPlayingHand(playingHandField.getValue());
            player.setRating(ratingField.getValue());
            player.setWinnedMatches(wonMatchesField.getValue());
            player.setLosedMatches(lostMatchesField.getValue());
            player.setGoalsScored(goalsScoredField.getValue());
            player.setGoalsLosed(goalsLostField.getValue());

            try {
                playerService.save(player);
                showAllPlayers();
//                refreshGridData();

                playerEditDialog.close();

                Notification.show("Player updated successfully: " + player.getPlayerName());
            }
            catch (Exception e)
            {
                Notification.show("Player cannot be updated : " + e.getMessage());
            }
        });

        Button cancelButton = ViewUtils.createButton("Cancel", "button", () -> playerEditDialog.close());

        playerEditDialog.add(formLayout, ViewUtils.createHorizontalLayout(JustifyContentMode.CENTER, saveButton, cancelButton));
        playerEditDialog.open();
    }

    @Override
    public void showDeletePlayer(Player player)
    {
        Dialog playerDeleteDialog = new Dialog();
        playerDeleteDialog.setWidth("500px");
        playerDeleteDialog.setHeaderTitle("Confirm Delete");

        Span confirmationText = new Span("Are you sure you want to delete " + player.getPlayerName() + "?");
        confirmationText.getStyle().set("margin", "10px 0");

        Button deleteButton = ViewUtils.createButton("Delete", "colored-button", () ->
        {
            try {
                playerService.deleteById(player.getId());
                Notification.show("Player " + player.getPlayerName() + " deleted!");
                playerDeleteDialog.close();
//                refreshGridData();
                showAllPlayers();
            }
            catch (Exception e)
            {
                Notification.show("Player cannot be deleted : " + e.getMessage());
            }
        });

        Button cancelButton = ViewUtils.createButton("Cancel", "button", () -> playerDeleteDialog.close());

        HorizontalLayout buttonLayout = ViewUtils.createHorizontalLayout(JustifyContentMode.CENTER, deleteButton, cancelButton);

        playerDeleteDialog.add(confirmationText, buttonLayout);
        playerDeleteDialog.open();
    }

//    private void refreshGridData()
//    {
//        playersGrid.setItems(playerService.getAll().collect(Collectors.toSet()));
//    }


}
