package com.revolut.dao;

import com.revolut.dao.factory.H2DAOFactory;
import com.revolut.model.CustomerAccount;
import com.revolut.model.CustomerTransaction;
import com.revolut.util.BaseException;
import com.revolut.util.MoneyUtils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Guang_Yang
 * @version V1.0
 * @Title: CustomerAccountDAOImpl
 * @Package com.revolut.dao
 * @Description: Customer Account Data Access Object Implementation Class
 */
public class CustomerAccountDAOImpl implements CustomerAccountDao {

    private static Logger log = Logger.getLogger(CustomerAccountDAOImpl.class);
    private final static String SQL_GET_ACC_BY_ID = "SELECT * FROM CustomerAccount WHERE AccountId = ? ";
    private final static String SQL_LOCK_ACC_BY_ID = "SELECT * FROM CustomerAccount WHERE AccountId = ? FOR UPDATE";
    private final static String SQL_CREATE_ACC = "INSERT INTO CustomerAccount (CustomerName, Balance, CurrencyCode) VALUES (?, ?, ?)";
    private final static String SQL_UPDATE_ACC_BALANCE = "UPDATE CustomerAccount SET Balance = ? WHERE AccountId = ? ";
    private final static String SQL_GET_ALL_ACC = "SELECT * FROM CustomerAccount";
    private final static String SQL_DELETE_ACC_BY_ID = "DELETE FROM CustomerAccount WHERE AccountId = ?";

