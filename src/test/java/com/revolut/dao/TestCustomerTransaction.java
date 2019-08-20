package com.revolut.dao;

import com.revolut.dao.factory.DAOFactory;
import com.revolut.dao.factory.H2DAOFactory;
import com.revolut.model.CustomerAccount;
import com.revolut.model.CustomerTransaction;
import com.revolut.util.BaseException;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import static junit.framework.TestCase.assertTrue;

/**
 * @author Guang_Yang
 * @version V1.0
 * @Title: TestCustomerTransaction
 * @Package com.revolut.dao
 * @Description: Test Customer Transaction
 */
public class TestCustomerTransaction {

    private static Logger log = Logger.getLogger(TestCustomerTransaction.class);
    private static final DAOFactory h2DaoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);
    private static final int THREADS_COUNT = 100;

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
     * Test two customer transfer money with same currency
     * @throws BaseException
     */
    @Test
    public void testCustomerAccountSingleThreadSameCcyTransfer() throws BaseException {

        final CustomerAccountDAO accountDAO = h2DaoFactory.getCustomerAccountDAO();

        BigDecimal transferAmount = new BigDecimal(50.01234).setScale(4, RoundingMode.HALF_EVEN);
        CustomerTransaction transaction = new CustomerTransaction("EUR", transferAmount, 3L, 4L);

        long startTime = System.currentTimeMillis();
        accountDAO.transferCustomerAccountBalance(transaction);
        long endTime = System.currentTimeMillis();
        log.info("TransferCustomerAccountBalance finished, time taken: " + (endTime - startTime) + "ms");

        CustomerAccount accountFrom = accountDAO.getCustomerAccountById(3L);
        CustomerAccount accountTo = accountDAO.getCustomerAccountById(4L);

        log.info("CustomerAccount From: " + accountFrom);
        log.info("CustomerAccount From: " + accountTo);

        assertTrue(accountFrom.getBalance().compareTo(new BigDecimal(2949.9877).setScale(4, RoundingMode.HALF_EVEN)) == 0);
        assertTrue(accountTo.getBalance().equals(new BigDecimal(4050.0123).setScale(4, RoundingMode.HALF_EVEN)));

    }

    /**
     * Test two customer transfer money with same currency using multi-thread
     * Total transfer will be 2000 CNY while the balance is only 1000, half of the transfer will fail
     * @throws BaseException
     */
    @Test
    public void testCustomerAccountMultiThreadedTransfer() throws InterruptedException, BaseException {

        final CustomerAccountDAO accountDAO = h2DaoFactory.getCustomerAccountDAO();
        final CountDownLatch latch = new CountDownLatch(THREADS_COUNT);

        for (int i = 0; i < THREADS_COUNT; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        CustomerTransaction transaction = new CustomerTransaction("CNY",
                                new BigDecimal(20).setScale(4, RoundingMode.HALF_EVEN), 1L, 2L);
                        accountDAO.transferCustomerAccountBalance(transaction);
                    } catch (Exception e) {
                        log.error("Error occurred during transfer ", e);
                    } finally {
                        latch.countDown();
                    }
                }
            }).start();
        }

        latch.await();

        CustomerAccount accountFrom = accountDAO.getCustomerAccountById(1L);
        CustomerAccount accountTo = accountDAO.getCustomerAccountById(2L);

        log.info("CustomerAccount From: " + accountFrom);
        log.info("CustomerAccount From: " + accountTo);

        assertTrue(accountFrom.getBalance().equals(new BigDecimal(0).setScale(4, RoundingMode.HALF_EVEN)));
        assertTrue(accountTo.getBalance().equals(new BigDecimal(3000).setScale(4, RoundingMode.HALF_EVEN)));
    }

    /**
     * Test two customer transfer money fail while one of the account is locked
     * Total transfer will be 2000 GBP while the balance is only 1000, half of the transfer will fail
     * @throws BaseException
     */
    @Test
    public void testTransferFailOnDBLock() throws BaseException, SQLException {
        final String SQL_LOCK_ACC = "SELECT * FROM CustomerAccount WHERE CustomerAccountId = 5 FOR UPDATE";
        Connection conn = null;
        PreparedStatement lockStmt = null;
        ResultSet rs = null;
        CustomerAccount fromCustomerAccount = null;

        try {
            conn = H2DAOFactory.getConnection();
            conn.setAutoCommit(false);
            lockStmt = conn.prepareStatement(SQL_LOCK_ACC);
            rs = lockStmt.executeQuery();
            if (rs.next()) {
                fromCustomerAccount = new CustomerAccount(rs.getLong("CustomerAccountId"), rs.getString("CustomerName"),
                        rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
                log.info("Locked CustomerAccount: " + fromCustomerAccount);
            }
            if (fromCustomerAccount == null) {
                throw new BaseException("Locking error during test, SQL = " + SQL_LOCK_ACC);
            }
            BigDecimal transferAmount = new BigDecimal(50).setScale(4, RoundingMode.HALF_EVEN);
            CustomerTransaction transaction = new CustomerTransaction("GBP", transferAmount, 6L, 5L);
            h2DaoFactory.getCustomerAccountDAO().transferCustomerAccountBalance(transaction);
            conn.commit();
        } catch (Exception e) {
            log.error("Exception occurred, initiate a rollback");
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException re) {
                log.error("Fail to rollback transaction", re);
            }
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(lockStmt);
        }

        BigDecimal originalBalance = new BigDecimal(5000).setScale(4, RoundingMode.HALF_EVEN);
        assertTrue(h2DaoFactory.getCustomerAccountDAO().getCustomerAccountById(6L).getBalance().equals(originalBalance));
        assertTrue(h2DaoFactory.getCustomerAccountDAO().getCustomerAccountById(5L).getBalance().equals(originalBalance));
    }
}
