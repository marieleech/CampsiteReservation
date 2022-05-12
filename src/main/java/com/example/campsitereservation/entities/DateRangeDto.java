package com.example.campsitereservation.entities;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DateRangeDto {
	LocalDate checkInDate;
	LocalDate checkOutDate;
}
