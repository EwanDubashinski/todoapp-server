package com.oakenscience.todoapp.controllers;

import com.oakenscience.todoapp.models.Item;
import com.oakenscience.todoapp.models.Project;
import com.oakenscience.todoapp.repositories.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Arrays.asList;
@CrossOrigin
@RestController
@RequestMapping("/api")
public class ProjectController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);
    private final ProjectRepository projectRepository;

    public ProjectController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @GetMapping("projects")
    public List<Project> getProjects() {
        return projectRepository.findAll();
    }

//    @GetMapping("projects/{id}")
//    public ResponseEntity<Project> getPerson(@PathVariable String id) {
//        Project project = projectRepository.findOne(id);
//        if (project == null)
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        return ResponseEntity.ok(project);
//    }

    @PostMapping(value = "projects/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Project createNew(@RequestBody Project project) {
        return projectRepository.createNew(project);
    }
    @PutMapping(value = "projects/update")
    @ResponseStatus(HttpStatus.CREATED)
    public Project update(@RequestBody Project project) {
        return projectRepository.update(project);
    }

    @PostMapping(value = "projects/delete")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long delete(@RequestBody Project project) {
        return projectRepository.delete(project);
    }
    @PostMapping(value = "projects/collapsed")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setCollapsed(@RequestBody Project project) {
        projectRepository.setCollapsed(project);
    }

    @PostMapping(value = "projects/up")
    public void up(@RequestBody Project project) {
        Project projectAbove = projectRepository.getProjectAbove(project);
        if (projectAbove != null) {
            int newOrder = projectAbove.getChildOrder();
            projectRepository.setChildOrder(project, newOrder);
        }
    }
    @PostMapping(value = "projects/down")
    public void down(@RequestBody Project project) {
        Project projectBelow = projectRepository.getProjectBelow(project);
        if (projectBelow != null) {
            int newOrder = projectBelow.getChildOrder();
            projectRepository.setChildOrder(project, newOrder);
        }
    }
    @PostMapping(value = "projects/reset")
    public void resetOrder() {
        projectRepository.resetOrder();
    }

    @PostMapping(value = "projects/right")
    public void right(@RequestBody Project project) {
        Project projectAbove = projectRepository.getProjectAbove(project);
        if (projectAbove != null) {
            Integer order = projectRepository.getNextChildOrder(projectAbove.getId());
            project = projectRepository.setParent(project, projectAbove);
            projectRepository.setChildOrder(project, order);
        };
    }
    @PostMapping(value = "projects/left")
    public void left(@RequestBody Project project) {
        if (project.getParentId() == null) return;

        Project parent = projectRepository.getParent(project);
        Project grandParent = projectRepository.getParent(parent);
        Long grandParentId = grandParent == null ? null : grandParent.getId();
//        Integer order = itemRepository.getNextChildOrder(project.getProjectId(), grandParentId);
        Integer order = parent.getChildOrder() + 1;
        project = projectRepository.setParent(project, grandParent);
        projectRepository.setChildOrder(project, order);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final Exception handleAllExceptions(RuntimeException e) {
        LOGGER.error("Internal server error.", e);
        return e;
    }
}
