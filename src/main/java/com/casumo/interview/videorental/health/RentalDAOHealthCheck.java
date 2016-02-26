package com.casumo.interview.videorental.health;

import com.casumo.interview.videorental.core.dao.RentalDAO;
import com.codahale.metrics.health.HealthCheck;

public class RentalDAOHealthCheck extends HealthCheck {
	private final RentalDAO rentalDAO;

	public RentalDAOHealthCheck(final RentalDAO rentalDAO) {
		this.rentalDAO = rentalDAO;
	}

	@Override
	protected Result check() throws Exception {
		if (rentalDAO.findAll().size() == 0) {
			return Result.healthy();
		}

		return Result.unhealthy("There is at least one rental on startup, shouldn't happen.");
	}
}
