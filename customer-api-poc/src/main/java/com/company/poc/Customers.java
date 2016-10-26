package com.company.poc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Customers implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5989506484851718292L;
	
	private List<Customer> customers = new ArrayList<Customer>();

	public List<Customer> getCustomers() {
		return customers;
	}

	public void addCustomer(Customer customer) {
		this.customers.add(customer);
	}

	@Override
	public String toString() {
		return "Customers [customers=" + customers + "]";
	}	

}
