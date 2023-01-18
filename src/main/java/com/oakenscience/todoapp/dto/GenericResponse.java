package com.oakenscience.todoapp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericResponse {

    private String message;
    private String error;

    private List<Problem> problems;

    public GenericResponse(String message) {
        this.message = message;
    }

    public GenericResponse(final String message, final String error) {
        super();
        this.message = message;
        this.error = error;
    }

    public GenericResponse(List<ObjectError> allErrors, String error) {
        this.error = error;
        this.problems = allErrors.stream().map(e -> {
            Problem problem;
            if (e instanceof FieldError) {
                problem = new Problem(((FieldError) e).getField(), e.getDefaultMessage());
            } else {
                problem = new Problem(e.getDefaultMessage());
            }
            return problem;
        }).collect(Collectors.toList());
    }

    public GenericResponse(List<FieldError> fieldErrors, List<ObjectError> globalErrors) {
        super();
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.message = mapper.writeValueAsString(fieldErrors);
            this.error = mapper.writeValueAsString(globalErrors);
        } catch (JsonProcessingException e) {
            this.message = "";
            this.error = "";
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<Problem> getProblems() {
        return problems;
    }

    public void setProblems(List<Problem> problems) {
        this.problems = problems;
    }
}
