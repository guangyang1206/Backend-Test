package com.revolut.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * @author Guang_Yang
 * @version V1.0
 * @Title: CustomerAccount
 * @Package com.revolut.model
 * @Description: CustomerAccount Model
 */
public class CustomerAccount {

    @JsonIgnore
    private Long accountId;

    @JsonProperty(required = true)
    private String customerName;

    @JsonProperty(required = true)
    private BigDecimal balance;

    @JsonProperty(required = true)
    private String currencyCode;

    public CustomerAccount() {
    }

    public CustomerAccount(Long accountId, String customerName, BigDecimal balance, String currencyCode) {
        this.accountId = accountId;
        this.customerName = customerName;
        this.balance = balance;
        this.currencyCode = currencyCode;
    }

    public CustomerAccount(String customerName, BigDecimal balance, String currencyCode) {
        this.customerName = customerName;
        this.balance = balance;
        this.currencyCode = currencyCode;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomerAccount account = (CustomerAccount) o;

        if (accountId != account.accountId) return false;
        if (!customerName.equals(account.customerName)) return false;
        if (!balance.equals(account.balance)) return false;
        return currencyCode.equals(account.currencyCode);

    }

    @Override
    public int hashCode() {
        int result = (int) (accountId ^ (accountId >>> 32));
        result = 31 * result + customerName.hashCode();
        result = 31 * result + balance.hashCode();
        result = 31 * result + currencyCode.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", userName='" + customerName + '\'' +
                ", balance=" + balance +
                ", currencyCode='" + currencyCode + '\'' +
                '}';
    }
}
