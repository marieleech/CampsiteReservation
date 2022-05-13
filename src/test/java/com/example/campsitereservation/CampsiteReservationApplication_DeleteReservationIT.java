package com.example.campsitereservation;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CampsiteReservationApplication_DeleteReservationIT extends CampsiteReservationApplicationIT{

	private String bookingReference = createReservation();
	@Test
	public void deleteReservationShouldReturn200() {
		ResponseEntity<String> responseEntity = restTemplate.exchange(
				"http://localhost:" + port + String.format("/reservations/%s", bookingReference),
				HttpMethod.DELETE,
				buildJsonEntityEmpty(),
				String.class);

		assertEquals(200, responseEntity.getStatusCodeValue());
		assertTrue(responseEntity.getBody().contains("Your reservation has successfully been deleted."));
	}

	@Test
	public void deleteReservationShouldReturn404WithInvalidBookingReference() {
		ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:" + port + "/reservations/fakeReference", HttpMethod.DELETE, buildJsonEntityEmpty(), String.class);

		assertEquals(404, responseEntity.getStatusCodeValue());

	}
}
