package com.oakenscience.todoapp.service;

import com.oakenscience.todoapp.dto.UserDto;
import com.oakenscience.todoapp.models.User;
import com.oakenscience.todoapp.models.VerificationToken;
import com.oakenscience.todoapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
//import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService implements IUserService{
    @Autowired
    UserRepository userRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public User registerNewUserAccount(UserDto accountDto) {
//        if (emailExists(accountDto.getEmail())) {
//            throw new UserAlreadyExistException("There is an account with that email address: " + accountDto.getEmail());
//        }
        final User user = new User();

        user.setName(accountDto.getName());
        user.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        user.setEmail(accountDto.getEmail());
        user.setToken(new VerificationToken());
        return userRepository.createNew(user);
    }

    @Override
    public void createVerificationTokenForUser(User user, String token) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        user.setToken(verificationToken);
        userRepository.updateUserToken(user);
    }
}
