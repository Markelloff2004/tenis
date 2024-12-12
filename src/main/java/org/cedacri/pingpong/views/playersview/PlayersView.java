package org.cedacri.pingpong.views.playersview;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
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
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.views.MainLayout;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@PageTitle("PlayersView")
@Route(value = "players", layout = MainLayout.class)
@CssImport("./themes/ping-pong-tournament/main-layout.css")
@Uses(Icon.class)
public class PlayersView extends VerticalLayout {

    private final PlayerService playerService;
    private final Button addPlayerButton;
    private final Grid<Player> playersGrid;

    public PlayersView(PlayerService playerService) {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        HorizontalLayout pageHeader = new HorizontalLayout();
        pageHeader.setWidthFull();

        H1 title = new H1("Players list");
        title.addClassName("players-title");

        addPlayerButton = new Button("New player");
        addPlayerButton.addClassName("colored-button");
        addPlayerButton.addClickListener(e -> {
            openNewPlayerDialog();
        });

        pageHeader.setJustifyContentMode(JustifyContentMode.BETWEEN);
        pageHeader.add(title, addPlayerButton);

//        HorizontalLayout buttonLayout = new HorizontalLayout(addPlayerButton);
//        buttonLayout.setWidthFull();
//        buttonLayout.setJustifyContentMode(JustifyContentMode.END);

        playersGrid = new Grid<>(Player.class, false);
        playersGrid.addClassName("players-grid");
        playersGrid.setSizeFull();
        configureGrid();

        add(pageHeader, playersGrid);
        this.playerService = playerService;

        refreshGridData();
    }

    private void openNewPlayerDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth("300px");
        dialog.setHeight("auto");

        TextField nameField = new TextField();
        IntegerField ageField = new IntegerField();
        TextField emailField = new TextField();
        TextField playingHandField = new TextField();

        nameField.setRequired(true);
        ageField.setMin(0);

        FormLayout formLayout = new FormLayout();
        formLayout.addFormItem(nameField, "Name").getStyle().set("flex-direction", "column").set("margin-bottom", "5px");
        formLayout.addFormItem(ageField, "Age").getStyle().set("flex-direction", "column").set("margin-bottom", "5px");
        formLayout.addFormItem(emailField, "Email").getStyle().set("flex-direction", "column").set("margin-bottom", "5px");
        formLayout.addFormItem(playingHandField, "Playing Hand").getStyle().set("flex-direction", "column").set("margin-bottom", "5px");

        String fieldWidth = "100%";
        nameField.setWidth(fieldWidth);
        ageField.setWidth(fieldWidth);
        emailField.setWidth(fieldWidth);
        playingHandField.setWidth(fieldWidth);

        Button saveButton = new Button("Save", event -> {
            String name = nameField.getValue();
            Integer age = ageField.getValue();
            String email = emailField.getValue();
            String playingHand = playingHandField.getValue();

            if (name.isBlank() || name.isEmpty()) {
                Notification.show("Please fill in all required fields.");
                return;
            }

            Player newPlayer = new Player(name, age, email, Instant.now(), 0, playingHand, 0, 0, 0, 0);

            playerService.save(newPlayer);

            dialog.close();
            refreshGridData();
            Notification.show("Player added successfully: " + newPlayer.getPlayerName());
        });
        saveButton.setWidth("100px");
        saveButton.addClassName("colored-button");

        Button cancelButton = new Button("Cancel", event -> dialog.close());
        cancelButton.setWidth("100px");
        cancelButton.addClassName("button");


