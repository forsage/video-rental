package com.casumo.interview.videorental.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class Rental {
	private int id;
	private Customer customer;
	private List<Film> filmsRented = Lists.newArrayList();
	private int daysRentedFor;
	private Date rentedOn;
	private Date returnedOn;
	private int normalPrice;
	private int lateCharge;

	public Rental() {
	}

	@JsonProperty
	public int getId() {
		return id;
	}

	@JsonProperty
	public void setId(int id) {
		this.id = id;
	}

	@JsonProperty
	public Customer getCustomer() {
		return customer;
	}

	@JsonProperty
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@JsonProperty
	public List<Film> getFilmsRented() {
		return filmsRented;
	}

	@JsonProperty
	public void setFilmsRented(List<Film> filmsRented) {
		this.filmsRented = filmsRented;
	}

	@JsonProperty
	public int getDaysRentedFor() {
		return daysRentedFor;
	}

	@JsonProperty
	public void setDaysRentedFor(int daysRentedFor) {
		this.daysRentedFor = daysRentedFor;
	}

	@JsonProperty
	public Date getRentedOn() {
		return rentedOn;
	}

	@JsonProperty
	public void setRentedOn(Date rentedOn) {
		this.rentedOn = rentedOn;
	}

	@JsonProperty
	public Date getReturnedOn() {
		return returnedOn;
	}

	@JsonProperty
	public void setReturnedOn(final Date returnedOn) {
		this.returnedOn = returnedOn;
	}

	@JsonProperty
	public int getNormalPrice() {
		return normalPrice;
	}

	@JsonProperty
	public void setNormalPrice(final int normalPrice) {
		this.normalPrice = normalPrice;
	}

	@JsonProperty
	public int getLateCharge() {
		return lateCharge;
	}

	@JsonProperty
	public void setLateCharge(final int lateCharge) {
		this.lateCharge = lateCharge;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final Rental rental = (Rental) o;

		return id == rental.id;

	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		// @formatter:off
		return MoreObjects.toStringHelper(this).omitNullValues()
				.add("id", id)
				.add("customer", customer)
				.add("filmsRented", filmsRented)
				.add("daysRentedFor", daysRentedFor)
				.add("rentedOn", rentedOn)
				.add("returnedOn", returnedOn)
				.add("normalPrice", normalPrice)
				.add("lateCharge", lateCharge)
				.toString();
		// @formatter:on
	}
}
