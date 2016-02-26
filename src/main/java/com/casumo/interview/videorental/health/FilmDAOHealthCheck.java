package com.casumo.interview.videorental.health;

import com.casumo.interview.videorental.core.dao.FilmDAO;
import com.codahale.metrics.health.HealthCheck;

public class FilmDAOHealthCheck extends HealthCheck {
	private final FilmDAO filmDAO;

	public FilmDAOHealthCheck(final FilmDAO filmDAO) {
		this.filmDAO = filmDAO;
	}

	@Override
	protected Result check() throws Exception {
		if (filmDAO.findAll().size() == 0) {
			return Result.healthy();
		}

		return Result.unhealthy("There is at least one film on startup, shouldn't happen.");
	}
}
