package com.oakenscience.todoapp.service;
import com.oakenscience.todoapp.dto.UserDto;
import com.oakenscience.todoapp.models.DbUser;
import com.oakenscience.todoapp.models.VerificationToken;
import com.oakenscience.todoapp.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public DbUser signup(UserDto accountDto) {
        final DbUser dbUser = new DbUser();

        dbUser.setName(accountDto.getName());
        dbUser.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        dbUser.setEmail(accountDto.getEmail());
        dbUser.setToken(new VerificationToken());
        dbUser.setActivated(false);
        return userRepository.createNew(dbUser);
    }

    public DbUser authenticate(UserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail());
    }
}
