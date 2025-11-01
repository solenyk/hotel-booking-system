package com.kopchak.hotel.dto;

import com.kopchak.hotel.domain.RoomType;
import com.kopchak.hotel.validation.StringEnumeration;
import com.kopchak.hotel.validation.steps.ValidationStepOne;
import com.kopchak.hotel.validation.steps.ValidationStepTwo;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@GroupSequence(value = {ValidationStepOne.class, ValidationStepTwo.class, CreateUpdateRoomDTO.class})
public record CreateUpdateRoomDTO(

        @NotNull(message = "Invalid room number: room number is mandatory", groups = ValidationStepOne.class)
        Integer number,

        @StringEnumeration(
                message = "Invalid group type: group type should be in {allowedValues}",
                enumClass = RoomType.class,
                groups = ValidationStepOne.class
        ) String type,

        @NotNull(message = "Invalid price per night: price per night is mandatory", groups = ValidationStepOne.class)
        @DecimalMin(
                message = "Invalid price per night: price '${formatter.format('%1$.2f', validatedValue)}' must not " +
                        "be greater than {status}",
                value = "0.0",
                inclusive = false,
                groups = ValidationStepTwo.class
        ) BigDecimal pricePerNight,

        @NotNull(message = "Invalid capacity: capacity is mandatory", groups = ValidationStepOne.class)
        @Min(
                message = "Invalid capacity: capacity '${validatedValue}' must not be less than {status}",
                value = 1,
                groups = ValidationStepTwo.class
        ) Integer capacity
) {
}
