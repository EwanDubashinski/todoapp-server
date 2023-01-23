package com.oakenscience.todoapp.service;

import com.oakenscience.todoapp.dto.UserDto;
import com.oakenscience.todoapp.models.User;

public interface IUserService {

    User registerNewUserAccount(UserDto accountDto);

    void createVerificationTokenForUser(User user, String token);
}