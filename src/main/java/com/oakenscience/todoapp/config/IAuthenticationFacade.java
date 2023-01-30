package com.oakenscience.todoapp.config;

import com.oakenscience.todoapp.models.DbUser;

public interface IAuthenticationFacade {
    DbUser getCurrentUser();
}
