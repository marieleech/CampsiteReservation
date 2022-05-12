package com.example.campsitereservation.errors;

public class ReservationNotAvailableException extends Exception {
	public ReservationNotAvailableException(String errorMessage) {
		super(errorMessage);
	}
}
