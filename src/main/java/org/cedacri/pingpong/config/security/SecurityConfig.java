package org.cedacri.pingpong.config.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.cedacri.pingpong.config.security.model.enums.RoleEnum;
import org.cedacri.pingpong.views.loginview.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/home", "/login").permitAll()
                        .requestMatchers("/tournaments", "/tournament/**", "/tournament/matches", "/players")
                        .hasAnyRole(RoleEnum.ADMIN.name(), RoleEnum.MANAGER.name())
                        .requestMatchers("/VAADIN/**", "/frontend/**", "/images/**", "/line-awesome/**", "/styles/**").permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                )
                .logout(AbstractHttpConfigurer::disable);

        setLoginView(http, LoginView.class);
        super.configure(http);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
