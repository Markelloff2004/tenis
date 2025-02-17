package org.cedacri.pingpong.views.loginview;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route("login")
@PageTitle("Login | Ping Pong")
@AnonymousAllowed
@PreserveOnRefresh
public class LoginView extends VerticalLayout {

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        addClassName("login-view");

        H1 title = new H1("🏓 Ping Pong Tournament");
        title.addClassName("login-title");

        LoginForm loginForm = new LoginForm();
        loginForm.setI18n(createLoginI18n());
        loginForm.setAction("login");
        loginForm.addClassName("custom-login-form");

        loginForm.addLoginListener(event -> loginForm.setError(true));

        VerticalLayout container = new VerticalLayout(title, loginForm);
        container.setAlignItems(Alignment.CENTER);
        container.addClassName("login-container");

        add(container);
    }

    private LoginI18n createLoginI18n() {
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setTitle("Login");
        i18n.getForm().setUsername("Username");
        i18n.getForm().setPassword("Password");
        i18n.getForm().setSubmit("Sign in");
        i18n.getErrorMessage().setMessage("Invalid username or password.");
        return i18n;
    }
}
