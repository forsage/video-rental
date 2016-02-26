package com.casumo.interview.videorental.resources;

import com.casumo.interview.videorental.api.Customer;
import com.casumo.interview.videorental.core.dao.CustomerDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/customer")
@Produces(MediaType.APPLICATION_JSON)
public class CustomerResource {
	private static final Logger logger = LoggerFactory.getLogger(CustomerResource.class);

	private final CustomerDAO customerDAO;

	public CustomerResource(final CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@GET
	@Path("/getAll")
	public List<Customer> getAll() {
		logger.debug("Getting all the customers from the inventory.");

		return customerDAO.findAll();
	}

	@POST
	@Path("/create")
	public Customer create(final Customer customer) {
		logger.debug("Adding a customer to the inventory. [customer={}]", customer);

		return customerDAO.create(customer);
	}
}
