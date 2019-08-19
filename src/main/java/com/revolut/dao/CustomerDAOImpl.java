package com.revolut.dao;

import com.revolut.dao.factory.H2DAOFactory;
import com.revolut.model.Customer;
import com.revolut.util.BaseException;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Guang_Yang
 * @version V1.0
 * @Title: CustomerDAOImpl
 * @Package com.revolut.dao
 * @Description: Customer Data Access Object Implementation Class
 */
public class CustomerDAOImpl implements CustomerDAO {

    private static Logger log = Logger.getLogger(CustomerDAOImpl.class);
    private final static String SQL_GET_CUSTOMER_BY_ID = "SELECT * FROM Customer WHERE CustomerId = ? ";
    private final static String SQL_GET_ALL_CUSTOMERS = "SELECT * FROM Customer";
    private final static String SQL_GET_CUSTOMER_BY_NAME = "SELECT * FROM Customer WHERE CustomerName = ? ";
    private final static String SQL_INSERT_CUSTOMER = "INSERT INTO Customer (CustomerName, EmailAddress, PhoneNumber) VALUES (?, ?, ?)";
    private final static String SQL_UPDATE_CUSTOMER = "UPDATE Customer SET CustomerName = ?, EmailAddress = ?, PhoneNumber = ? WHERE CustomerId = ? ";
    private final static String SQL_DELETE_CUSTOMER_BY_ID = "DELETE FROM Customer WHERE CustomerId = ? ";

    /**
     * Get all customers
     */
    public List<Customer> getAllCustomers() throws BaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Customer> customers = new ArrayList<Customer>();
        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_GET_ALL_CUSTOMERS);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Customer customer = new Customer(rs.getLong("CustomerId"), rs.getString("CustomerName"), rs.getString("EmailAddress"), rs.getString("PhoneNumber"));
                customers.add(customer);
                if (log.isDebugEnabled())
                    log.debug("getAllCustomers() Retrieve Customer: " + customer);
            }
            return customers;
        } catch (SQLException e) {
            throw new BaseException("Error reading customer data", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }

    /**
     * Find customer by customerId
     */
    public Customer getCustomerById(Long customerId) throws BaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Customer customer = null;
        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_GET_CUSTOMER_BY_ID);
            stmt.setLong(1, customerId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                customer = new Customer(rs.getLong("CustomerId"), rs.getString("CustomerName"), rs.getString("EmailAddress"), rs.getString("PhoneNumber"));
                if (log.isDebugEnabled())
                    log.debug("getCustomerById(): Retrieve Customer: " + customer);
            }
            return customer;
        } catch (SQLException e) {
            throw new BaseException("Error reading customer data", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }

    /**
     * Find customer by customerName
     */
    public Customer getCustomerByName(String customerName) throws BaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Customer customer = null;
        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_GET_CUSTOMER_BY_NAME);
            stmt.setString(1, customerName);
            rs = stmt.executeQuery();
            if (rs.next()) {
                customer = new Customer(rs.getLong("CustomerId"), rs.getString("CustomerName"), rs.getString("EmailAddress"), rs.getString("PhoneNumber"));
                if (log.isDebugEnabled())
                    log.debug("getCustomerByName(): Retrieve Customer: " + customer);
            }
            return customer;
        } catch (SQLException e) {
            throw new BaseException("Error reading customer data", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }

    /**
     * Save Customer
     */
    public Long insertCustomer(Customer customer) throws BaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_INSERT_CUSTOMER, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, customer.getCustomerName());
            stmt.setString(2, customer.getEmailAddress());
            stmt.setString(3, customer.getPhoneNumber());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                log.error("insertCustomer(): Creating customer failed, no rows affected." + customer);
                throw new BaseException("Customer cannot be created");
            }
            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            } else {
                log.error("insertCustomer():  Creating customer failed, no ID obtained." + customer);
                throw new BaseException("Customers Cannot be created");
            }
        } catch (SQLException e) {
            log.error("Error Inserting Customer :" + customer);
            throw new BaseException("Error creating customer data", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, generatedKeys);
        }

    }

    /**
     * Update Customer
     */
    public int updateCustomer(Long customerId, Customer customer) throws BaseException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE_CUSTOMER);
            stmt.setString(1, customer.getCustomerName());
            stmt.setString(2, customer.getEmailAddress());
            stmt.setString(3, customer.getPhoneNumber());
            stmt.setLong(4, customerId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error Updating Customer :" + customer);
            throw new BaseException("Error update customer data", e);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(stmt);
        }
    }

    /**
     * Delete Customer
     */
    public int deleteCustomer(Long customerId) throws BaseException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_DELETE_CUSTOMER_BY_ID);
            stmt.setLong(1, customerId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error Deleting Customer :" + customerId);
            throw new BaseException("Error Deleting Customer ID:" + customerId, e);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(stmt);
        }
    }

}
