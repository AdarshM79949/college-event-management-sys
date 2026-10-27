package com.college.eventmanagement;

import com.college.eventmanagement.repository.EventRepository;
import com.college.eventmanagement.repository.RegistrationRepository;
import com.college.eventmanagement.repository.UserRepository;
import com.college.eventmanagement.repository.VenueRepository;
import com.college.eventmanagement.service.EventService;
import com.college.eventmanagement.service.RegistrationService;
import com.college.eventmanagement.swing.CampusPulseDesktopApp;
import com.formdev.flatlaf.FlatDarkLaf;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;

/**
 * Main Spring Boot application and Swing desktop launcher for CampusPulse.
 */
@SpringBootApplication
public class EventManagementApplication {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        try {
            FlatDarkLaf.setup();
        } catch (Exception ignored) {}

        new SpringApplicationBuilder(EventManagementApplication.class)
                .web(WebApplicationType.NONE)
                .headless(false)
                .run(args);
    }

    @Bean
    @ConditionalOnExpression("!T(java.awt.GraphicsEnvironment).isHeadless()")
    public CampusPulseDesktopApp campusPulseDesktopApp(
            UserRepository userRepository,
            VenueRepository venueRepository,
            EventRepository eventRepository,
            RegistrationRepository registrationRepository,
            EventService eventService,
            RegistrationService registrationService,
            PasswordEncoder passwordEncoder) {
        return new CampusPulseDesktopApp(
                userRepository,
                eventRepository,
                registrationRepository,
                venueRepository,
                eventService,
                registrationService,
                passwordEncoder
        );
    }

    @Bean
    public ApplicationRunner guiRunner(ObjectProvider<CampusPulseDesktopApp> desktopAppProvider) {
        return args -> {
            CampusPulseDesktopApp desktopApp = desktopAppProvider.getIfAvailable();
            if (desktopApp != null && !GraphicsEnvironment.isHeadless()) {
                SwingUtilities.invokeLater(() -> {
                    desktopApp.setVisible(true);
                    desktopApp.toFront();
                    desktopApp.requestFocus();
                });
            }
        };
    }
}
