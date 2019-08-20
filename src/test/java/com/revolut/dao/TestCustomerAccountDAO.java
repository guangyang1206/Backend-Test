package com.revolut.dao;

import com.revolut.dao.factory.DAOFactory;
import com.revolut.model.CustomerAccount;
import com.revolut.util.BaseException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * @author Guang_Yang
 * @version V1.0
 * @Title: TestCustomerCustomerAccountDAO
 * @Package com.revolut.dao
 * @Description: Test Customer Customer Account DAO
 */
public class TestCustomerAccountDAO {

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
     * Test if all customer accounts can be found
     * @throws BaseException
     */
    @Test
    public void testGetAllCustomerAccounts() throws BaseException {
        List<CustomerAccount> allCustomerAccounts = h2DaoFactory.getCustomerAccountDAO().getAllCustomerAccounts();
        int accountNum = 8;
        assertEquals(accountNum, allCustomerAccounts.size());
    }

    /**
     * Test if customer account can be found via ID
     * @throws BaseException
     */
    @Test
    public void testGetCustomerAccountById() throws BaseException {
        CustomerAccount customerAccount = h2DaoFactory.getCustomerAccountDAO().getCustomerAccountById(1L);
        assertTrue(customerAccount.getCustomerName().equals("Allen"));
    }

    /**
     * Test if customer account cannot be found case
     * @throws BaseException
     */
    @Test
    public void testGetNonExistingAccById() throws BaseException {
        CustomerAccount customerAccount = h2DaoFactory.getCustomerAccountDAO().getCustomerAccountById(100L);
        assertTrue(customerAccount == null);
    }

    /**
     * Test if customer account can be created successfully case
     * @throws BaseException
     */
    @Test
    public void testCreateCustomerAccount() throws BaseException {
        BigDecimal balance = new BigDecimal(10).setScale(4, RoundingMode.HALF_EVEN);
        CustomerAccount customerAccount = new CustomerAccount("Test", balance, "GBP");
        long aid = h2DaoFactory.getCustomerAccountDAO().createCustomerAccount(customerAccount);
        CustomerAccount afterCreation = h2DaoFactory.getCustomerAccountDAO().getCustomerAccountById(aid);
        assertTrue(afterCreation.getCustomerName().equals("Test"));
        assertTrue(afterCreation.getBalance().equals(balance));
        assertTrue(afterCreation.getCurrencyCode().equals("GBP"));
    }

    /**
     * Test if customer account can be deleted successfully case
     * @throws BaseException
     */
    @Test
    public void testDeleteCustomerAccount() throws BaseException {
        int rowCount = h2DaoFactory.getCustomerAccountDAO().deleteCustomerAccountById(2L);
        assertTrue(rowCount == 1);
        assertTrue(h2DaoFactory.getCustomerAccountDAO().getCustomerAccountById(2L) == null);
    }

    /**
     * Test if customer account cannot be deleted case
     * @throws BaseException
     */
    @Test
    public void testDeleteNonExistingCustomerAccount() throws BaseException {
        int rowCount = h2DaoFactory.getCustomerAccountDAO().deleteCustomerAccountById(500L);
        // assert no row(user) deleted
        assertTrue(rowCount == 0);

    }

    /**
     * Test if customer account balance can be updated successfully case
     * @throws BaseException
     */
    @Test
    public void testUpdateCustomerAccountBalanceSufficientFund() throws BaseException {

        BigDecimal deltaDeposit = new BigDecimal(50).setScale(4, RoundingMode.HALF_EVEN);
        BigDecimal afterDeposit = new BigDecimal(1050).setScale(4, RoundingMode.HALF_EVEN);
        int rowsUpdated = h2DaoFactory.getCustomerAccountDAO().updateCustomerAccountBalance(1L, deltaDeposit);
        assertTrue(rowsUpdated == 1);
        assertTrue(h2DaoFactory.getCustomerAccountDAO().getCustomerAccountById(1L).getBalance().equals(afterDeposit));
        BigDecimal deltaWithDraw = new BigDecimal(-50).setScale(4, RoundingMode.HALF_EVEN);
        BigDecimal afterWithDraw = new BigDecimal(1000).setScale(4, RoundingMode.HALF_EVEN);
        int rowsUpdatedW = h2DaoFactory.getCustomerAccountDAO().updateCustomerAccountBalance(1L, deltaWithDraw);
        assertTrue(rowsUpdatedW == 1);
        assertTrue(h2DaoFactory.getCustomerAccountDAO().getCustomerAccountById(1L).getBalance().equals(afterWithDraw));

    }

    /**
     * Test if customer account balance not enough case
     * @throws BaseException
     */
    @Test(expected = BaseException.class)
    public void testUpdateCustomerAccountBalanceNotEnoughFund() throws BaseException {
        BigDecimal deltaWithDraw = new BigDecimal(-50000).setScale(4, RoundingMode.HALF_EVEN);
        int rowsUpdatedW = h2DaoFactory.getCustomerAccountDAO().updateCustomerAccountBalance(1L, deltaWithDraw);
        assertTrue(rowsUpdatedW == 0);

    }
}
