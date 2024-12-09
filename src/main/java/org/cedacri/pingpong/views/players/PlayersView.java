package org.cedacri.pingpong.views.players;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.views.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;


@PageTitle("Jucatori")
@Route(value = "jucatori", layout = MainLayout.class)
public class PlayersView extends VerticalLayout {

    private final PlayerService playerService;
    private Grid<Player> grid = new Grid<>(Player.class, false);
    private Button addButton = new Button("Adauga Jucator", new Icon(VaadinIcon.PLUS));

    public PlayersView(PlayerService playerService) {
        this.playerService = playerService;
        setSizeFull();

        H3 title = new H3("Lista de Jucatori");
        title.getStyle().set("margin-top", "0");

        configureGrid();
        updateGrid();

        addButton.addClickListener(e -> openPlayerDialog(new Player()));

        VerticalLayout content = new VerticalLayout(title, addButton, grid);
        content.setSpacing(true);
        content.setPadding(true);
        content.setSizeFull();

        add(content);
    }

    private void configureGrid() {
        grid.addColumn(Player::getName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(player -> "#" + player.getRank()).setHeader("Rank").setAutoWidth(true);
        grid.addColumn(Player::getAge).setHeader("Age").setAutoWidth(true);
        grid.addColumn(Player::getStyle).setHeader("Style").setAutoWidth(true);

        grid.addComponentColumn(player -> {
            Button edit = new Button(new Icon(VaadinIcon.EDIT));
            edit.addClickListener(e -> openPlayerDialog(player));

            Button delete = new Button(new Icon(VaadinIcon.TRASH));
            delete.addClickListener(e -> {
                playerService.deletePlayer(player);
                updateGrid();
            });

            VerticalLayout actions = new VerticalLayout(edit, delete);
            actions.setSpacing(false);
            return actions;
        }).setHeader("").setAutoWidth(true);
    }

    private void updateGrid() {
        grid.setItems(playerService.getAllPlayers());
    }

    private void openPlayerDialog(Player player) {
        Dialog dialog = new Dialog();
        PlayerForm form = new PlayerForm();
        form.setPlayer(player);

        form.save.addClickListener(e -> {
            playerService.createPlayer(form.getPlayer());
            updateGrid();
            dialog.close();
        });

        form.cancel.addClickListener(e -> dialog.close());

        dialog.add(form);
        dialog.open();
    }
}