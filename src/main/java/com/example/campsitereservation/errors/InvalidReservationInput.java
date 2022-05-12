package com.example.campsitereservation.errors;

public class InvalidReservationInput extends Exception {
	public InvalidReservationInput(String errorMessage) {
		super(errorMessage);
	}
}
