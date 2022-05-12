package com.example.campsitereservation;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CampsiteReservationApplication_UpdateReservationIT extends CampsiteReservationApplicationIT {

	@Test
	public void updateUserInfoShouldReturn200() {
		String bookingReference = createReservation(LocalDate.now().plusDays(3), LocalDate.now().plusDays(5));
		System.out.println(bookingReference);
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

		System.out.println(responseEntity.toString());

		assertEquals(200, responseEntity.getStatusCodeValue());
	}

	@Test
	public void updateDatesShouldReturn200() {
		String bookingReference = createReservation(LocalDate.now().plusWeeks(1), LocalDate.now().plusWeeks(1).plusDays(3));
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
		String bookingReference = createReservation(LocalDate.now().plusWeeks(2), LocalDate.now().plusWeeks(2).plusDays(5));
		createReservation(LocalDate.now().plusWeeks(2).plusDays(7), LocalDate.now().plusWeeks(2).plusDays(10));

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

	private String createReservation(LocalDate checkIn, LocalDate checkOut) {
		String responseEntity = restTemplate
				.postForEntity(
						"http://localhost:" + port + "/reservations",
						buildCreateReservationRequestEntity(
								"sampleEmail@email.com",
								"FirstName",
								"LastName",
								checkIn,
								checkOut),
						String.class).getBody();
		return responseEntity.substring(responseEntity.lastIndexOf(": ")+1).replaceAll("\\s+","");
	}
}
