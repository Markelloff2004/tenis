package org.cedacri.pingpong.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.RouterLink;
import org.cedacri.pingpong.views.players.PlayersView;
import org.cedacri.pingpong.views.tournament.TournamentView;


public class MainLayout extends AppLayout {
    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        // Header
        H1 appName = new H1("TOURNAMENT APP");
        appName.getStyle().set("margin", "0");
        // A placeholder search (not functional)
        TextField search = new TextField();
        search.setPlaceholder("Search...");
        search.setWidth("200px");
        HorizontalLayout header = new HorizontalLayout(appName, search);
        header.setPadding(true);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.expand(appName);
        addToNavbar(header);
        // Drawer
        Image avatar = new Image("images/user-avatar.png", "User avatar");
        avatar.setWidth("40px");
        Span userName = new Span("Nuca Ion");
        Span role = new Span("Admin");
        VerticalLayout userSection = new VerticalLayout(avatar, userName, role);
        userSection.setSpacing(false);
        userSection.setPadding(false);
        userSection.setAlignItems(FlexComponent.Alignment.CENTER);
        RouterLink homeLink = new RouterLink("Home", MainView.class);
        RouterLink tournamentLink = new RouterLink("Turneu", TournamentView.class);
        RouterLink playersLink = new RouterLink("Jucatori", PlayersView.class);
        VerticalLayout drawerLayout = new VerticalLayout(userSection, homeLink, tournamentLink, playersLink);
        drawerLayout.setSpacing(true);
        drawerLayout.setPadding(true);
        drawerLayout.setSizeFull();
        addToDrawer(drawerLayout);
    }
}