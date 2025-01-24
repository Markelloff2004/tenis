package org.cedacri.pingpong.views.playersview.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.utils.ViewUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerInfoDialog extends Dialog {

    private static final Logger logger = LoggerFactory.getLogger(PlayerInfoDialog.class);

    public PlayerInfoDialog(Player player) {
        logger.info("Showing details player: {} {}:{}", player.getName(), player.getSurname(), player.getId());

        setWidth("400px");
        setHeight("auto");
        setHeaderTitle("Player Details");

        FormLayout formLayout = new FormLayout();
        formLayout.getStyle().set("gap", "5px");

        formLayout.addFormItem(new Span(player.getName()), "Name");
        formLayout.addFormItem(new Span(player.getSurname()), "Surname");
        formLayout.addFormItem(new Span(player.getEmail()), "Email");
        formLayout.addFormItem(new Span(player.getBirthDate() != null ? player.getBirthDate().toString() : "N/A"), "Age");
        formLayout.addFormItem(new Span(player.getAddress()), "Address");
        formLayout.addFormItem(new Span(player.getHand()), "Playing Hand");
        formLayout.addFormItem(new Span(player.getRating() != null ? player.getRating().toString() : "N/A"), "Rating");
        formLayout.addFormItem(new Span(player.getWonMatches() != null ? player.getWonMatches().toString() : "N/A"), "Won Matches");
        formLayout.addFormItem(new Span(player.getLostMatches() != null ? player.getLostMatches().toString() : "N/A"), "Lost Matches");
        formLayout.addFormItem(new Span(player.getGoalsScored() != null ? player.getGoalsScored().toString() : "N/A"), "Goals Scored");
        formLayout.addFormItem(new Span(player.getGoalsLost() != null ? player.getGoalsLost().toString() : "N/A"), "Goals Lost");
        formLayout.addFormItem(new Span(player.getCreatedAt() != null ? player.getCreatedAt().toString() : "N/A"), "Created At");

        Button closeButton = ViewUtils.createButton("Cancel", "button", () -> {
            logger.info("Close button clicked. Closing PlayerInfoDialog.");
            close();
        } );

        HorizontalLayout buttonLayout = ViewUtils.createHorizontalLayout(JustifyContentMode.CENTER, closeButton);

        add(formLayout, buttonLayout);
    }
}
