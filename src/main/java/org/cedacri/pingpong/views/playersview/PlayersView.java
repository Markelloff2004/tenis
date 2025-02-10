package org.cedacri.pingpong.views.playersview;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.utils.ViewUtils;
import org.cedacri.pingpong.views.MainLayout;
import org.cedacri.pingpong.views.interfaces.PlayerViewManagement;
import org.cedacri.pingpong.views.playersview.components.PlayerDeleteDialog;
import org.cedacri.pingpong.views.playersview.components.PlayerEditDialog;
import org.cedacri.pingpong.views.playersview.components.PlayerInfoDialog;
import org.cedacri.pingpong.views.playersview.components.PlayerSaveDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@PageTitle("PlayersView")
@Route(value = "players", layout = MainLayout.class)
@CssImport("./themes/ping-pong-tournament/main-layout.css")
@Uses(Icon.class)
public class PlayersView extends VerticalLayout implements PlayerViewManagement
{

    private static final Logger log = LoggerFactory.getLogger(PlayersView.class);

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

        log.info("PlayersView initialized");

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
        playersGrid.addColumn(Player::getSurname).setHeader("Surname").setSortable(true).setKey("playerSurname");
        playersGrid.addColumn(player ->ChronoUnit.YEARS.between(player.getBirthDate(), LocalDate.now())).setHeader("Age").setSortable(true).setKey("age");

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
        log.info("Loading all players");
        playersGrid.setItems(playerService.getAll().collect(Collectors.toSet()));
    }

    @Override
    public void showCreatePlayer()
    {
        log.info("Creating new player");
        PlayerSaveDialog playerSaveDialog = new PlayerSaveDialog(playerService, this::showAllPlayers);
        playerSaveDialog.open();
    }

    @Override
    public void showDetailsPlayer(Player player)
    {
        log.info("Loading player details for {} {}", player.getName(), player.getSurname());
        PlayerInfoDialog playerInfoDialog = new PlayerInfoDialog(player, playerService, this::showAllPlayers);
        playerInfoDialog.open();
    }

    @Override
    public void showEditPlayer(Player player)
    {
        log.info("Editing player: {} with Id: {}", player.getName() + " " + player.getSurname(), player.getId());
        Dialog playerEditDialog = new PlayerEditDialog(player, playerService, this::showAllPlayers);
        playerEditDialog.open();
    }

    @Override
    public void showDeletePlayer(Player player)
    {
        log.info("Deleting player: {} with Id: {}", player.getName(), player.getId());
        Dialog playerDeleteDialog = new PlayerDeleteDialog(player, playerService, this::showAllPlayers);
        playerDeleteDialog.open();
    }
}
