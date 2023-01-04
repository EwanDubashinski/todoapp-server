package com.oakenscience.todoapp.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerificationToken {
    private static final int EXPIRATION = 60 * 60 * 24;

    public VerificationToken() {
        this.token = UUID.randomUUID().toString();
        this.expiryDate = Instant.now().plusSeconds(EXPIRATION);
    }

    private String token;
    private Instant expiryDate;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }
}
