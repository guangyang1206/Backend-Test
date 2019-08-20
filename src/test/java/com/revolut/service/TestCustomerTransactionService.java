package com.revolut.service;

import com.revolut.model.CustomerAccount;
import com.revolut.model.CustomerTransaction;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;

/**
 * @author Guang_Yang
 * @version V1.0
 * @Title: TestCustomerTransactionService
 * @Package com.revolut.service
 * @Description: Test Customer Transaction Service
 */
public class TestCustomerTransactionService extends TestAbstractService {

    /**
     * Category: Positive
     * Scenario: Test deposit money to given account number
     * Return: 200 OK
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testDeposit() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/1/deposit/1000").build();
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);

        String jsonString = EntityUtils.toString(response.getEntity());
        CustomerAccount afterDeposit = mapper.readValue(jsonString, CustomerAccount.class);
        //check balance is increased from 100 to 200
        assertTrue(afterDeposit.getBalance().equals(new BigDecimal(2000).setScale(4, RoundingMode.HALF_EVEN)));

    }

    /**
     * Category: Positive
     * Scenario: Test withdraw money from account given account number when account has sufficient fund
     * Return: 200 OK
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testWithDrawSufficientFund() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/2/withdraw/1000").build();
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);

        String jsonString = EntityUtils.toString(response.getEntity());
        CustomerAccount afterDeposit = mapper.readValue(jsonString, CustomerAccount.class);
        assertTrue(afterDeposit.getBalance().equals(new BigDecimal(1000).setScale(4, RoundingMode.HALF_EVEN)));

    }

    /**
     * Category: Negative
     * Scenario: Test withdraw money from account given account number when account has no sufficient fund
     * Return: 500 INTERNAL SERVER ERROR
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testWithDrawNonSufficientFund() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/2/withdraw/2000.23456").build();
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        String responseBody = EntityUtils.toString(response.getEntity());
        assertTrue(statusCode == 500);
        assertTrue(responseBody.contains("Not sufficient Fund"));
    }

    /**
     * Category: Positive
     * Scenario: Test transaction from one account to another with source account has sufficient fund
     * Return: 200 OK
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testTransactionEnoughFund() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/transaction").build();
        BigDecimal amount = new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN);
        CustomerTransaction transaction = new CustomerTransaction("EUR", amount, 3L, 4L);

        String jsonInString = mapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);
    }

    /**
     * Category: Negative
     * Scenario: Test transaction from one account to another with source account has no sufficient fund
     * Return: 500 INTERNAL SERVER ERROR
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testTransactionNotEnoughFund() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/transaction").build();
        BigDecimal amount = new BigDecimal(100000).setScale(4, RoundingMode.HALF_EVEN);
        CustomerTransaction transaction = new CustomerTransaction("EUR", amount, 3L, 4L);

        String jsonInString = mapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 500);
    }

    /**
     * Category: Negative
     * Scenario: Test transaction from one account to another with from/to account with different currency code
     * Return: 500 INTERNAL SERVER ERROR
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testTransactionDifferentCcy() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/transaction").build();
        BigDecimal amount = new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN);
        CustomerTransaction transaction = new CustomerTransaction("USD", amount, 3L, 4L);

        String jsonInString = mapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 500);
    }
}
