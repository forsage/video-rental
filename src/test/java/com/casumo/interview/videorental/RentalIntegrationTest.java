package com.casumo.interview.videorental;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import com.casumo.interview.videorental.api.Customer;
import com.casumo.interview.videorental.api.Film;
import com.casumo.interview.videorental.api.Rental;
import com.google.common.collect.Lists;

public class RentalIntegrationTest extends IntegrationTestActions {

	@Test
	public void testRentalResource() {
		final Customer customer = createAndCheckCustomer();
		final Film film = createAndCheckFilm();
		final DateTime today = new DateTime().withTimeAtStartOfDay();
		final DateTime future = today.plusDays(7).withTimeAtStartOfDay();

		final Rental template = new Rental();
		template.setCustomer(customer);
		template.setFilmsRented(Lists.newArrayList(film));
		template.setDaysRentedFor(4);

		final Rental started = startRental(template);

		final Customer customerAfterStart = getCustomer();

		assertThat(started.getId()).isGreaterThan(0);
		assertThat(started.getCustomer()).isEqualTo(customer);
		assertThat(started.getFilmsRented().size()).isEqualTo(1);
		assertThat(started.getFilmsRented().get(0)).isEqualTo(film);
		assertThat(started.getRentedOn()).isAfter(today.toDate());
		assertThat(started.getNormalPrice()).isEqualTo(160);
		assertThat(customerAfterStart.getBalance()).isEqualTo(140);

		final List<Rental> byCustomerId = getRentalsByCustomerId(customer);

		assertThat(byCustomerId.size()).isEqualTo(1);
		assertThat(byCustomerId.get(0)).isEqualTo(started);

		started.setReturnedOn(future.toDate());

		final Rental finished = finishRental(started);

		assertThat(finished.getReturnedOn()).isAfter(finished.getRentedOn());
		assertThat(finished.getNormalPrice()).isEqualTo(started.getNormalPrice());
		assertThat(finished.getLateCharge()).isEqualTo(80);

		final Customer customerAfterFinish = getCustomer();

		assertThat(customerAfterFinish.getId()).isEqualTo(customerAfterStart.getId());
		assertThat(customerAfterFinish.getBalance()).isEqualTo(60);
		assertThat(customerAfterFinish.getBonus()).isEqualTo(2);
	}
}
