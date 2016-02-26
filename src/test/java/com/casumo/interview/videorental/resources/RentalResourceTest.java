package com.casumo.interview.videorental.resources;

import com.casumo.interview.videorental.VideoRentalConfig;
import com.casumo.interview.videorental.api.Age;
import com.casumo.interview.videorental.api.Customer;
import com.casumo.interview.videorental.api.Film;
import com.casumo.interview.videorental.api.Rental;
import com.casumo.interview.videorental.core.bonus.BonusCalculator;
import com.casumo.interview.videorental.core.dao.CustomerDAO;
import com.casumo.interview.videorental.core.dao.FilmDAO;
import com.casumo.interview.videorental.core.dao.RentalDAO;
import com.casumo.interview.videorental.core.price.PriceCalculator;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RentalResourceTest {

	private static final FilmDAO filmDAO = mock(FilmDAO.class);
	private static final CustomerDAO customerDAO = mock(CustomerDAO.class);
	private static final RentalDAO rentalDAO = mock(RentalDAO.class);
	private static final PriceCalculator normalPriceCalculator = mock(PriceCalculator.class);
	private static final PriceCalculator lateChargeCalculator = mock(PriceCalculator.class);
	private static final PriceCalculator bonusCalculator = mock(BonusCalculator.class);

	@ClassRule
	public static final ResourceTestRule resources = ResourceTestRule.builder()
			.addResource(new RentalResource(
					filmDAO,
					customerDAO,
					rentalDAO,
					normalPriceCalculator,
					lateChargeCalculator,
					bonusCalculator))
			.build();

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Captor
	private ArgumentCaptor<Rental> rentalCaptor;
	private final Customer customer;
	private final Film film;
	private final List<Film> films;
	private final Rental rental;
	private final List<Rental> rentals;
	{
		customer = new Customer();

		customer.setId(1);
		customer.setName("Malin Svensson");
		customer.setBalance(200 + VideoRentalConfig.CUSTOMER_DEPOSIT);

		film = new Film();

		film.setId(1);
		film.setTitle("The Beauty And The Beast");
		film.setAge(Age.NEW);

		films = Lists.newArrayList(film);

		rental = new Rental();

		rental.setId(1);
		rental.setCustomer(customer);
		rental.setFilmsRented(Lists.newArrayList(film));
		rental.setRentedOn(new Date());

		rentals = Lists.newArrayList(rental);
	}

	@Before
	public void setUp() throws Exception {
		when(rentalDAO.findAll()).thenReturn(rentals);
		when(rentalDAO.create(any(Rental.class))).thenReturn(rental);
		when(rentalDAO.findByCustomer(any(Customer.class))).thenReturn(rentals);
	}

	@After
	public void tearDown() throws Exception {
		reset(customerDAO, filmDAO, rentalDAO, normalPriceCalculator, lateChargeCalculator, bonusCalculator);
	}

	@Test
	public void getByAbsentCustomerIdExceptionIsThrown() {
		when(customerDAO.findById(0)).thenReturn(Optional.<Customer>absent());

		expectedException.expect(NotFoundException.class);
		expectedException.expectMessage("HTTP 404 Not Found");

		resources.client()
				.target("/rental/getByCustomerId")
				.queryParam("customerId", 0)
				.request()
				.get(new GenericType<List<Rental>>() {
				});
	}

	@Test
	public void getByPresentCustomerIdFoundCorrectly() {
		when(customerDAO.findById(1)).thenReturn(Optional.of(customer));

		final List<Rental> found = resources.client()
				.target("/rental/getByCustomerId")
				.queryParam("customerId", 1)
				.request()
				.get(new GenericType<List<Rental>>() {
				});

		assertThat(found.get(0)).isEqualTo(rental);

		verify(customerDAO).findById(1);
		verify(rentalDAO).findByCustomer(customer);
	}

	@Test
	public void startRentalWithNonExistingCustomerRentalIsNotStarted() {
		when(customerDAO.findById(0)).thenReturn(Optional.<Customer>absent());

		final Rental withNonExistingCustomer = new Rental();
		withNonExistingCustomer.setCustomer(new Customer());
		withNonExistingCustomer.getCustomer().setId(0);

		resources.client()
				.target("/rental/startRental")
				.request()
				.post(Entity.entity(withNonExistingCustomer, MediaType.APPLICATION_JSON_TYPE));

		verify(customerDAO).findById(0);
		verifyNoMoreInteractions(customerDAO, filmDAO, rentalDAO);
	}

	@Test
	public void startRentalWithAlreadyRentedFilmsExceptionIsThrown() {
		when(customerDAO.findById(1)).thenReturn(Optional.of(customer));
		when(rentalDAO.findByFilm(film)).thenReturn(rentals);
		when(filmDAO.findAvailable()).thenReturn(Collections.<Film>emptyList());

		final Rental withAlreadyRentedFilms = new Rental();
		withAlreadyRentedFilms.setCustomer(customer);
		withAlreadyRentedFilms.setFilmsRented(films);

		expectedException.expect(ProcessingException.class);

		resources.client()
				.target("/rental/startRental")
				.request()
				.post(Entity.entity(withAlreadyRentedFilms, MediaType.APPLICATION_JSON_TYPE));

		verify(customerDAO).findById(1);
		verify(filmDAO).findAvailable();
		verifyNoMoreInteractions(customerDAO, filmDAO, rentalDAO);
	}

	@Test
	public void startRentalWithTooFewDaysRentedForExceptionIsThrown() {
		when(customerDAO.findById(1)).thenReturn(Optional.of(customer));
		when(rentalDAO.findByFilm(film)).thenReturn(rentals);
		when(filmDAO.findAvailable()).thenReturn(films);

		final Rental withTooFewDaysRentedFor = new Rental();
		withTooFewDaysRentedFor.setCustomer(customer);
		withTooFewDaysRentedFor.setFilmsRented(films);
		withTooFewDaysRentedFor.setDaysRentedFor(2);

		expectedException.expect(ProcessingException.class);

		resources.client()
				.target("/rental/startRental")
				.request()
				.post(Entity.entity(withTooFewDaysRentedFor, MediaType.APPLICATION_JSON_TYPE));

		verify(customerDAO).findById(1);
		verify(filmDAO).findAvailable();
		verifyNoMoreInteractions(customerDAO, filmDAO, rentalDAO);
	}

	@Test
	public void startRentalWithTooLowCustomerBalanceExceptionIsThrown() {
		when(customerDAO.findById(1)).thenReturn(Optional.of(customer));
		when(rentalDAO.findByFilm(film)).thenReturn(rentals);
		when(filmDAO.findAvailable()).thenReturn(films);
		final int justTooHighPrice = customer.getBalance() - VideoRentalConfig.CUSTOMER_DEPOSIT + 1;
		when(normalPriceCalculator.calculate(any(Rental.class))).thenReturn(justTooHighPrice);

		final Rental withTooLowCustomerBalance = new Rental();
		withTooLowCustomerBalance.setCustomer(customer);
		withTooLowCustomerBalance.setFilmsRented(films);
		withTooLowCustomerBalance.setDaysRentedFor(3);

		expectedException.expect(ProcessingException.class);

		resources.client()
				.target("/rental/startRental")
				.request()
				.post(Entity.entity(withTooLowCustomerBalance, MediaType.APPLICATION_JSON_TYPE));

		verify(customerDAO).findById(1);
		verify(filmDAO).findAvailable();
		verify(normalPriceCalculator.calculate(any(Rental.class)));
		verifyNoMoreInteractions(customerDAO, filmDAO, rentalDAO, normalPriceCalculator);
	}

	@Test
	public void startRentalWithAllParametersCorrectRentalIsCreated() {
		when(customerDAO.findById(1)).thenReturn(Optional.of(customer));
		when(rentalDAO.findByFilm(film)).thenReturn(rentals);
		when(filmDAO.findAvailable()).thenReturn(films);
		final int justLowEnoughPrice = customer.getBalance() - VideoRentalConfig.CUSTOMER_DEPOSIT;
		when(normalPriceCalculator.calculate(any(Rental.class))).thenReturn(justLowEnoughPrice);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(final InvocationOnMock ignored) throws Throwable {
				rental.setNormalPrice(justLowEnoughPrice);
				return null;
			}
		}).when(rentalDAO).updateNormalPrice(rental, justLowEnoughPrice);

		final Rental withAllParametersCorrect = new Rental();
		withAllParametersCorrect.setCustomer(customer);
		withAllParametersCorrect.setFilmsRented(films);
		withAllParametersCorrect.setDaysRentedFor(3);

		when(rentalDAO.create(withAllParametersCorrect)).thenReturn(rental);

		final Rental created = resources.client()
				.target("/rental/startRental")
				.request()
				.post(Entity.entity(withAllParametersCorrect, MediaType.APPLICATION_JSON_TYPE))
				.readEntity(Rental.class);

		assertThat(created).isEqualTo(rental);
		assertThat(created.getRentedOn()).isNotNull();
		assertThat(created.getReturnedOn()).isNull();
		assertThat(created.getNormalPrice()).isEqualTo(justLowEnoughPrice);
		assertThat(created.getLateCharge()).isEqualTo(0);

		verify(customerDAO).findById(1);
		verify(filmDAO).findAvailable();
		verify(normalPriceCalculator).calculate(withAllParametersCorrect);
		verify(rentalDAO).create(withAllParametersCorrect);
		verify(rentalDAO).updateNormalPrice(rental, justLowEnoughPrice);
		verify(customerDAO).withdrawBalance(customer, justLowEnoughPrice);
		verifyNoMoreInteractions(customerDAO, filmDAO, rentalDAO, normalPriceCalculator);
	}

	@Test
	public void finishAbsentRentalExceptionIsThrown() {
		final Rental absentRental = createAbsentRental();

		expectedException.expect(ProcessingException.class);

		resources.client()
				.target("/rental/finishRental")
				.request()
				.post(Entity.entity(absentRental, MediaType.APPLICATION_JSON_TYPE));

		verify(rentalDAO).findById(absentRental.getId());
		verifyNoMoreInteractions(customerDAO, filmDAO, rentalDAO);
	}

	private Rental createAbsentRental() {
		final int absentId = 0;
		when(rentalDAO.findById(absentId)).thenReturn(Optional.<Rental>absent());

		final Rental absentRental = new Rental();
		absentRental.setId(absentId);
		absentRental.setCustomer(customer);
		absentRental.setFilmsRented(films);

		return absentRental;
	}

	@Test
	public void finishAlreadyReturnedRentalExceptionIsThrown() {
		final Rental alreadyReturned = createPresentRental();
		alreadyReturned.setRentedOn(new Date());
		when(rentalDAO.findById(alreadyReturned.getId())).thenReturn(Optional.of(alreadyReturned));

		expectedException.expect(ProcessingException.class);

		resources.client()
				.target("/rental/finishRental")
				.request()
				.post(Entity.entity(alreadyReturned, MediaType.APPLICATION_JSON_TYPE));

		verify(rentalDAO).findById(alreadyReturned.getId());
		verifyNoMoreInteractions(customerDAO, filmDAO, rentalDAO);
	}

	private Rental createPresentRental() {
		final int presentId = 1;

		final Rental presentRental = new Rental();
		presentRental.setId(presentId);
		presentRental.setCustomer(customer);
		presentRental.setFilmsRented(films);
		presentRental.setDaysRentedFor(3);
		presentRental.setRentedOn(new Date());

		when(rentalDAO.findById(presentId)).thenReturn(Optional.of(presentRental));

		return presentRental;
	}

	private Rental copy(final Rental original) {
		final Rental copied = new Rental();

		copied.setId(original.getId());
		copied.setCustomer(original.getCustomer());
		copied.setFilmsRented(original.getFilmsRented());
		copied.setDaysRentedFor(original.getDaysRentedFor());
		copied.setRentedOn(original.getRentedOn());
		copied.setReturnedOn(original.getReturnedOn());

		return copied;
	}

	@Test
	public void finishRentalWithTooHighLateChargeExceptionIsThrown() {
		final Rental withTooHighLateCharge = createPresentRental();
		final int justTooHighLateCharge = customer.getBalance() + 1;
		when(lateChargeCalculator.calculate(withTooHighLateCharge)).thenReturn(justTooHighLateCharge);

		expectedException.expect(ProcessingException.class);

		resources.client()
				.target("/rental/finishRental")
				.request()
				.post(Entity.entity(withTooHighLateCharge, MediaType.APPLICATION_JSON_TYPE));

		verify(rentalDAO).findById(withTooHighLateCharge.getId());
		verify(lateChargeCalculator).calculate(withTooHighLateCharge);
		verifyNoMoreInteractions(customerDAO, filmDAO, rentalDAO, lateChargeCalculator);
	}

	@Test
	public void finishRentalWithCorrectParametersRentalIsUpdated() {
		final Rental withCorrectParameters = copy(createPresentRental());
		withCorrectParameters.setReturnedOn(new Date());
		final int justEnoughLateCharge = customer.getBalance();
		when(lateChargeCalculator.calculate(withCorrectParameters)).thenReturn(justEnoughLateCharge);
		final int bonus = 1;
		when(bonusCalculator.calculate(withCorrectParameters)).thenReturn(bonus);

		final Rental updated = resources.client()
				.target("/rental/finishRental")
				.request()
				.post(Entity.entity(withCorrectParameters, MediaType.APPLICATION_JSON_TYPE))
				.readEntity(Rental.class);

		assertThat(updated).isEqualTo(withCorrectParameters);

		verify(rentalDAO).findById(withCorrectParameters.getId());
		verify(lateChargeCalculator).calculate(withCorrectParameters);
		verify(rentalDAO).updateReturnedOn(any(Rental.class), any(Date.class));
		verify(rentalDAO).updateLateCharge(withCorrectParameters, justEnoughLateCharge);
		verify(customerDAO).withdrawBalance(customer, justEnoughLateCharge);
		verify(customerDAO).addBonus(customer, bonus);
		verifyNoMoreInteractions(customerDAO, filmDAO, rentalDAO, lateChargeCalculator);
	}
}