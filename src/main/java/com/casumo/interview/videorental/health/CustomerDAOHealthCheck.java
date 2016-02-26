package com.casumo.interview.videorental.health;

import com.casumo.interview.videorental.core.dao.CustomerDAO;
import com.casumo.interview.videorental.core.dao.FilmDAO;
import com.codahale.metrics.health.HealthCheck;

public class CustomerDAOHealthCheck extends HealthCheck {
	private final CustomerDAO customerDAO;

	public CustomerDAOHealthCheck(final CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Override
	protected Result check() throws Exception {
		if (customerDAO.findAll().size() == 0) {
			return Result.healthy();
		}

		return Result.unhealthy("There is at least one customer on startup, shouldn't happen.");
	}
}
