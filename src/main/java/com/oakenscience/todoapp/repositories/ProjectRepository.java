package com.oakenscience.todoapp.repositories;

import com.oakenscience.todoapp.models.Project;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository {
    List<Project> findAll();

    List<Project> findAll(List<String> ids);

    Project findOne(String id);

    Project createNew(Project project);
}
