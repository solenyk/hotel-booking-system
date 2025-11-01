package com.kopchak.hotel.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class StringEnumerationConstraintValidator implements ConstraintValidator<StringEnumeration, String> {

    private Set<String> allowedValues;

    @Override
    public void initialize(StringEnumeration constraintAnnotation) {
        Class<? extends Enum<?>> enumClass = constraintAnnotation.enumClass();
        this.allowedValues = Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null && allowedValues.contains(value.toUpperCase())) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        HibernateConstraintValidatorContext hibernateContext =
                context.unwrap(HibernateConstraintValidatorContext.class);
        hibernateContext.addMessageParameter("allowedValues", String.join(",", allowedValues));
        hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addConstraintViolation();
        return false;
    }
}
