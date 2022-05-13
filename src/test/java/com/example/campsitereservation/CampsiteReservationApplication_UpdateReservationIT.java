package com.example.campsitereservation;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CampsiteReservationApplication_UpdateReservationIT extends CampsiteReservationApplicationIT {

	@Test
	public void updateUserInfoShouldReturn200() {
		String bookingReference = createReservationWithDates(LocalDate.now().plusDays(3), LocalDate.now().plusDays(5));
		ResponseEntity<String> responseEntity = restTemplate.exchange(
				"http://localhost:" + port + String.format("/reservations/%s", bookingReference),
				HttpMethod.PUT,
				buildCreateReservationRequestEntity(
						"sampleEmail@email.com",
						"FirstName",
						"LastName",
						LocalDate.now().plusDays(6),
						LocalDate.now().plusDays(8)
				),
				String.class);

		assertEquals(200, responseEntity.getStatusCodeValue());
	}

	@Test
	public void updateDatesShouldReturn200() {
		String bookingReference = createReservationWithDates(LocalDate.now().plusWeeks(1), LocalDate.now().plusWeeks(1).plusDays(3));
		ResponseEntity<String> responseEntity = restTemplate.exchange(
				"http://localhost:" + port + String.format("/reservations/%s", bookingReference),
				HttpMethod.PUT,
				buildUpdateDatesResponseEntity(
						LocalDate.now().plusWeeks(1).plusDays(5),
						LocalDate.now().plusWeeks(1).plusDays(8)
				),
				String.class);
		assertEquals(200, responseEntity.getStatusCodeValue());
	}

	@Test
	public void updateDatesShouldReturn400WhenChangedDatesAreInvalid() {
		String bookingReference = createReservationWithDates(LocalDate.now().plusWeeks(2), LocalDate.now().plusWeeks(2).plusDays(5));
		createReservationWithDates(LocalDate.now().plusWeeks(2).plusDays(7), LocalDate.now().plusWeeks(2).plusDays(10));

		ResponseEntity<String> responseEntity = restTemplate.exchange(
				"http://localhost:" + port + String.format("/reservations/%s", bookingReference),
				HttpMethod.PUT,
				buildUpdateDatesResponseEntity(
						LocalDate.now().plusWeeks(2).plusDays(7),
						LocalDate.now().plusWeeks(2).plusDays(10)
				),
				String.class);

		assertEquals(400, responseEntity.getStatusCodeValue());
	}
}
