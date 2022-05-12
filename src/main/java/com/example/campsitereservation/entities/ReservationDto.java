package com.example.campsitereservation.entities;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationDto {
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String email;
    private String firstName;
    private String lastName;
    private String bookingReference;
}
