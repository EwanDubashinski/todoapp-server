package com.oakenscience.todoapp.config;

import com.oakenscience.todoapp.models.DbUser;
import org.bson.conversions.Bson;

public interface IAuthenticationFacade {
    DbUser getCurrentUser();

    Long getCurrentUserId();
    Bson forCurrentUser(Bson query);
    Bson forCurrentUser();
}
