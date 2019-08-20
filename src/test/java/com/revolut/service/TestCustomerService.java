package com.revolut.service;

import com.revolut.model.Customer;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;

/**
 * @author Guang_Yang
 * @version V1.0
 * @Title: TestCustomerService
 * @Package com.revolut.service
 * @Description: Test Customer Service
 * @date
 */
public class TestCustomerService extends TestAbstractService {

    /**
     * Category: Positive
     * Scenario: Test get all the customers
     * Return: Customer list with 200 OK
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testGetAllCustomers() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/customer/all").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);

        String jsonString = EntityUtils.toString(response.getEntity());
        Customer[] customers = mapper.readValue(jsonString, Customer[].class);
        assertTrue(customers.length > 0);
        assertTrue(customers.length == 6);
    }

    /**
     * Category: Positive
     * Scenario: Test get customer by ID
     * Return: Customer with 200 OK
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testGetCustomerById() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/customer/id/2").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);

        String jsonString = EntityUtils.toString(response.getEntity());
        Customer customer = mapper.readValue(jsonString, Customer.class);
        assertTrue(customer.getCustomerName().equals("Bob"));
        assertTrue(customer.getEmailAddress().equals("bob@revolut.com"));
    }

    /**
     * Category: Positive
     * Scenario: Test get customer by Name
     * Return: Customer with 200 OK
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testGetCustomerByName() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/customer/name/Carl").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);

        String jsonString = EntityUtils.toString(response.getEntity());
        Customer customer = mapper.readValue(jsonString, Customer.class);
        assertTrue(customer.getCustomerName().equals("Carl"));
        assertTrue(customer.getEmailAddress().equals("carl@revolut.com"));
    }

    /**
     * Category: Positive
     * Scenario: Test create customer using JSON
     * Return: New Customer 200 OK
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testCreateCustomer() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/customer/create").build();
        Customer customer = new Customer("TestNew", "TestNew@revolut.com", "99999999999");
        String jsonInString = mapper.writeValueAsString(customer);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);

        String jsonString = EntityUtils.toString(response.getEntity());
        Customer uAfterCreation = mapper.readValue(jsonString, Customer.class);
        assertTrue(uAfterCreation.getCustomerName().equals("TestNew"));
        assertTrue(uAfterCreation.getEmailAddress().equals("TestNew@revolut.com"));
        assertTrue(uAfterCreation.getPhoneNumber().equals("99999999999"));
    }

    /**
     * Category: Negative
     * Scenario: Test create already existed customer using JSON
     * Return: return 400 BAD REQUEST
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testCreateExistingCustomer() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/customer/create").build();
        Customer customer = new Customer("David", "david@revolut.com", "44455556666");
        String jsonInString = mapper.writeValueAsString(customer);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 400);

    }

    /**
     * Category: Positive
     * Scenario: Test Update Existing Customer using JSON
     * Return: 200 OK
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testUpdateCustomer() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/customer/2").build();
        Customer customer = new Customer(2L, "Test", "Test@revolut.com", "88888888888");
        String jsonInString = mapper.writeValueAsString(customer);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);
    }

    /**
     * Category: Negative
     * Scenario: Test Update non existed customer using JSON
     * Return: 404 NOT FOUND
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testUpdateNonExistingCustomer() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/customer/100").build();
        Customer customer = new Customer(2L, "Test", "Test@revolut.com", "88888888888");
        String jsonInString = mapper.writeValueAsString(customer);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 404);
    }

    /**
     * Category: Positive
     * Scenario: Test delete Existing Customer using JSON
     * Return: 200 OK
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testDeleteCustomer() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/customer/3").build();
        HttpDelete request = new HttpDelete(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);
    }
    
    /**
     * Category: Negative
     * Scenario: Test delete non existed customer using JSON
     * Return: 404 NOT FOUND
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testDeleteNonExistingCustomer() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/customer/300").build();
        HttpDelete request = new HttpDelete(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 404);
    }
}
