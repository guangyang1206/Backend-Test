package com.revolut.dao;

import com.revolut.model.Customer;
import com.revolut.util.BaseException;

import java.util.List;

/**
 * @author Guang_Yang
 * @version V1.0
 * @Title: CustomerDAO
 * @Package com.revolut.dao
 * @Description: Customer Data Access Object Interface
 */
public interface CustomerDAO {
	
	List<Customer> getAllCustomers() throws BaseException;

	Customer getCustomerById(Long customerId) throws BaseException;

	Customer getCustomerByName(String customerName) throws BaseException;

	Long insertCustomer(Customer customer) throws BaseException;

	int updateCustomer(Long customerId, Customer customer) throws BaseException;

	int deleteCustomer(Long customerId) throws BaseException;

}
