package com.casumo.interview.videorental.core.price;

import com.casumo.interview.videorental.api.Rental;

public interface PriceCalculator {
	int calculate(Rental rental);
}
