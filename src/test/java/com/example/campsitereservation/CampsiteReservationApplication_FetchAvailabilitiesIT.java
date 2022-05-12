package com.example.campsitereservation;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CampsiteReservationApplication_FetchAvailabilitiesIT extends CampsiteReservationApplicationIT {

	@Test
	public void shouldReturn200Default1Month() {
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(
				"http://localhost:" + port + "/reservations",
				String.class,
				buildJsonEntityEmpty()
				);
		assertEquals(200, responseEntity.getStatusCodeValue());
		assertTrue(responseEntity.getBody().contains("The available dates within your time range are:"));
	}

	@Test
	public void shouldReturn200GiveDates() {
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(
				"http://localhost:" + port + "/reservations",
				String.class,
				buildDateRangeEntity(LocalDate.now(), LocalDate.now().plusWeeks(3))
		);
		assertEquals(200, responseEntity.getStatusCodeValue());
		assertTrue(responseEntity.getBody().contains("The available dates within your time range are:"));
	}

	@Test
	public void shouldReturn200NoAvailabilities() {
		createReservation();
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(
				"http://localhost:" + port + "/reservations",
				String.class,
				buildDateRangeEntity(LocalDate.now().plusDays(3), LocalDate.now().plusDays(5))
		);
		System.out.println(responseEntity);
		assertEquals(200, responseEntity.getStatusCodeValue());
		assertTrue(responseEntity.getBody().contains("There are no availabilities in your given time range."));
	}

	private void createReservation() {
		restTemplate
				.postForEntity(
						"http://localhost:" + port + "/reservations",
						buildCreateReservationRequestEntity(
								"sampleEmail@email.com",
								"FirstName",
								"LastName",
								LocalDate.now().plusDays(3),
								LocalDate.now().plusDays(5)),
						String.class);
	}
}
