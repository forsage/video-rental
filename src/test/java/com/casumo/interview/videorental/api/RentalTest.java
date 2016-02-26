package com.casumo.interview.videorental.api;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;

public class RentalTest {

	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

	@Test
	public void whenSerializedToJSONValuesAreWritten() throws Exception {
		final Rental rental = createRental();

		final String expected = MAPPER.writeValueAsString(MAPPER.readValue(createFixture(), Rental.class));

		assertThat(MAPPER.writeValueAsString(rental)).isEqualTo(expected);
	}

	@Test
	public void whenDeserializedFromJSONValuesAreRead() throws Exception {
		final Rental rental = createRental();

		assertThat(MAPPER.readValue(createFixture(), Rental.class)).isEqualTo(rental);
	}

	private Rental createRental() {
		final Rental rental = new Rental();

		rental.setId(1);
		rental.setDaysRentedFor(1);

		return rental;
	}

	private String createFixture() {
		return fixture("fixtures/rental.json");
	}
}