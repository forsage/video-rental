package com.casumo.interview.videorental;

import com.casumo.interview.videorental.api.Age;
import com.casumo.interview.videorental.api.Customer;
import com.casumo.interview.videorental.api.Film;
import com.casumo.interview.videorental.api.Rental;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTestActions {

	protected Client client;

	@ClassRule
	public static final DropwizardAppRule<VideoRentalConfig> RULE = new DropwizardAppRule<VideoRentalConfig>(VideoRentalApplication.class);

	@Before
	public void setUp() throws Exception {
		client = ClientBuilder.newClient();
	}

	@After
	public void tearDown() throws Exception {
		client.close();
	}

	protected Customer createAndCheckCustomer() {
		final Customer template = new Customer();
		template.setName("Malin Svensson");
		template.setBalance(300);

		final Customer customer = client.target("http://localhost:" + RULE.getLocalPort() + "/customer/create")
				.request()
				.post(Entity.entity(template, MediaType.APPLICATION_JSON_TYPE))
				.readEntity(Customer.class);

		assertThat(customer.getId()).isNotNull();
		assertThat(customer.getName()).isEqualTo(template.getName());
		assertThat(customer.getBalance()).isEqualTo(template.getBalance());

		return customer;
	}

	protected Film createAndCheckFilm() {
		final Film template = new Film();
		template.setTitle("The Beauty And The Beast");
		template.setAge(Age.NEW);

		final Film film = client.target("http://localhost:" + RULE.getLocalPort() + "/film/create")
				.request()
				.post(Entity.entity(template, MediaType.APPLICATION_JSON_TYPE))
				.readEntity(Film.class);

		assertThat(film.getId()).isNotNull();
		assertThat(film.getTitle()).isEqualTo(template.getTitle());
		assertThat(film.getAge()).isEqualTo(template.getAge());
		return film;
	}

	protected Customer getCustomer() {
		return client.target("http://localhost:" + RULE.getLocalPort() + "/customer/getAll")
				.request()
				.get(new GenericType<List<Customer>>() {
				}).get(0);
	}

	protected List<Customer> getAllCustomers() {
		return client.target("http://localhost:" + RULE.getLocalPort() + "/customer/getAll")
				.request()
				.get(new GenericType<List<Customer>>() {
				});
	}

	protected List<Film> getAllFilms() {
		return client.target("http://localhost:" + RULE.getLocalPort() + "/film/getAll")
				.request()
				.get(new GenericType<List<Film>>() {
				});
	}

	protected Rental startRental(Rental template) {
		return client.target("http://localhost:" + RULE.getLocalPort() + "/rental/startRental")
				.request()
				.post(Entity.entity(template, MediaType.APPLICATION_JSON_TYPE))
				.readEntity(Rental.class);
	}

	protected List<Rental> getRentalsByCustomerId(Customer customer) {
		return client.target("http://localhost:" + RULE.getLocalPort() + "/rental/getByCustomerId")
				.queryParam("customerId", customer.getId())
				.request()
				.get(new GenericType<List<Rental>>() {
				});
	}

	protected Rental finishRental(Rental started) {
		return client.target("http://localhost:" + RULE.getLocalPort() + "/rental/finishRental")
				.request()
				.post(Entity.entity(started, MediaType.APPLICATION_JSON_TYPE))
				.readEntity(Rental.class);
	}
}
