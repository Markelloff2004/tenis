package org.cedacri.pingpong.views.loginview;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Login | Ping Pong")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    public LoginView() {
        LoginForm loginForm = new LoginForm();
        loginForm.setI18n(createLoginI18n());
        loginForm.setAction("login"); // This triggers Spring Security authentication

        loginForm.addLoginListener(event -> loginForm.setError(true));

        add(new H1("Ping Pong Tournament"), loginForm);
    }

    private LoginI18n createLoginI18n() {
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setTitle("Login");
        i18n.getForm().setUsername("Username");
        i18n.getForm().setPassword("Password");
        i18n.getForm().setSubmit("Sign in");
        i18n.getErrorMessage().setMessage("Invalid credentials");
        return i18n;
    }
}
