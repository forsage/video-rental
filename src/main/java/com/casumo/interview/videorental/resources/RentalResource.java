package com.casumo.interview.videorental.resources;

import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.casumo.interview.videorental.VideoRentalConfig;
import com.casumo.interview.videorental.api.Customer;
import com.casumo.interview.videorental.api.Film;
import com.casumo.interview.videorental.api.Rental;
import com.casumo.interview.videorental.core.bonus.BonusCalculator;
import com.casumo.interview.videorental.core.dao.CustomerDAO;
import com.casumo.interview.videorental.core.dao.FilmDAO;
import com.casumo.interview.videorental.core.dao.RentalDAO;
import com.casumo.interview.videorental.core.price.PriceCalculator;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Path("/rental")
@Produces(MediaType.APPLICATION_JSON)
public class RentalResource {
	private static final Logger logger = LoggerFactory.getLogger(RentalResource.class);
	private static final int MIN_RENTAL_DAYS = 3;

	private final FilmDAO filmDAO;
	private final CustomerDAO customerDAO;
	private final RentalDAO rentalDAO;
	private final PriceCalculator normalPriceCalculator;
	private final PriceCalculator lateChargeCalculator;
	private final PriceCalculator bonusCalculator;

	public RentalResource(final FilmDAO filmDAO, final CustomerDAO customerDAO, final RentalDAO rentalDAO, final PriceCalculator normalPriceCalculator,
			final PriceCalculator lateChargeCalculator, final PriceCalculator bonusCalculator) {
		this.filmDAO = filmDAO;
		this.customerDAO = customerDAO;
		this.rentalDAO = rentalDAO;
		this.normalPriceCalculator = normalPriceCalculator;
		this.lateChargeCalculator = lateChargeCalculator;
		this.bonusCalculator = bonusCalculator;
	}

	@GET
	@Path("/getByCustomerId")
	public List<Rental> getByCustomerId(@QueryParam("customerId") final int customerId) {
		logger.debug("Getting rentals by customer id. [customerId={}]", customerId);

		final Customer customer = findCustomerSafely(customerId);

		return rentalDAO.findByCustomer(customer);
	}

	@POST
	@Path("/startRental")
	public Rental startRental(final Rental rental) {
		logger.debug("Renting films. [rental={}]", rental);

		final Customer customer = findCustomerSafely(rental.getCustomer().getId());

		final List<Film> chosenFilmsToRent = checkAndGetFilmsToRent(Lists.newArrayList(Iterables.transform(rental.getFilmsRented(),
				new Function<Film, Integer>() {
					@Nullable
					@Override
					public Integer apply(final Film input) {
						return input.getId();
					}
				})));

		Preconditions.checkArgument(rental.getDaysRentedFor() >= MIN_RENTAL_DAYS,
				String.format("days rented for must not be lower than minimum [%s] days", MIN_RENTAL_DAYS));

		final Rental external = new Rental();

		external.setCustomer(customer);
		external.setFilmsRented(chosenFilmsToRent);
		external.setDaysRentedFor(rental.getDaysRentedFor());

		final int normalPrice = calculateAndCheckNormalPrice(customer, external);
		external.setNormalPrice(normalPrice);

		final Rental created = rentalDAO.create(external);
		rentalDAO.updateNormalPrice(created, normalPrice);
		customerDAO.withdrawBalance(customer, normalPrice);

		logger.debug("Films rented. [created={}]", created);

		return created;
	}

	private Customer findCustomerSafely(final int customerId) {
		final Optional<Customer> maybeCustomer = customerDAO.findById(customerId);

		if (!maybeCustomer.isPresent()) {
			throw new NotFoundException(String.format("no maybeCustomer with id [%s]", customerId));
		}

		return maybeCustomer.get();
	}

	private List<Film> checkAndGetFilmsToRent(final List<Integer> filmIds) {
		Preconditions.checkArgument(!Iterables.isEmpty(filmIds), "no film ids given to startRental");
		final List<Film> chosenFilmsToRent = Lists.newArrayList(Iterables.filter(filmDAO.findAvailable(), new Predicate<Film>() {
			@Override
			public boolean apply(final Film input) {
				return filmIds.contains(input.getId());
			}
		}));
		Preconditions.checkState(chosenFilmsToRent.size() == filmIds.size(), "not all the films are available to startRental");

		return chosenFilmsToRent;
	}

	private int calculateAndCheckNormalPrice(final Customer customer, final Rental rental) {
		final int normalPrice = normalPriceCalculator.calculate(rental);
		logger.trace("Normal price calculated for rental. [rental={};normalPrice={}]", rental, normalPrice);
		Preconditions.checkState(normalPrice <= customer.getBalance() - VideoRentalConfig.CUSTOMER_DEPOSIT,
				String.format("customer balance [%s] must reach deposit [%s] plus normal price [%s]",
						customer.getBalance(),
						VideoRentalConfig.CUSTOMER_DEPOSIT,
						normalPrice));

		return normalPrice;
	}

	@POST
	@Path("/finishRental")
	public Rental finishRental(final Rental rental) {
		Preconditions.checkNotNull(rental, "rental has to be set");
		Preconditions.checkNotNull(rental.getReturnedOn(), "rental.returnedOn has to be set");
		logger.debug("Finishing rental. [rental={}]", rental);

		final Rental found = findRentalSafely(rental.getId());
		Preconditions.checkState(found.getReturnedOn() == null, String.format("rental [%s] is already returned, cannot return twice", found));

		found.setReturnedOn(rental.getReturnedOn());
		calculateCheckAndApplyLateCharge(found);
		rentalDAO.updateReturnedOn(found, rental.getReturnedOn());
		calculateAndApplyBonus(found);

		logger.debug("Rental finished. [rental={}]", found);

		return found;
	}

	private Rental findRentalSafely(final int rentalId) {
		final Optional<Rental> maybeRental = rentalDAO.findById(rentalId);

		if (!maybeRental.isPresent()) {
			throw new NotFoundException(String.format("no rental with id [%s]", rentalId));
		}

		return maybeRental.get();
	}

	private void calculateCheckAndApplyLateCharge(final Rental rental) {
		final int lateCharge = lateChargeCalculator.calculate(rental);
		if (lateCharge > 0) {
			final int customerBalance = rental.getCustomer().getBalance();
			Preconditions.checkState(lateCharge <= customerBalance, String.format("late charge [%s] exceeds customer balance [%s]",
					lateCharge, customerBalance));

			logger.trace("Applying late charge. [rental={};lateCharge={}]", rental, lateCharge);
			rentalDAO.updateLateCharge(rental, lateCharge);
			customerDAO.withdrawBalance(rental.getCustomer(), lateCharge);
		}
	}

	private void calculateAndApplyBonus(final Rental rental) {
		final int bonus = bonusCalculator.calculate(rental);
		if (bonus > 0) {
			logger.trace("Adding bonus. [customer={};bonus={}]", rental.getCustomer(), bonus);
			customerDAO.addBonus(rental.getCustomer(), bonus);
		}
	}
}
