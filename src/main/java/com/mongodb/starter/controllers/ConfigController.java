package com.mongodb.starter.controllers;

import com.mongodb.starter.models.Project;
import com.mongodb.starter.repositories.ConfigRepository;
import com.mongodb.starter.repositories.ProjectRepository;
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
public class ConfigController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ConfigController.class);
    private final ConfigRepository configRepository;

    public ConfigController(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @GetMapping("items/next_id")
    public Long getNextItemId() {
        return configRepository.getNextItemId();
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final Exception handleAllExceptions(RuntimeException e) {
        LOGGER.error("Internal server error.", e);
        return e;
    }
}
