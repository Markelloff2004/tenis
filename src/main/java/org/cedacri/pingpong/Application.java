package org.cedacri.pingpong;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point of the Spring Boot application.
 * <p>
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 */
@SpringBootApplication
@Theme(value = "ping-pong-tournament", variant = Lumo.LIGHT)
public class Application implements AppShellConfigurator
{

    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }
}
