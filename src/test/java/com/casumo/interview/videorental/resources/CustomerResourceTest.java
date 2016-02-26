package com.casumo.interview.videorental.resources;

import com.casumo.interview.videorental.api.Customer;
import com.casumo.interview.videorental.core.dao.CustomerDAO;
import com.google.common.collect.Lists;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomerResourceTest {

	private static final CustomerDAO customerDAO = mock(CustomerDAO.class);

	@ClassRule
	public static final ResourceTestRule resources = ResourceTestRule.builder()
			.addResource(new CustomerResource(customerDAO))
			.build();

	@Captor
	private ArgumentCaptor<Customer> customerCaptor;
	private final Customer customer;
	private final List<Customer> customers;
	{
		customer = new Customer();

		customer.setId(1);
		customer.setName("Malin Svensson");

		customers = Lists.newArrayList(customer);
	}

	@Before
	public void setUp() throws Exception {
		when(customerDAO.findAll()).thenReturn(customers);
		when(customerDAO.create(any(Customer.class))).thenReturn(customer);
	}

	@After
	public void tearDown() throws Exception {
		reset(customerDAO);
	}

	@Test
	public void getAllFoundCorrectly() {
		final List<Customer> found = resources.client()
				.target("/customer/getAll")
				.request()
				.get(new GenericType<List<Customer>>() {
				});

		assertThat(found.get(0).getId()).isEqualTo(customers.get(0).getId());

		verify(customerDAO).findAll();
	}

	@Test
	public void testCreatingWithParametersCreatedCorrectly() {
		final Response response = resources.client()
				.target("/customer/create")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.entity(customer, MediaType.APPLICATION_JSON_TYPE));

		assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);

		verify(customerDAO).create(customerCaptor.capture());
		assertThat(customerCaptor.getValue()).isEqualTo(customer);
	}
}