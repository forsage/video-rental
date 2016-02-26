package com.casumo.interview.videorental.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class CustomerTest {

	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

	@Test
	public void whenSerializedToJSONValuesAreWritten() throws Exception {
		final Customer customer = createCustomer();

		final String expected = MAPPER.writeValueAsString(MAPPER.readValue(createFixture(), Customer.class));

		assertThat(MAPPER.writeValueAsString(customer)).isEqualTo(expected);
	}

	@Test
	public void whenDeserializedFromJSONValuesAreRead() throws Exception {
		final Customer customer = createCustomer();

		assertThat(MAPPER.readValue(createFixture(), Customer.class)).isEqualTo(customer);
	}

	private Customer createCustomer() {
		final Customer customer = new Customer();

		customer.setId(1);
		customer.setName("Malin Svensson");

		return customer;
	}

	private String createFixture() {
		return fixture("fixtures/customer.json");
	}
}