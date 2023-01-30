package com.oakenscience.todoapp.listeners;
import com.oakenscience.todoapp.models.DbUser;
import com.oakenscience.todoapp.service.IUserService;
import com.oakenscience.todoapp.service.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Component
public class RegistrationListener implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    IUserService userService;

    @Autowired
    MailSender mailSender;

    private final String hostname;

    public RegistrationListener(@Value("${server.host}") String host, @Value("${server.port}") String port) {
        this.hostname = String.format("%s:%s", host, port);
    }

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(final RegistrationCompleteEvent event) {
        final DbUser dbUser = event.getUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(dbUser, token);

        mailSender.send(dbUser.getEmail(),
                "Todoapp registration",
                String.format("%s/activation/%s", hostname, token));
    }
}
