package com.kopchak.booking.dto;

import com.kopchak.booking.validation.ValidDateRange;
import com.kopchak.booking.validation.ValidationStepOne;
import com.kopchak.booking.validation.ValidationStepTwo;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@ValidDateRange
@GroupSequence(value = {ValidationStepOne.class, ValidationStepTwo.class, BookingDTO.class})
public record BookingDTO(
        @NotNull(groups = ValidationStepOne.class)
        @FutureOrPresent(groups = ValidationStepTwo.class)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,

        @NotNull(groups = ValidationStepOne.class)
        @FutureOrPresent(groups = ValidationStepTwo.class)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate
) {
}
