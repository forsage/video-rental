package com.casumo.interview.videorental;

import com.casumo.interview.videorental.core.bonus.BonusCalculator;
import com.casumo.interview.videorental.core.dao.*;
import com.casumo.interview.videorental.health.CustomerDAOHealthCheck;
import com.casumo.interview.videorental.health.FilmDAOHealthCheck;
import com.casumo.interview.videorental.health.RentalDAOHealthCheck;
import com.casumo.interview.videorental.resources.CustomerResource;
import com.casumo.interview.videorental.resources.FilmResource;
import com.casumo.interview.videorental.core.price.LateChargeCalculator;
import com.casumo.interview.videorental.core.price.NormalPriceCalculator;
import com.casumo.interview.videorental.core.price.PriceCalculator;
import com.casumo.interview.videorental.resources.RentalResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class VideoRentalApplication extends Application<VideoRentalConfig> {

	private final Sequence sequence = new Sequence();
	private CustomerDAO customerDAO;
	private RentalDAO rentalDAO;
	private FilmDAO filmDAO;
	private PriceCalculator normalPriceCalculator;
	private PriceCalculator lateChargeCalculator;
	private PriceCalculator bonusCalculator;

	public static void main(final String[] args) throws Exception {
		new VideoRentalApplication().run(args);
	}

	@Override
	public void run(final VideoRentalConfig config, final Environment environment) throws Exception {
		initServices();

		initHealthChecks(environment);

		initResources(environment);
	}

	private void initServices() {
		customerDAO = new CustomerStore(sequence);
		rentalDAO = new RentalStore(sequence);
		filmDAO = new FilmStore(sequence, rentalDAO);
		normalPriceCalculator = new NormalPriceCalculator();
		lateChargeCalculator = new LateChargeCalculator();
		bonusCalculator = new BonusCalculator();
	}

	private void initHealthChecks(final Environment environment) {
		final FilmDAOHealthCheck filmDAOHealthCheck = new FilmDAOHealthCheck(filmDAO);
		environment.healthChecks().register("filmDAO", filmDAOHealthCheck);

		final CustomerDAOHealthCheck customerDAOHealthCheck = new CustomerDAOHealthCheck(customerDAO);
		environment.healthChecks().register("customerDAO", customerDAOHealthCheck);

		final RentalDAOHealthCheck rentalDAOHealthCheck = new RentalDAOHealthCheck(rentalDAO);
		environment.healthChecks().register("rentalDAO", rentalDAOHealthCheck);
	}

	private void initResources(final Environment environment) {
		environment.jersey().register(new FilmResource(filmDAO));
		environment.jersey().register(new CustomerResource(customerDAO));
		environment.jersey().register(new RentalResource(filmDAO, customerDAO, rentalDAO, normalPriceCalculator, lateChargeCalculator, bonusCalculator));
	}
}
