package com.reservation_system.authService.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/booking")
public class BookingServiceTestController {

    @GetMapping("/status")
    public String testBookingService() {
      return "hello";
    }
}