package com.casumo.interview.videorental.core.price;

import com.casumo.interview.videorental.VideoRentalConfig;
import com.casumo.interview.videorental.api.Film;
import com.casumo.interview.videorental.api.Rental;
import com.google.common.base.Preconditions;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LateChargeCalculator implements PriceCalculator {
	private static final Logger logger = LoggerFactory.getLogger(LateChargeCalculator.class);

	@Override
	public int calculate(final Rental rental) {
		logger.trace("Calculating late charge. [rental={}]", rental);

		checkDates(rental);

		final int daysLate = getDaysLate(rental);
		if (daysLate <= 0) {
			logger.trace("Rental not late, no late charge needed. [rental={}]", rental);
			return 0;
		}

		final int daysPassed = getDaysPassed(rental);
		int lateCharge = 0;
		for (final Film filmRented: rental.getFilmsRented()) {
			Preconditions.checkNotNull(filmRented.getAge(), "film age has to be set");
			switch (filmRented.getAge()) {
				case NEW:
					lateCharge += VideoRentalConfig.PREMIUM_PRICE * daysLate;
					break;
				case NORMAL:
					if (daysPassed > VideoRentalConfig.NORMAL_DAYS) {
						lateCharge += VideoRentalConfig.BASIC_PRICE * daysLate;
					}
					break;
				case OLD:
					if (daysPassed > VideoRentalConfig.OLD_DAYS) {
						lateCharge += VideoRentalConfig.BASIC_PRICE * daysLate;
					}
					break;
				default:
					throw new IllegalArgumentException("Unknown age: " + filmRented.getAge());
			}
		}

		logger.trace("Rental is late. Late charge for rental calculated. [rental={};daysLate={};lateCharge={}]", rental, daysLate, lateCharge);

		return lateCharge;
	}

	private void checkDates(final Rental rental) {
		Preconditions.checkNotNull(rental.getRentedOn(), "rental.rentedOn has to be filled");
		Preconditions.checkNotNull(rental.getReturnedOn(), "rental.returnedOn has to be filled");
		Preconditions.checkArgument(rental.getRentedOn().before(rental.getReturnedOn()), "rental.rentedOn has to happen after rental.returnedOn");
	}

	private int getDaysLate(final Rental rental) {
		final int daysPassed = getDaysPassed(rental);
		return daysPassed - rental.getDaysRentedFor();
	}

	private int getDaysPassed(final Rental rental) {
		return Days.daysBetween(new DateTime(rental.getRentedOn()), new DateTime(rental.getReturnedOn())).getDays();
	}
}
