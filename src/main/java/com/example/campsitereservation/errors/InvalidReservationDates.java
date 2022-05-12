package com.example.campsitereservation.errors;

public class InvalidReservationDates extends Exception{
	public InvalidReservationDates(String errorMessage) {
		super(errorMessage);
	}
}
