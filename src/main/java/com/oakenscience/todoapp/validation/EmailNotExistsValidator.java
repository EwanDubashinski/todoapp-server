package com.oakenscience.todoapp.validation;

import com.oakenscience.todoapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class EmailNotExistsValidator implements ConstraintValidator<EmailNotExists, Object> {

    @Autowired
    UserRepository userRepository;

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        String email = (String) object;
        return emailNotExists(email);
    }

    @Override
    public void initialize(EmailNotExists constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    private boolean emailNotExists(final String email) {
        return userRepository.findByEmail(email) == null;
    }
}
