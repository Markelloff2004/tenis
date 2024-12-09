package org.cedacri.pingpong.views.players;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import org.cedacri.pingpong.entity.Player;


public class PlayerForm extends FormLayout {
    TextField name = new TextField("Name");
    IntegerField rank = new IntegerField("Rank");
    IntegerField age = new IntegerField("Age");
    TextField style = new TextField("Style");
    Button save = new Button("Save");
    Button cancel = new Button("Cancel");
    private Player player;
    public PlayerForm() {
        add(name, rank, age, style, save, cancel);
        rank.setMin(1);
        age.setMin(1);
    }
    public void setPlayer(Player player) {
        this.player = player;
        if (player != null) {
            name.setValue(player.getName() != null ? player.getName() : "");
            rank.setValue(player.getRank() != null ? player.getRank() : 1);
            age.setValue(player.getAge() != null ? player.getAge() : 18);
            style.setValue(player.getStyle() != null ? player.getStyle() : "");
        } else {
            name.clear();
            rank.clear();
            age.clear();
            style.clear();
        }
    }
    public Player getPlayer() {
        player.setName(name.getValue());
        player.setRank(rank.getValue());
        player.setAge(age.getValue());
        player.setStyle(style.getValue());
        return player;
    }
}