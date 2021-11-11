package com.codingtask.model.form.validation;

import com.codingtask.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
public class ProductIdValidator implements ConstraintValidator<ValidProductId, Long> {

    private final ProductRepository productRepository;

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        return productRepository.findById(id).isPresent();
    }
}