package com.oakenscience.todoapp.listeners;
import com.oakenscience.todoapp.models.User;
import com.oakenscience.todoapp.service.IUserService;
import com.oakenscience.todoapp.service.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Component
public class RegistrationListener implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    IUserService userService;

    @Autowired
    MailSender mailSender;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(final RegistrationCompleteEvent event) {
        final User user = event.getUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);

        mailSender.send(user.getEmail(), "Todoapp registration", token);
    }
}
