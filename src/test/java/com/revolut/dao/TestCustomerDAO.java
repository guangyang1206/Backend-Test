package com.revolut.dao;

import com.revolut.dao.factory.DAOFactory;
import com.revolut.model.Customer;
import com.revolut.util.BaseException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * @author Guang_Yang
 * @version V1.0
 * @Title: TestCustomerDAO
 * @Package com.revolut.dao
 * @Description: Test Customer Customer DAO
 */
public class TestCustomerDAO {

    private static final DAOFactory h2DaoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);

    /**
     * Initialize the database for testing
     */
    @BeforeClass
    public static void setup() {
        h2DaoFactory.populateTestData();
    }

    @After
    public void tearDown() {

    }

    /**
     * Test if all customers can be found
     * @throws BaseException
     */
    @Test
    public void testGetAllCustomers() throws BaseException {
        List<Customer> allCustomers = h2DaoFactory.getCustomerDAO().getAllCustomers();
        assertTrue(allCustomers.size() > 1);
        assertTrue(allCustomers.size() == 6);
    }

    /**
     * Test if customer can be found via ID
     * @throws BaseException
     */
    @Test
    public void testGetCustomerById() throws BaseException {
        Customer customer = h2DaoFactory.getCustomerDAO().getCustomerById(3L);
        assertTrue(customer.getCustomerName().equals("Carl"));
    }

    /**
     * Test if customer account cannot be found case
     * @throws BaseException
     */
    @Test
    public void testGetNonExistingCustomerById() throws BaseException {
        Customer customer = h2DaoFactory.getCustomerDAO().getCustomerById(500L);
        assertTrue(customer == null);
    }

    /**
     * Test if customer account cannot be found case
     * @throws BaseException
     */
    @Test
    public void testGetNonExistingCustomerByName() throws BaseException {
        Customer customer = h2DaoFactory.getCustomerDAO().getCustomerByName("abcdeftg");
        assertTrue(customer == null);
    }

    /**
     * Test if customer account can be created successfully case
     * @throws BaseException
     */
    @Test
    public void testCreateCustomer() throws BaseException {
        Customer customer = new Customer("Green", "Green@gmail.com", "77788889999");
        long id = h2DaoFactory.getCustomerDAO().insertCustomer(customer);
        Customer uAfterInsert = h2DaoFactory.getCustomerDAO().getCustomerById(id);
        assertTrue(uAfterInsert.getCustomerName().equals("Green"));
        assertTrue(uAfterInsert.getEmailAddress().equals("Green@gmail.com"));
        assertTrue(uAfterInsert.getPhoneNumber().equals("77788889999"));
    }

    /**
     * Test if customer account can be updated successfully case
     * @throws BaseException
     */
    @Test
    public void testUpdateCustomer() throws BaseException {
        Customer customer = new Customer(1L, "Test", "Test@gmail.com", "99999999999");
        int rowCount = h2DaoFactory.getCustomerDAO().updateCustomer(1L, customer);
        assertTrue(rowCount == 1);
        assertTrue(h2DaoFactory.getCustomerDAO().getCustomerById(1L).getEmailAddress().equals("Test@gmail.com"));
    }

    /**
     * Test if customer account cannot be updated case
     * @throws BaseException
     */
    @Test
    public void testUpdateNonExistingCustomer() throws BaseException {
        Customer customer = new Customer(1L, "Test", "Test@gmail.com", "99999999999");
        int rowCount = h2DaoFactory.getCustomerDAO().updateCustomer(500L, customer);
        assertTrue(rowCount == 0);
    }

    /**
     * Test if customer account can be deleted case
     * @throws BaseException
     */
    @Test
    public void testDeleteCustomer() throws BaseException {
        int rowCount = h2DaoFactory.getCustomerDAO().deleteCustomer(1L);
        assertTrue(rowCount == 1);
        assertTrue(h2DaoFactory.getCustomerDAO().getCustomerById(1L) == null);
    }

    /**
     * Test if customer account cannot be deleted case
     * @throws BaseException
     */
    @Test
    public void testDeleteNonExistingCustomer() throws BaseException {
        int rowCount = h2DaoFactory.getCustomerDAO().deleteCustomer(500L);
        // assert no row(user) deleted
        assertTrue(rowCount == 0);

    }
}
