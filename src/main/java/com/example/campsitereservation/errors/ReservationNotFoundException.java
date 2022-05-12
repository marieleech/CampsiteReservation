package com.example.campsitereservation.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "There is no reservation based on this booking reference id.")
public class ReservationNotFoundException extends Exception {
	public ReservationNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}
