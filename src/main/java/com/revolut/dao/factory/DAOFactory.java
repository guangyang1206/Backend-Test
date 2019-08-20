package com.revolut.dao.factory;

import com.revolut.dao.CustomerAccountDAO;
import com.revolut.dao.CustomerDAO;

/**
 * @author Guang_Yang
 * @version V1.0
 * @Title: DAOFactory
 * @Package com.revolut.dao.factory
 * @Description: Data Access Object Factory Abstract Class
 */
public abstract class DAOFactory {

	public static final int H2 = 1;

	public abstract CustomerDAO getCustomerDAO();

	public abstract CustomerAccountDAO getCustomerAccountDAO();

	public abstract void populateTestData();

	public static DAOFactory getDAOFactory(int factoryCode) {

		switch (factoryCode) {
		case H2:
			return new H2DAOFactory();
		default:
			// by default using H2 in memory database
			return new H2DAOFactory();
		}
	}
}
