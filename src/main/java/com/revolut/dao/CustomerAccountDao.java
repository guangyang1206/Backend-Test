package com.revolut.dao;

import com.revolut.model.CustomerAccount;
import com.revolut.model.CustomerTransaction;
import com.revolut.util.BaseException;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Guang_Yang
 * @version V1.0
 * @Title: CustomerAccountDao
 * @Package com.revolut.dao
 * @Description: Customer Account Data Access Object Interface
 */
public interface CustomerAccountDao {

    List<CustomerAccount> getAllCustomerAccounts() throws BaseException;
    CustomerAccount getCustomerAccountById(Long CustomerAccountId) throws BaseException;
    long createCustomerAccount(CustomerAccount CustomerAccount) throws BaseException;
    int deleteCustomerAccountById(Long CustomerAccountId) throws BaseException;
    int updateCustomerAccountBalance(Long CustomerAccountId, BigDecimal deltaAmount) throws BaseException;
    int transferCustomerAccountBalance(CustomerTransaction customerTransaction) throws BaseException;
}
