package com.oakenscience.todoapp.repositories;

import com.oakenscience.todoapp.models.DbUser;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    DbUser createNew(DbUser dbUser);

    DbUser findByEmail(String email);

    DbUser updateUserToken(DbUser dbUser);

    DbUser findByActivationCode(String code);

    DbUser enable(DbUser dbUser);

    DbUser clearUserTokens(DbUser dbUser);
}
