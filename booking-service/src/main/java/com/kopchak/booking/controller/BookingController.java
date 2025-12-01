package com.kopchak.booking.controller;

import com.kopchak.booking.domain.HotelUser;
import com.kopchak.booking.dto.BookingDTO;
import com.kopchak.booking.dto.ExceptionDto;
import com.kopchak.booking.dto.ValidationExceptionDto;
import com.kopchak.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/{roomId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createBooking(@PathVariable Integer roomId, @AuthenticationPrincipal HotelUser user,
                              @RequestBody @Validated BookingDTO bookingDTO) {
        System.out.println(user.getId());
        bookingService.book(roomId, user, bookingDTO);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handleOptimisticLockingException() {
        return new ExceptionDto("An error occurred, please try again");
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleRuntimeException(RuntimeException e) {
        return new ExceptionDto(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationExceptionDto handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> fieldsErrorDetails = e.getBindingResult().getFieldErrors()
                .stream()
                .filter(fieldError -> fieldError.getDefaultMessage() != null)
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        return new ValidationExceptionDto(fieldsErrorDetails);
    }
}
