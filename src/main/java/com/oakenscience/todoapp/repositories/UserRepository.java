package com.oakenscience.todoapp.repositories;

import com.oakenscience.todoapp.models.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    User createNew(User user);

    User findByEmail(String email);
}
