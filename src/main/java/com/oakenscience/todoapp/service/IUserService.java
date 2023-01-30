package com.oakenscience.todoapp.service;

import com.oakenscience.todoapp.dto.UserDto;
import com.oakenscience.todoapp.models.DbUser;

public interface IUserService {

    DbUser registerNewUserAccount(UserDto accountDto);

    void createVerificationTokenForUser(DbUser dbUser, String token);

    void activate(String code);
}