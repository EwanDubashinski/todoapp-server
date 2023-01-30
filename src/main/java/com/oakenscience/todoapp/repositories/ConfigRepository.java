package com.oakenscience.todoapp.repositories;

import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository {
    public Long getNextItemId();
    public Long getNextProjectId();
    public Long getNextUserId();
}
