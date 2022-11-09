package com.mongodb.starter.repositories;

import com.mongodb.starter.models.Project;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigRepository {
    public Long getNextItemId();
    public Long getNextProjectId();
}
