package com.oakenscience.todoapp.listeners;

import com.oakenscience.todoapp.models.DbUser;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@SuppressWarnings("serial")
public class RegistrationCompleteEvent extends ApplicationEvent {

    private Locale locale;
    private final DbUser dbUser;

    public RegistrationCompleteEvent(final DbUser dbUser, final Locale locale) {
        super(dbUser);
        this.dbUser = dbUser;
        this.locale = locale;
    }

    public RegistrationCompleteEvent(DbUser dbUser) {
        super(dbUser);
        this.dbUser = dbUser;
    }

    public Locale getLocale() {
        return locale;
    }

    public DbUser getUser() {
        return dbUser;
    }

}
