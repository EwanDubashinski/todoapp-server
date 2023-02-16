package com.oakenscience.todoapp.config;

import com.oakenscience.todoapp.models.DbUser;
import com.oakenscience.todoapp.models.UserInfo;
import org.bson.conversions.Bson;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import static com.mongodb.client.model.Filters.*;

@Component
public class AuthenticationFacade implements IAuthenticationFacade{
    @Override
    public DbUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        return userInfo.getDbUser();
    }

    @Override
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
    @Override
    public Bson forCurrentUser(Bson query) {
        return and(forCurrentUser(), query);
    }

    public Bson forCurrentUser() {
        return eq("user_id", getCurrentUserId());
    }
}
