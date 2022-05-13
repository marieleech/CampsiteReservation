package com.example.campsitereservation.beans;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.context.annotation.Bean;

import com.example.campsitereservation.entities.Reservation;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationBean {
	private Long id;
	private String bookingReference;
	private LocalDate reservationDate;
	private String email;
	private String firstName;
	private String lastName;

	@Bean
	public Reservation getReservation() {
		return new Reservation(this.id, this.bookingReference, this.reservationDate, this.email, this.firstName, this.lastName);
	}
}
