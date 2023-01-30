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
    public List<Project> getPersons() {
        return projectRepository.findAll();
    }

    @GetMapping("project/{id}")
    public ResponseEntity<Project> getPerson(@PathVariable String id) {
        Project project = projectRepository.findOne(id);
        if (project == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(project);
    }

    @GetMapping("projects/{ids}")
    public List<Project> getProjects(@PathVariable String ids) {
        List<String> listIds = asList(ids.split(","));
        return projectRepository.findAll(listIds);
    }

    @PostMapping(value = "project/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Project createNew(@RequestBody Project project) {
        return projectRepository.createNew(project);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final Exception handleAllExceptions(RuntimeException e) {
        LOGGER.error("Internal server error.", e);
        return e;
    }


}
