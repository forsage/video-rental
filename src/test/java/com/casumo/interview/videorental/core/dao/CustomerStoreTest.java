package com.casumo.interview.videorental.core.dao;

import com.casumo.interview.videorental.Sequence;
import com.casumo.interview.videorental.api.Customer;
import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerStoreTest {

	private CustomerDAO customerDAO;

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		customerDAO = new CustomerStore(new Sequence());
	}

	@Test
	public void createNullThrowsException() {
		expectedException.expect(NullPointerException.class);
		expectedException.expectMessage("customer has to be set");

		customerDAO.create(null);
	}

	@Test
	public void createFilledReturnsCorrect() {
		final Customer filled = createCustomer();

		final Customer created = customerDAO.create(filled);

		assertThat(created.getId()).isEqualTo(1);
		assertThat(created.getName()).isEqualTo(filled.getName());
		assertThat(created.getBalance()).isEqualTo(filled.getBalance());
		assertThat(created.getBonus()).isEqualTo(filled.getBonus());
	}

	private Customer createCustomer() {
		final Customer customer = new Customer();

		customer.setName("Malin Svensson");
		customer.setBalance(300);
		customer.setBonus(0);

		return customer;
	}

	@Test
	public void findByAbsentId() {
		final Optional<Customer> maybeCustomer = customerDAO.findById(0);

		assertThat(maybeCustomer.isPresent()).isFalse();
	}

	@Test
	public void findByPresentId() {
		final Customer filled = createCustomer();
		final Customer existing = customerDAO.create(filled);

		final Optional<Customer> maybeCustomer = customerDAO.findById(existing.getId());

		assertThat(maybeCustomer.isPresent()).isTrue();
		assertThat(maybeCustomer.get()).isEqualTo(existing);
	}

	@Test
	public void findAllEmpty() {
		final List<Customer> empty = customerDAO.findAll();

		assertThat(empty).isEmpty();
	}

	@Test
	public void findAllFilled() {
		final Customer filled = createCustomer();
		final Customer firstExisting = customerDAO.create(filled);
		final Customer secondExisting = customerDAO.create(filled);

		final List<Customer> customers = customerDAO.findAll();

		assertThat(customers).isNotEmpty();
		assertThat(customers.size()).isEqualTo(2);
		assertThat(customers.get(0)).isEqualTo(firstExisting);
		assertThat(customers.get(1)).isEqualTo(secondExisting);
	}

	@Test
	public void withdrawNullThrowsException() {
		expectedException.expect(NullPointerException.class);
		expectedException.expectMessage("customer has to be set");

		customerDAO.withdrawBalance(null, 0);
	}

	@Test
	public void withdrawTooMuchThrowsException() {
		final Customer customer = createCustomer();

		expectedException.expect(IllegalArgumentException.class);

		customerDAO.withdrawBalance(customer, customer.getBalance() + 1);
	}

	@Test
	public void withdrawCorrectAmountPerformed() {
		final Customer customer = createCustomer();

		customerDAO.withdrawBalance(customer, customer.getBalance() - 100);

		assertThat(customer.getBalance()).isEqualTo(100);

		customerDAO.withdrawBalance(customer, 100);

		assertThat(customer.getBalance()).isEqualTo(0);
	}

	@Test
	public void addBonusNullThrowsException() {
		expectedException.expect(NullPointerException.class);
		expectedException.expectMessage("customer has to be set");

		customerDAO.addBonus(null, 0);
	}

	@Test
	public void addZeroBonusThrowsException() {
		final Customer customer = createCustomer();

		expectedException.expect(IllegalArgumentException.class);

		customerDAO.addBonus(customer, 0);
	}

	@Test
	public void addPositiveBonusPerformedCorrectly() {
		final Customer customer = createCustomer();

		customerDAO.addBonus(customer, 100);

		assertThat(customer.getBonus()).isEqualTo(100);
	}
}