package com.oakenscience.todoapp.repositories;

import com.oakenscience.todoapp.models.Item;
import com.oakenscience.todoapp.models.Project;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository {
    List<Project> findAll();

    Project createNew(Project project);

    Project update(Project project);

    void delete(Project project);

    void setCollapsed(Project project);

    Project getProjectBelow(Project project);

    Project getProjectAbove(Project project);

    void setChildOrder(Project project, Integer childOrder);

    void resetOrder();

    Integer getNextChildOrder(Long parentId);

    Project getParent(Project project);
    Project setParent(Project project, Project parent);

}
