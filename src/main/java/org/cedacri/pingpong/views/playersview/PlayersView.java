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
//            System.out.println("New player button clicked");
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
        dialog.setWidth("300px"); // Make the dialog smaller
        dialog.setHeight("auto"); // Adjust height automatically based on content

        // Create form fields
        TextField nameField = new TextField();
        IntegerField ageField = new IntegerField();
        TextField emailField = new TextField();
        TextField playingHandField = new TextField();

        // Validation
        nameField.setRequired(true);
        ageField.setMin(0);

        // Create form layout with labels on top and reduced spacing
        FormLayout formLayout = new FormLayout();
        formLayout.addFormItem(nameField, "Name").getStyle().set("flex-direction", "column").set("margin-bottom", "5px");
        formLayout.addFormItem(ageField, "Age").getStyle().set("flex-direction", "column").set("margin-bottom", "5px");
        formLayout.addFormItem(emailField, "Email").getStyle().set("flex-direction", "column").set("margin-bottom", "5px");
        formLayout.addFormItem(playingHandField, "Playing Hand").getStyle().set("flex-direction", "column").set("margin-bottom", "5px");

        // Resize fields for compactness
        String fieldWidth = "100%"; // Ensure fields take full available width within the form
        nameField.setWidth(fieldWidth);
        ageField.setWidth(fieldWidth);
        emailField.setWidth(fieldWidth);
        playingHandField.setWidth(fieldWidth);

        // Create Save and Cancel buttons
        Button saveButton = new Button("Save", event -> {
            // Collect data and create a new Player object
            String name = nameField.getValue();
            Integer age = ageField.getValue();
            String email = emailField.getValue();
            String playingHand = playingHandField.getValue();

            // Validate fields
            if (name.isBlank() || name.isEmpty()) {
                Notification.show("Please fill in all required fields.");
                return;
            }

            // Create the Player object (save logic would go here)
            Player newPlayer = new Player(name, age, email, Instant.now(), 0, playingHand, 0, 0, 0, 0);

            // Close the dialog
            dialog.close();

            // Notify success
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

        // Open the dialog
        dialog.open();
    }

    private void configureGrid()
    {
        playersGrid.addColumn(Player::getPlayerName).setHeader("Name").setSortable(true);
        playersGrid.addColumn(Player::getAge).setHeader("Age").setSortable(true);
        playersGrid.addColumn(Player::getRating).setHeader("Rating").setSortable(true);
        playersGrid.addColumn(Player::getPlayingHand).setHeader("Playing Style").setSortable(true);
        playersGrid.addColumn(Player::getWinnedMatches).setHeader("Won Matches").setSortable(true);
        playersGrid.addColumn(Player::getLosedMatches).setHeader("Losed Matches").setSortable(true);
        playersGrid.addColumn(Player::getGoalsScored).setHeader("Goals Scored").setSortable(true);
        playersGrid.addColumn(Player::getEmail).setHeader("Email").setSortable(true);
        playersGrid.addColumn(Player::getCreatedAt).setHeader("Created at").setSortable(true);

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

//    private void openDetailsPlayerDialog1(Player player) {
//        Dialog dialog = new Dialog();
//        dialog.setHeaderTitle("View Player");
//
//        // Display player data in labels
//        FormLayout formLayout = new FormLayout();
//
//        formLayout.addFormItem(new Label(player.getName()), "Name");
//        formLayout.addFormItem(new Label(player.getAge() != null ? player.getAge().toString() : "N/A"), "Age");
//        formLayout.addFormItem(new Label(player.getEmail()), "Email");
//        formLayout.addFormItem(new Label(player.getCreatedAt().toString()), "Created At");
//        formLayout.addFormItem(new Label(player.getRating() != null ? player.getRating().toString() : "N/A"), "Rating");
//        formLayout.addFormItem(new Label(player.getPlayingHand()), "Playing Hand");
//        formLayout.addFormItem(new Label(player.getWinnedMatches() != null ? player.getWinnedMatches().toString() : "N/A"), "Won Matches");
//        formLayout.addFormItem(new Label(player.getLosedMatches() != null ? player.getLosedMatches().toString() : "N/A"), "Lost Matches");
//        formLayout.addFormItem(new Label(player.getGoalsScored() != null ? player.getGoalsScored().toString() : "N/A"), "Goals Scored");
//        formLayout.addFormItem(new Label(player.getGoalsLosed() != null ? player.getGoalsLosed().toString() : "N/A"), "Goals Lost");
//
//        // Close button
//        Button closeButton = new Button("Close", event -> dialog.close());
//        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//
//        // Add components to the dialog
//        dialog.add(formLayout, closeButton);
//
//        dialog.open();
//    }

    private void openDetailsPlayerDialog(Player player) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px"); // Adjust dialog width to make it more compact

        // FormLayout to display player details
        FormLayout formLayout = new FormLayout();
        formLayout.getStyle().set("gap", "5px"); // Reduce the gap between rows and columns

        // Add player details
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

        // Adjust labels and text size
        formLayout.getChildren().forEach(child -> {
            if (child instanceof Label) {
                ((Label) child).getStyle()
                        .set("font-size", "12px") // Make text smaller
                        .set("line-height", "16px"); // Adjust line spacing for compactness
            }
        });

        // Add Close button
        Button closeButton = new Button("Close", e -> dialog.close());
        closeButton.addClassName("button"); // Add your custom class for styling
        closeButton.getStyle().set("width", "100px"); // Compact button width

        // Layout for button
        HorizontalLayout buttonLayout = new HorizontalLayout(closeButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttonLayout.getStyle().set("margin-top", "10px"); // Reduced space above the button

        // Add everything to dialog
        dialog.add(formLayout, buttonLayout);

        // Open the dialog
        dialog.open();
    }

    private void openEditPlayerDialog(Player player) {
        Dialog dialog = new Dialog();
        dialog.setWidth("600px"); // Adjust dialog width to fit two columns

        // Editable fields to display and edit player details
        TextField nameField = new TextField();
        nameField.setValue(player.getPlayerName() != null ? player.getPlayerName() : "");
        nameField.setWidth("300px"); // Set field width

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
        createdAtField.setReadOnly(true); // Prevent editing for createdAt field
        createdAtField.setWidth("300px");

        // FormLayout for two-column layout
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

        // Set responsive steps for two columns
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1), // One column for small screens
                new FormLayout.ResponsiveStep("600px", 2) // Two columns for larger screens
        );

        // Save button to apply changes
        Button saveButton = new Button("Save", event -> {
            // Update player object with new values
            player.setPlayerName(nameField.getValue());
            player.setEmail(emailField.getValue());
            player.setAge(ageField.getValue());
            player.setPlayingHand(playingHandField.getValue());
            player.setRating(ratingField.getValue());
            player.setWinnedMatches(wonMatchesField.getValue());
            player.setLosedMatches(lostMatchesField.getValue());
            player.setGoalsScored(goalsScoredField.getValue());
            player.setGoalsLosed(goalsLostField.getValue());

            // Close the dialog
            dialog.close();

            // Notify success
            Notification.show("Player updated successfully: " + player.getPlayerName());
        });
        saveButton.addClassName("colored-button"); // Add your custom class for styling


        // Cancel button to discard changes
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        cancelButton.addClassName("button"); // Add your custom class for styling


        // Layout for buttons
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttonLayout.getStyle().set("margin-top", "10px"); // Reduced space above the buttons

        // Add everything to dialog
        dialog.add(formLayout, buttonLayout);

        // Open the dialog
        dialog.open();
    }

    private void openDeletePlayerDialog(Player player) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px"); // Adjust the dialog width
        dialog.setHeaderTitle("Confirm Delete");

        // Confirmation text
        Label confirmationText = new Label("Are you sure you want to delete " + player.getPlayerName() + "?");
        confirmationText.getStyle().set("margin", "10px 0");

        // Confirm Delete Button
        Button deleteButton = new Button("Delete", event -> {
            playerService.deleteById(player.getId());
            Notification.show("Player " + player.getPlayerName() + " deleted!");
            dialog.close(); // Close the dialog
            // Uncomment this if you want to refresh the grid after deletion
            // refreshGridData();
        });
        deleteButton.setWidth("100px"); // Set the width to 100px
        deleteButton.addClassName("colored-button"); // Add your custom class for styling

        // Cancel Button
        Button cancelButton = new Button("Cancel", event -> dialog.close());
        cancelButton.setWidth("100px"); // Set the width to 100px
        cancelButton.addClassName("button"); // Add your custom class for styling

        // Horizontal layout for buttons
        HorizontalLayout buttonLayout = new HorizontalLayout(deleteButton, cancelButton);
        buttonLayout.setSpacing(true); // Add spacing between buttons
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END); // Align buttons to the right
        buttonLayout.getStyle().set("margin-top", "50px"); // Move the buttons 50px lower

        // Add components to the dialog
        dialog.add(confirmationText, buttonLayout);

        // Open the dialog
        dialog.open();
    }

    private void refreshGridData() {
        playersGrid.setItems(query -> playerService.list(query.getPage()));
    }

}
