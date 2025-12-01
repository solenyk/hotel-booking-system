package com.kopchak.booking.validation;

import com.kopchak.booking.dto.BookingDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidDateRangeConstraint implements ConstraintValidator<ValidDateRange, BookingDTO> {

    @Override
    public boolean isValid(BookingDTO value, ConstraintValidatorContext context) {
        return value.startDate().isBefore(value.endDate());
    }
}
