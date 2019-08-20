package com.revolut.service;

import com.revolut.model.CustomerAccount;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
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
 * @Title: TestCustomerCustomerAccountService
 * @Package com.revolut.service
 * @Description: Test Customer CustomerAccount Service RestAPI
 */
public class TestCustomerAccountService extends TestAbstractService {

    /**
     * Category: Positive
     * Scenario: Test get all the customer accounts
     * Return: Customer account list with 200 OK
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testGetAllCustomerAccounts() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/all").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);

        String jsonString = EntityUtils.toString(response.getEntity());
        CustomerAccount[] accounts = mapper.readValue(jsonString, CustomerAccount[].class);
        assertTrue(accounts.length > 0);
        assertTrue(accounts.length == 8);
    }

    /**
     * Category: Positive
     * Scenario: Test get customer by name
     * Return: Customer account with 200 OK
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testGetCustomerAccountByName() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/1").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();

        assertTrue(statusCode == 200);

        String jsonString = EntityUtils.toString(response.getEntity());
        CustomerAccount account = mapper.readValue(jsonString, CustomerAccount.class);
        assertTrue(account.getCustomerName().equals("Allen"));
    }

    /**
     * Category: Positive
     * Scenario: Test get customer account balance given account ID
     * Return: Customer account balance with 200 OK
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testGetCustomerAccountBalance() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/1/balance").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);
        //check the content, assert user test2 have balance 100
        String balance = EntityUtils.toString(response.getEntity());
        BigDecimal res = new BigDecimal(balance).setScale(4, RoundingMode.HALF_EVEN);
        BigDecimal db = new BigDecimal(1000).setScale(4, RoundingMode.HALF_EVEN);
        assertTrue(res.equals(db));
    }

    /**
     * Category: Positive
     * Scenario: Test create customer account
     * Return: New customer account balance with 200 OK
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testCreateCustomerAccount() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/create").build();
        BigDecimal balance = new BigDecimal(10).setScale(4, RoundingMode.HALF_EVEN);
        CustomerAccount acc = new CustomerAccount("Test", balance, "CNY");
        String jsonInString = mapper.writeValueAsString(acc);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);
        String jsonString = EntityUtils.toString(response.getEntity());
        CustomerAccount aAfterCreation = mapper.readValue(jsonString, CustomerAccount.class);
        assertTrue(aAfterCreation.getCustomerName().equals("Test"));
        assertTrue(aAfterCreation.getBalance().equals(balance));
        assertTrue(aAfterCreation.getCurrencyCode().equals("CNY"));
    }

    /**
     * Category: Negative
     * Scenario: Test create an existing customer account
     * Return: 500 INTERNAL SERVER ERROR
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testCreateExistingCustomerAccount() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/create").build();
        CustomerAccount acc = new CustomerAccount("Allen", new BigDecimal(1000), "CNY");
        String jsonInString = mapper.writeValueAsString(acc);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 500);
    }

    /**
     * Category: Positive
     * Scenario: Test delete an existing customer account
     * Return: 200 OK
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testDeleteCustomerAccount() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/3").build();
        HttpDelete request = new HttpDelete(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);
    }

    /**
     * Category: Negative
     * Scenario: Test delete an non-existing customer account
     * Return: 404 NOT FOUND
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testDeleteNonExistingCustomerAccount() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/300").build();
        HttpDelete request = new HttpDelete(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 404);
    }
}
