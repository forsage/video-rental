package com.casumo.interview.videorental.core.price;

import com.casumo.interview.videorental.VideoRentalConfig;
import com.casumo.interview.videorental.api.Film;
import com.casumo.interview.videorental.api.Rental;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NormalPriceCalculator implements PriceCalculator {
	private static final Logger logger = LoggerFactory.getLogger(NormalPriceCalculator.class);

	@Override
	public int calculate(final Rental rental) {
		logger.trace("Calculating normal price for rental. [rental={}]", rental);

		final int daysRentedFor = rental.getDaysRentedFor();
		int normalPrice = 0;
		for (final Film filmRented: rental.getFilmsRented()) {
			Preconditions.checkNotNull(filmRented.getAge(), "film age has to be filled");
			switch (filmRented.getAge()) {
				case NEW:
					normalPrice += VideoRentalConfig.PREMIUM_PRICE * daysRentedFor;
					break;
				case NORMAL:
					normalPrice += VideoRentalConfig.BASIC_PRICE * Math.max(daysRentedFor - VideoRentalConfig.NORMAL_DAYS + 1, 1);
					break;
				case OLD:
					normalPrice += VideoRentalConfig.BASIC_PRICE * Math.max(daysRentedFor - VideoRentalConfig.OLD_DAYS + 1, 1);
					break;
				default:
					throw new IllegalArgumentException("Unknown age: " + filmRented.getAge());
			}
		}

		logger.trace("Normal price for rental calculated. [rental={};normalPrice={}]", rental, normalPrice);

		return normalPrice;
	}
}
