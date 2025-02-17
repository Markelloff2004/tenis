package org.cedacri.pingpong.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.server.VaadinSession;
import org.cedacri.pingpong.utils.ViewUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@CssImport("./themes/ping-pong-tournament/main-layout.css")
public class MainLayout extends AppLayout implements AfterNavigationObserver {

    private List<Button> menuButtons;
    private final List<String> routes = Arrays.asList("home", "tournaments", "players");

    public MainLayout() {
        configureView();
    }

    private void configureView() {
        setPrimarySection(Section.DRAWER);

        H4 appName = new H4("TOURNAMENT");
        Image logo = new Image("images/logo.png", "Tournament Logo");
        logo.setWidth("160px");
        logo.setHeight("auto");

        VerticalLayout logoLayout = ViewUtils.createVerticalLayout(FlexComponent.JustifyContentMode.START, logo, appName);
        logoLayout.addClassName("logo-section");

        menuButtons = Arrays.asList(
                ViewUtils.createButton("Home", "transparent-button", () -> navigateTo("home")),
                ViewUtils.createButton("Tournament", "transparent-button", () -> navigateTo("tournaments")),
                ViewUtils.createButton("Players", "transparent-button", () -> navigateTo("players"))
        );

        VerticalLayout menuItemsLayout = ViewUtils.createVerticalLayout(FlexComponent.JustifyContentMode.START, menuButtons.toArray(new Button[0]));
        menuItemsLayout.addClassName("menu-items");

        // User Authentication Section
        HorizontalLayout userSection = createUserSection();

        VerticalLayout drawerLayout = ViewUtils.createVerticalLayout(FlexComponent.JustifyContentMode.START, logoLayout, menuItemsLayout, userSection);
        drawerLayout.addClassName("side-menu");

        addToDrawer(drawerLayout);
    }

    private HorizontalLayout createUserSection() {
        HorizontalLayout userLayout = new HorizontalLayout();
        userLayout.setWidthFull();
        userLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        userLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            // If user is authenticated, show greeting and logout button
            String username = auth.getName();
            H4 greeting = new H4("Hi, " + username);
            greeting.addClassName("user-greeting");

            Button logoutButton = new Button("Logout", e -> logout());
            logoutButton.addClassName("logout-button");

            userLayout.add(greeting, logoutButton);
        } else {
            // If user is not authenticated, show login button
            Button loginButton = new Button("Login", e -> navigateTo("login"));
            loginButton.addClassName("login-button");

            userLayout.add(loginButton);
        }

        return userLayout;
    }

    private void logout() {
        VaadinSession.getCurrent().getSession().invalidate();
        UI.getCurrent().getPage().setLocation("/login"); // Redirect to login page
    }

    private void navigateTo(String route) {
        UI.getCurrent().navigate(route);
    }

    private void highlightActiveMenuItem(String route) {
        int selectedIndex = routes.indexOf(route);
        ViewUtils.highlightSelectedComponentFromComponentsList(menuButtons, selectedIndex, "selected");
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        highlightActiveMenuItem(getCurrentRoute());
    }

    private String getCurrentRoute() {
        return Optional.ofNullable(
                        UI.getCurrent()
                                .getInternals()
                                .getActiveViewLocation()
                                .getPath())
                .orElse("home");
    }
}
