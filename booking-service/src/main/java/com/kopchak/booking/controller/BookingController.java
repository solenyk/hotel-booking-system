package com.kopchak.booking.controller;

import com.kopchak.booking.domain.HotelUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/booking")
public class BookingController {

    @GetMapping
    public String test(@AuthenticationPrincipal HotelUser user) {
        return user.toString();
    }
}