    /**
     * Get all CustomerAccounts.
     */
    public List<CustomerAccount> getAllCustomerAccounts() throws BaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<CustomerAccount> allCustomerAccounts = new ArrayList<CustomerAccount>();
        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_GET_ALL_ACC);
            rs = stmt.executeQuery();
            while (rs.next()) {
                CustomerAccount acc = new CustomerAccount(rs.getLong("AccountId"), rs.getString("CustomerName"),
                        rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
                if (log.isDebugEnabled())
                    log.debug("getAllCustomerAccounts(): Get  CustomerAccount " + acc);
                allCustomerAccounts.add(acc);
            }
            return allCustomerAccounts;
        } catch (SQLException e) {
            throw new BaseException("getCustomerAccountById(): Error reading CustomerAccount data", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }

    /**
     * Get CustomerAccount by AccountId
     */
    public CustomerAccount getCustomerAccountById(Long accountId) throws BaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        CustomerAccount acc = null;
        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_GET_ACC_BY_ID);
            stmt.setLong(1, accountId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                acc = new CustomerAccount(rs.getLong("AccountId"), rs.getString("CustomerName"), rs.getBigDecimal("Balance"),
                        rs.getString("CurrencyCode"));
                if (log.isDebugEnabled())
                    log.debug("Retrieve CustomerAccount By Id: " + acc);
            }
            return acc;
        } catch (SQLException e) {
            throw new BaseException("getCustomerAccountById(): Error reading CustomerAccount data", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }

    /**
     * Create CustomerAccount
     */
    public long createCustomerAccount(CustomerAccount customerAccount) throws BaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_CREATE_ACC);
            stmt.setString(1, customerAccount.getCustomerName());
            stmt.setBigDecimal(2, customerAccount.getBalance());
            stmt.setString(3, customerAccount.getCurrencyCode());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                log.error("createCustomerAccount(): Creating CustomerAccount failed, no rows affected.");
                throw new BaseException("CustomerAccount Cannot be created");
            }
            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            } else {
                log.error("Creating CustomerAccount failed, no ID obtained.");
                throw new BaseException("CustomerAccount Cannot be created");
            }
        } catch (SQLException e) {
            log.error("Error Inserting CustomerAccount  " + customerAccount);
            throw new BaseException("createCustomerAccount(): Error creating user CustomerAccount " + customerAccount, e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, generatedKeys);
        }
    }

    /**
     * Delete CustomerAccount by AccountId
     */
    public int deleteCustomerAccountById(Long AccountId) throws BaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_DELETE_ACC_BY_ID);
            stmt.setLong(1, AccountId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new BaseException("deleteCustomerAccountById(): Error deleting user CustomerAccount Id " + AccountId, e);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(stmt);
        }
    }

    /**
     * Update CustomerAccount balance
     */
    public int updateCustomerAccountBalance(Long AccountId, BigDecimal deltaAmount) throws BaseException {
        Connection conn = null;
        PreparedStatement lockStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;
        CustomerAccount targetCustomerAccount = null;
        int updateCount = -1;
        try {
            conn = H2DAOFactory.getConnection();
            conn.setAutoCommit(false);
            // lock CustomerAccount for writing:
            lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
            lockStmt.setLong(1, AccountId);
            rs = lockStmt.executeQuery();
            if (rs.next()) {
                targetCustomerAccount = new CustomerAccount(rs.getLong("AccountId"), rs.getString("CustomerName"),
                        rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
                if (log.isDebugEnabled())
                    log.debug("updateCustomerAccountBalance from CustomerAccount: " + targetCustomerAccount);
            }

            if (targetCustomerAccount == null) {
                throw new BaseException("updateCustomerAccountBalance(): fail to lock CustomerAccount : " + AccountId);
            }
            /* update CustomerAccount upon success locking */
            BigDecimal balance = targetCustomerAccount.getBalance().add(deltaAmount);
            if (balance.compareTo(MoneyUtils.zeroAmount) < 0) {
                throw new BaseException("Not sufficient Fund for CustomerAccount: " + AccountId);
            }

            updateStmt = conn.prepareStatement(SQL_UPDATE_ACC_BALANCE);
            updateStmt.setBigDecimal(1, balance);
            updateStmt.setLong(2, AccountId);
            updateCount = updateStmt.executeUpdate();
            conn.commit();
            if (log.isDebugEnabled())
                log.debug("New Balance after Update: " + targetCustomerAccount);
            return updateCount;
        } catch (SQLException se) {
            // rollback CustomerTransaction if exception occurs
            log.error("updateCustomerAccountBalance(): User CustomerTransaction Failed, rollback initiated for: " + AccountId, se);
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException re) {
                throw new BaseException("Fail to rollback CustomerTransaction", re);
            }
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(lockStmt);
            DbUtils.closeQuietly(updateStmt);
        }
        return updateCount;
    }

    /**
     * Transfer balance between two CustomerAccounts.
     */
    public int transferCustomerAccountBalance(CustomerTransaction customerTransaction) throws BaseException {
        int result = -1;
        Connection conn = null;
        PreparedStatement lockStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;
        CustomerAccount fromCustomerAccount = null;
        CustomerAccount toCustomerAccount = null;

        try {
            conn = H2DAOFactory.getConnection();
            conn.setAutoCommit(false);
            // lock the credit and debit CustomerAccount for writing:
            lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
            lockStmt.setLong(1, customerTransaction.getFromAccountId());
            rs = lockStmt.executeQuery();
            if (rs.next()) {
                fromCustomerAccount = new CustomerAccount(rs.getLong("AccountId"), rs.getString("CustomerName"),
                        rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
                if (log.isDebugEnabled())
                    log.debug("transferCustomerAccountBalance from CustomerAccount: " + fromCustomerAccount);
            }
            lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
            lockStmt.setLong(1, customerTransaction.getToAccountId());
            rs = lockStmt.executeQuery();
            if (rs.next()) {
                toCustomerAccount = new CustomerAccount(rs.getLong("AccountId"), rs.getString("CustomerName"), rs.getBigDecimal("Balance"),
                        rs.getString("CurrencyCode"));
                if (log.isDebugEnabled())
                    log.debug("transferCustomerAccountBalance to CustomerAccount: " + toCustomerAccount);
            }

            // check locking status
            if (fromCustomerAccount == null || toCustomerAccount == null) {
                throw new BaseException("Fail to lock both CustomerAccounts for write");
            }

            // check CustomerTransaction currency
            if (!fromCustomerAccount.getCurrencyCode().equals(customerTransaction.getCurrencyCode())) {
                throw new BaseException("Fail to transfer Fund, CustomerTransaction ccy are different from source/destination");
            }

            // check ccy is the same for both CustomerAccounts
            if (!fromCustomerAccount.getCurrencyCode().equals(toCustomerAccount.getCurrencyCode())) {
                throw new BaseException("Fail to transfer Fund, the source and destination CustomerAccount are in different currency");
            }

            // check enough fund in source CustomerAccount
            BigDecimal fromCustomerAccountLeftOver = fromCustomerAccount.getBalance().subtract(customerTransaction.getAmount());
            if (fromCustomerAccountLeftOver.compareTo(MoneyUtils.zeroAmount) < 0) {
                throw new BaseException("Not enough Fund from source CustomerAccount ");
            }
            // proceed with update
            updateStmt = conn.prepareStatement(SQL_UPDATE_ACC_BALANCE);
            updateStmt.setBigDecimal(1, fromCustomerAccountLeftOver);
            updateStmt.setLong(2, customerTransaction.getFromAccountId());
            updateStmt.addBatch();
            updateStmt.setBigDecimal(1, toCustomerAccount.getBalance().add(customerTransaction.getAmount()));
            updateStmt.setLong(2, customerTransaction.getToAccountId());
            updateStmt.addBatch();
            int[] rowsUpdated = updateStmt.executeBatch();
            result = rowsUpdated[0] + rowsUpdated[1];
            if (log.isDebugEnabled()) {
                log.debug("Number of rows updated for the transfer : " + result);
            }
            // If there is no error, commit the CustomerTransaction
            conn.commit();
        } catch (SQLException se) {
            // rollback CustomerTransaction if exception occurs
            log.error("transferCustomerAccountBalance(): User CustomerTransaction Failed, rollback initiated for: " + customerTransaction,
                    se);
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException re) {
                throw new BaseException("Fail to rollback CustomerTransaction", re);
            }
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(lockStmt);
            DbUtils.closeQuietly(updateStmt);
        }
        return result;
    }

}
