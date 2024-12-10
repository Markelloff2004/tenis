package org.cedacri.pingpong.views.playersview;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

    private final Button addPlayerButton;
    private final Grid<Player> playersGrid;
    private final PlayerService playerService;

    public PlayersView(PlayerService playerService) {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H1 title = new H1("Players list");
        title.addClassName("players-title");

        addPlayerButton = new Button("Add player");
        addPlayerButton.addClassName("colored-button");
        addPlayerButton.addClickListener(e -> {
            System.out.println("Add player button clicked");
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(addPlayerButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);

        playersGrid = new Grid<>(Player.class, false);
        playersGrid.addClassName("players-grid");
        playersGrid.setSizeFull();
        configureGrid();

        add(title, buttonLayout, playersGrid);
        this.playerService = playerService;

        refreshGridData();
    }

    private void configureGrid()
    {
        playersGrid.addColumn(Player::getName).setHeader("Name").setSortable(true);
        playersGrid.addColumn(Player::getAge).setHeader("Age").setSortable(true);
        playersGrid.addColumn(Player::getRating).setHeader("Rating").setSortable(true);
        playersGrid.addColumn(Player::getPlayingHand).setHeader("Playing Style").setSortable(true);
        playersGrid.addColumn(Player::getWinnedMatches).setHeader("Won Matches").setSortable(true);
        playersGrid.addColumn(Player::getLosedMatches).setHeader("Losed Matches").setSortable(true);
        playersGrid.addColumn(Player::getGoalsScored).setHeader("Goals Scored").setSortable(true);
        playersGrid.addColumn(Player::getEmail).setHeader("Email").setSortable(true);
        playersGrid.addColumn(Player::getCreatedAt).setHeader("Created at").setSortable(true);
    }

    private void refreshGridData() {
//        playersGrid.getDataProvider().refreshAll();
        // playerService.getAllPlayers()
        playersGrid.setItems(getDemoPlayers());
    }

    // for test only
    private List<Player> getDemoPlayers() {
        return Arrays.asList(
                new Player("John Doe", 25, "john.doe@example.com", Instant.parse("2023-01-01T10:00:00Z"), 1200, "Right-handed", 20, 5, 50, 30),
                new Player("Jane Smith", 30, "jane.smith@example.com", Instant.parse("2023-02-01T12:00:00Z"), 1400, "Left-handed", 25, 3, 60, 25),
                new Player("Mike Brown", 27, "mike.brown@example.com", Instant.parse("2023-03-01T14:00:00Z"), 1100, "Ambidextrous", 15, 10, 45, 35)
        );
    }
}
