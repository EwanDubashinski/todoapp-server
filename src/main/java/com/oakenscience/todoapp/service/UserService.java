package com.oakenscience.todoapp.service;

import com.oakenscience.todoapp.dto.UserDto;
import com.oakenscience.todoapp.error.UserActivationFailed;
import com.oakenscience.todoapp.models.DbUser;
import com.oakenscience.todoapp.models.VerificationToken;
import com.oakenscience.todoapp.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
//import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService implements IUserService{
    Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    UserRepository userRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public DbUser registerNewUserAccount(UserDto accountDto) {
//        if (emailExists(accountDto.getEmail())) {
//            throw new UserAlreadyExistException("There is an account with that email address: " + accountDto.getEmail());
//        }
        final DbUser dbUser = new DbUser();

        dbUser.setName(accountDto.getName());
        dbUser.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        dbUser.setEmail(accountDto.getEmail());
        dbUser.setToken(new VerificationToken());
        dbUser.setActivated(false);
        return userRepository.createNew(dbUser);
    }

    @Override
    public void createVerificationTokenForUser(DbUser dbUser, String token) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        dbUser.setToken(verificationToken);
        userRepository.updateUserToken(dbUser);
    }

    @Override
    public void activate(String code) {
        DbUser dbUser = userRepository.findByActivationCode(code);
        if (dbUser == null) {
            logger.error("Activation code not found");
            throw new UserActivationFailed("Activation code is invalid");
        } else if (dbUser.getToken().getExpiryDate().isBefore(Instant.now())) {
            logger.error("Activation code too old");
            userRepository.clearUserTokens(dbUser);
            throw new UserActivationFailed("Activation code is invalid");
        } else {
            logger.info("User activated");
            userRepository.clearUserTokens(dbUser);
            userRepository.enable(dbUser);
        }
    }
}