        // Add buttons to a HorizontalLayout and center them
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER); // Center buttons horizontally
        buttonLayout.setWidthFull();
        buttonLayout.getStyle().set("margin-top", "10px"); // Reduced space above buttons

        // Add form layout and buttons to the dialog
        dialog.add(formLayout, buttonLayout);

        dialog.open();
    }

    private void configureGrid()
    {
        playersGrid.addColumn(Player::getRating).setHeader("Rating").setSortable(true).setKey("rating");
        playersGrid.addColumn(Player::getPlayerName).setHeader("Name").setSortable(true).setKey("playerName");
        playersGrid.addColumn(Player::getEmail).setHeader("Email").setSortable(true).setKey("email");
        playersGrid.addColumn(Player::getAge).setHeader("Age").setSortable(true).setKey("age");
        playersGrid.addColumn(Player::getPlayingHand).setHeader("Playing Style").setSortable(true).setKey("playingHand");
        playersGrid.addColumn(Player::getWinnedMatches).setHeader("Won Matches").setSortable(true).setKey("winnedMatches");
        playersGrid.addColumn(Player::getLosedMatches).setHeader("Losed Matches").setSortable(true).setKey("losedMatches");
        playersGrid.addColumn(Player::getGoalsScored).setHeader("Goals Scored").setSortable(true).setKey("goalsScored");
        playersGrid.addColumn(Player::getGoalsLosed).setHeader("Goals Losed").setSortable(true).setKey("goalsLosed");

        playersGrid.addColumn(new ComponentRenderer<>(player -> {
            HorizontalLayout actionsLayout = new HorizontalLayout();

            Button viewButton = new Button("Details", click -> {
                openDetailsPlayerDialog(player);
            });
            viewButton.addClassName("compact-button");

            Button editButton = new Button("Edit", click -> {
                openEditPlayerDialog(player);
            });
            editButton.addClassName("compact-button");

            Button deleteButton = new Button("Delete", click -> {
                openDeletePlayerDialog(player);
            });
            deleteButton.addClassName("compact-button");

            actionsLayout.add(viewButton, editButton, deleteButton);
            return actionsLayout;
        })).setHeader("Actions").setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.CENTER);
    }

    private void openDetailsPlayerDialog(Player player) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");

        FormLayout formLayout = new FormLayout();
        formLayout.getStyle().set("gap", "5px");

        formLayout.addFormItem(new Label(player.getPlayerName()), "Name");
        formLayout.addFormItem(new Label(player.getEmail()), "Email");
        formLayout.addFormItem(new Label(player.getAge() != null ? player.getAge().toString() : "N/A"), "Age");
        formLayout.addFormItem(new Label(player.getPlayingHand()), "Playing Hand");
        formLayout.addFormItem(new Label(player.getRating() != null ? player.getRating().toString() : "N/A"), "Rating");
        formLayout.addFormItem(new Label(player.getWinnedMatches() != null ? player.getWinnedMatches().toString() : "N/A"), "Won Matches");
        formLayout.addFormItem(new Label(player.getLosedMatches() != null ? player.getLosedMatches().toString() : "N/A"), "Lost Matches");
        formLayout.addFormItem(new Label(player.getGoalsScored() != null ? player.getGoalsScored().toString() : "N/A"), "Goals Scored");
        formLayout.addFormItem(new Label(player.getGoalsLosed() != null ? player.getGoalsLosed().toString() : "N/A"), "Goals Lost");
        formLayout.addFormItem(new Label(player.getCreatedAt() != null ? player.getCreatedAt().toString() : "N/A"), "Created At");

        formLayout.getChildren().forEach(child -> {
            if (child instanceof Label) {
                ((Label) child).getStyle()
                        .set("font-size", "12px")
                        .set("line-height", "16px");
            }
        });

        Button closeButton = new Button("Close", e -> dialog.close());
        closeButton.addClassName("button");
        closeButton.getStyle().set("width", "100px");

        HorizontalLayout buttonLayout = new HorizontalLayout(closeButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttonLayout.getStyle().set("margin-top", "10px");

        dialog.add(formLayout, buttonLayout);

        dialog.open();
    }

    private void openEditPlayerDialog(Player player) {
        Dialog dialog = new Dialog();
        dialog.setWidth("600px");

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

        Button saveButton = new Button("Save", event -> {
            player.setPlayerName(nameField.getValue());
            player.setEmail(emailField.getValue());
            player.setAge(ageField.getValue());
            player.setPlayingHand(playingHandField.getValue());
            player.setRating(ratingField.getValue());
            player.setWinnedMatches(wonMatchesField.getValue());
            player.setLosedMatches(lostMatchesField.getValue());
            player.setGoalsScored(goalsScoredField.getValue());
            player.setGoalsLosed(goalsLostField.getValue());

            playerService.save(player);
            refreshGridData();

            dialog.close();

            Notification.show("Player updated successfully: " + player.getPlayerName());
        });
        saveButton.addClassName("colored-button");


        Button cancelButton = new Button("Cancel", e -> dialog.close());
        cancelButton.addClassName("button");
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttonLayout.getStyle().set("margin-top", "10px"); // Reduced space above the buttons

        dialog.add(formLayout, buttonLayout);

        dialog.open();
    }

    private void openDeletePlayerDialog(Player player) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");
        dialog.setHeaderTitle("Confirm Delete");

        Label confirmationText = new Label("Are you sure you want to delete " + player.getPlayerName() + "?");
        confirmationText.getStyle().set("margin", "10px 0");

        Button deleteButton = new Button("Delete", event -> {
            playerService.deleteById(player.getId());
            Notification.show("Player " + player.getPlayerName() + " deleted!");
            dialog.close();
            refreshGridData();
        });
        deleteButton.setWidth("100px");
        deleteButton.addClassName("colored-button");

        Button cancelButton = new Button("Cancel", event -> dialog.close());
        cancelButton.setWidth("100px");
        cancelButton.addClassName("button");

        HorizontalLayout buttonLayout = new HorizontalLayout(deleteButton, cancelButton);
        buttonLayout.setSpacing(true);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.getStyle().set("margin-top", "50px");

        dialog.add(confirmationText, buttonLayout);

        dialog.open();
    }

    private void refreshGridData() {
        playersGrid.setItems(query -> playerService.list(query.getPage()));
    }

}
