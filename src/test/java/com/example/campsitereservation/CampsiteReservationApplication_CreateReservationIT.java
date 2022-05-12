package com.example.campsitereservation;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CampsiteReservationApplication_CreateReservationIT extends CampsiteReservationApplicationIT {

	@Test
	public void createReservationShouldReturn200AndBookingReference() {
		ResponseEntity<String> responseEntity = restTemplate
				.postForEntity(
						"http://localhost:" + port + "/reservations",
						buildCreateReservationRequestEntity(
								"sampleEmail@email.com",
								"FirstName",
								"LastName",
								LocalDate.now().plusDays(3),
								LocalDate.now().plusDays(6)
						),
						String.class);

		assertEquals(200, responseEntity.getStatusCodeValue());
		assertTrue(responseEntity.getBody().contains("Reservation successful! Here is your booking reference number:"));
	}

	@Test
	public void createReservationShouldReturn400WhenDatesNotAvailable() {
		ResponseEntity responseEntity = this.restTemplate
				.postForEntity(
						"http://localhost:" + port + "/reservations",
						buildCreateReservationRequestEntity(
								"sampleEmail@email.com",
								"FirstName",
								"LastName",
								LocalDate.now().plusDays(3),
								LocalDate.now().plusDays(6)),
						String.class);

		assertEquals(400, responseEntity.getStatusCodeValue());
		assertTrue(responseEntity.getBody().toString().contains("These reservation dates are not available. Please choose another date."));
	}

	@Test
	public void createReservationShouldReturn400WhenProvidedDatesAreTooAdvanced() {
		ResponseEntity responseEntity = this.restTemplate
				.postForEntity(
						"http://localhost:" + port + "/reservations",
						buildCreateReservationRequestEntity(
								"sampleEmail@email.com",
								"FirstName",
								"LastName",
								LocalDate.now().plusWeeks(5),
								LocalDate.now().plusMonths(6)),
						String.class);

		assertEquals(400, responseEntity.getStatusCodeValue());
		assertTrue(responseEntity.getBody().toString().contains("Please book no less than one day in advance and no more than one month in advance."));
	}

	@Test
	public void createReservationShouldReturn400WhenProvidedDurationIsInvalid() {
		ResponseEntity responseEntity = this.restTemplate
				.postForEntity(
						"http://localhost:" + port + "/reservations",
						buildCreateReservationRequestEntity(
								"sampleEmail@email.com",
								"FirstName",
								"LastName",
								LocalDate.now().plusWeeks(1),
								LocalDate.now().plusMonths(6)
						),
						String.class);

		assertEquals(400, responseEntity.getStatusCodeValue());
		assertTrue(responseEntity.getBody().toString().contains("The duration of this reservation is invalid. Please choose a duration that is maximum three days long, and no less than 1 day long."));
	}

	@Test
	public void createReservationShouldReturn400ConcurrencyTest() {
		ResponseEntity responseEntityReservationAccepted = this.restTemplate
				.postForEntity(
						"http://localhost:" + port + "/reservations",
						buildCreateReservationRequestEntity(
								"sampleEmail@email.com",
								"FirstName",
								"LastName",
								LocalDate.now().plusWeeks(1),
								LocalDate.now().plusWeeks(1).plusDays(2)
						),
						String.class);

		ResponseEntity responseEntityReservationRejected = this.restTemplate
				.postForEntity(
						"http://localhost:" + port + "/reservations",
						buildCreateReservationRequestEntity(
								"sampleEmail@email.com",
								"FirstName",
								"LastName",
								LocalDate.now().plusWeeks(1),
								LocalDate.now().plusWeeks(1).plusDays(2)
						),
						String.class);

		assertEquals(200, responseEntityReservationAccepted.getStatusCodeValue());
		assertEquals(400, responseEntityReservationRejected .getStatusCodeValue());
		assertTrue(responseEntityReservationAccepted.getBody().toString().contains("Reservation successful! Here is your booking reference number:"));
		assertTrue(responseEntityReservationRejected.getBody().toString().contains("These reservation dates are not available. Please choose another date."));
	}
}
