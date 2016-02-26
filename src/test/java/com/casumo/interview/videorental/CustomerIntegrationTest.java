package com.casumo.interview.videorental;

import com.casumo.interview.videorental.api.Customer;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerIntegrationTest extends IntegrationTestActions {

	@Test
	public void testCustomerResource() {
		final Customer customer = createAndCheckCustomer();

		final List<Customer> allCustomers = getAllCustomers();

		assertThat(allCustomers.size()).isEqualTo(1);
		assertThat(allCustomers.get(0)).isEqualTo(customer);
	}
}
