package com.casumo.interview.videorental.core.dao;

import com.casumo.interview.videorental.Sequence;
import com.casumo.interview.videorental.api.Age;
import com.casumo.interview.videorental.api.Customer;
import com.casumo.interview.videorental.api.Film;
import com.casumo.interview.videorental.api.Rental;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RentalStoreTest {

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	private RentalDAO rentalDAO;

	@Before
	public void setUp() throws Exception {
		rentalDAO = new RentalStore(new Sequence());
	}

	@Test
	public void createEmptyThrowsException() {
		expectedException.expect(NullPointerException.class);

		rentalDAO.create(null);
	}

	@Test
	public void createFilledPerformedCorrectly() {
		final Rental rental = createRental();

		final Rental created = rentalDAO.create(rental);

		assertThat(created.getId()).isEqualTo(1);
		assertThat(created.getCustomer()).isEqualTo(rental.getCustomer());
		assertThat(created.getFilmsRented()).isEqualTo(rental.getFilmsRented());
		assertThat(created.getDaysRentedFor()).isEqualTo(rental.getDaysRentedFor());
	}

	private Rental createRental() {
		final Rental rental = new Rental();

		final Customer customer = new Customer();
		customer.setName("Malin Svensson");
		customer.setBalance(300);
		rental.setCustomer(customer);

		final Film film = new Film();
		film.setTitle("The Beauty And The Beast");
		film.setAge(Age.NEW);
		final List<Film> films = Lists.newArrayList(film);
		rental.setFilmsRented(films);

		rental.setDaysRentedFor(3);

		rental.setRentedOn(new Date());

		return rental;
	}

	@Test
	public void findByAbsentId() {
		final Optional<Rental> maybeRental = rentalDAO.findById(0);

		assertThat(maybeRental.isPresent()).isFalse();
	}

	@Test
	public void findByPresentId() {
		final Rental rental = createRental();
		final Rental created = rentalDAO.create(rental);

		final Optional<Rental> maybeRental = rentalDAO.findById(created.getId());

		assertThat(maybeRental.isPresent());
		assertThat(maybeRental.get()).isEqualTo(created);
	}

	@Test
	public void findAllEmpty() {
		final List<Rental> empty = rentalDAO.findAll();

		assertThat(empty).isEmpty();
	}

	@Test
	public void findAllFilled() {
		final Rental rental = createRental();
		final Rental created = rentalDAO.create(rental);

		final List<Rental> filled = rentalDAO.findAll();

		assertThat(filled.size()).isEqualTo(1);
		assertThat(filled.get(0)).isEqualTo(created);
	}

	@Test
	public void findByCustomer() {
		final Rental rental = createRental();
		final Rental created = rentalDAO.create(rental);

		final List<Rental> byCustomer = rentalDAO.findByCustomer(created.getCustomer());

		assertThat(byCustomer.size()).isEqualTo(1);
		assertThat(byCustomer.get(0)).isEqualTo(created);
	}

	@Test
	public void findByFilm() {
		final Rental rental = createRental();
		final Rental created = rentalDAO.create(rental);

		final List<Rental> byFilm = rentalDAO.findByFilm(created.getFilmsRented().get(0));

		assertThat(byFilm.size()).isEqualTo(1);
		assertThat(byFilm.get(0)).isEqualTo(created);
	}

	@Test
	public void updateReturnedOn() throws InterruptedException {
		final Rental rental = createRental();
		final Rental created = rentalDAO.create(rental);
		Thread.sleep(1L);
		final Date now = new Date();

		rentalDAO.updateReturnedOn(created, now);

		assertThat(created.getReturnedOn()).isEqualTo(now);
	}

	@Test
	public void updateNormalPrice() {
		final Rental rental = createRental();
		final Rental created = rentalDAO.create(rental);

		rentalDAO.updateNormalPrice(created, 100);

		assertThat(created.getNormalPrice()).isEqualTo(100);
	}

	@Test
	public void updateLateCharge() {
		final Rental rental = createRental();
		final Rental created = rentalDAO.create(rental);

		rentalDAO.updateLateCharge(created, 200);

		assertThat(created.getLateCharge()).isEqualTo(200);
	}
}