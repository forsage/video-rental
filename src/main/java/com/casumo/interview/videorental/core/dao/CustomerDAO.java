package com.casumo.interview.videorental.core.dao;

import com.casumo.interview.videorental.api.Customer;
import com.google.common.base.Optional;

import java.util.List;

public interface CustomerDAO {
	Customer create(Customer customer);

	Optional<Customer> findById(int id);

	List<Customer> findAll();

	void withdrawBalance(Customer customer, int amount);

	void addBonus(Customer customer, int bonus);

	void delete(Customer customer);
}
