package com.oakenscience.todoapp.listeners;

import com.oakenscience.todoapp.models.User;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@SuppressWarnings("serial")
public class RegistrationCompleteEvent extends ApplicationEvent {

    private Locale locale;
    private final User user;

    public RegistrationCompleteEvent(final User user, final Locale locale) {
        super(user);
        this.user = user;
        this.locale = locale;
    }

    public RegistrationCompleteEvent(User user) {
        super(user);
        this.user = user;
    }

    public Locale getLocale() {
        return locale;
    }

    public User getUser() {
        return user;
    }

}
