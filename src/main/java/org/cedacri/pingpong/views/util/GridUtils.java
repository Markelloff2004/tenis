package org.cedacri.pingpong.views.util;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.utils.ViewUtils;

import java.util.Set;

public class GridUtils {

    public static void configurePlayerGridWithActionButtons(Grid<Player> grid, Set<Player> source, Set<Player> target, String buttonLabel, Runnable refreshAction) {
        configurePlayerGrid(grid, source);
        grid.addColumn(createPlayerActionColumn(source, target, buttonLabel, refreshAction))
                .setHeader("Action")
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END)
                .setAutoWidth(true)
                .setResizable(false);
    }

    private static ComponentRenderer<Button, Player> createPlayerActionColumn(Set<Player> source, Set<Player> target, String buttonLabel, Runnable refreshAction) {
        return new ComponentRenderer<>(player -> ViewUtils.createButton(buttonLabel, "compact-button", () ->
        {
            source.remove(player);
            target.add(player);
            refreshAction.run();
        }));
    }

     public static void configureGrid(Grid grid) {
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setWidth("45%");
    }

    public static void configurePlayerGrid(Grid<Player> grid, Set<Player> source) {
        grid.addColumn(Player::getRating).setHeader("Rating").setSortable(true);
        grid.addColumn(Player::getName).setHeader("Name").setSortable(true);
        grid.addColumn(Player::getSurname).setHeader("Surname").setSortable(true);
        grid.setItems(source);
    }
}
