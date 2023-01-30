package com.oakenscience.todoapp.service;

import com.oakenscience.todoapp.models.DbUser;
import com.oakenscience.todoapp.models.UserInfo;
import com.oakenscience.todoapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        DbUser dbUser = userRepository.findByEmail(email);
        if (dbUser == null) {
            throw new UsernameNotFoundException("No user found with username: " + email);
        }
        boolean enabled = dbUser.getActivated();
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        return new UserInfo(dbUser,
                dbUser.getEmail(), dbUser.getPassword(), enabled, accountNonExpired,
                credentialsNonExpired, accountNonLocked, Collections.singletonList(new SimpleGrantedAuthority("ALL")));
    }
}