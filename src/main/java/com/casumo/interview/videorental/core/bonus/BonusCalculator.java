package com.casumo.interview.videorental.core.bonus;

import com.casumo.interview.videorental.VideoRentalConfig;
import com.casumo.interview.videorental.api.Film;
import com.casumo.interview.videorental.api.Rental;
import com.casumo.interview.videorental.core.price.PriceCalculator;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BonusCalculator implements PriceCalculator {
	private static final Logger logger = LoggerFactory.getLogger(BonusCalculator.class);

	public int calculate(final Rental rental) {
		logger.trace("Calculating bonus. [rental={}]", rental);
		int bonus = 0;
		for (final Film filmRented: rental.getFilmsRented()) {
			Preconditions.checkNotNull(filmRented.getAge(), "film age has to be filled");
			switch (filmRented.getAge()) {
				case NEW:
					bonus += VideoRentalConfig.PREMIUM_BONUS;
					break;
				case NORMAL:
				case OLD:
					bonus += VideoRentalConfig.BASIC_BONUS;
					break;
				default:
					throw new IllegalArgumentException("Unknown age: " + filmRented.getAge());
			}
		}

		logger.trace("Bonus calculated. [rental={};bonus={}]");

		return bonus;
	}
}
