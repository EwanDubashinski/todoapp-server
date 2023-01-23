package com.oakenscience.todoapp.repositories;

import com.oakenscience.todoapp.models.Item;
import com.oakenscience.todoapp.models.User;
import com.oakenscience.todoapp.models.VerificationToken;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    User createNew(User user);

    User findByEmail(String email);

    User updateUserToken(User user);
}
