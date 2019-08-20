package com.revolut.service;

import com.revolut.dao.factory.DAOFactory;
import com.revolut.model.Customer;
import com.revolut.util.BaseException;
import org.apache.log4j.Logger;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/customer")
@Produces(MediaType.APPLICATION_JSON)
public class CustomerService {
 
	private final DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);
    
	private static Logger log = Logger.getLogger(CustomerService.class);

    /**
     * Find all customers
     * @return List<Customer>
     * @throws BaseException
     */
    @GET
    @Path("/all")
    public List<Customer> getAllUsers() throws BaseException {
        return daoFactory.getCustomerDAO().getAllCustomers();
    }
    /**
     * Find by customerId
     * @param customerId
     * @return Customer
     * @throws BaseException
     */
    @GET
    @Path("/id/{customerId}")
    public Customer getCustomerById(@PathParam("customerId") Long customerId) throws BaseException {
        if (log.isDebugEnabled())
            log.debug("Request Received for get Customer by Id " + customerId);
        final Customer customer = daoFactory.getCustomerDAO().getCustomerById(customerId);
        if (customer == null) {
            throw new WebApplicationException("Customer Not Found", Response.Status.NOT_FOUND);
        }
        return customer;
    }

	/**
	 * Find by customerName
	 * @param customerName
	 * @return Customer
	 * @throws BaseException
	 */
    @GET
    @Path("/name/{customerName}")
    public Customer getCustomerByName(@PathParam("customerName") String customerName) throws BaseException {
        if (log.isDebugEnabled())
            log.debug("Request Received for get Customer by Name " + customerName);
        final Customer customer = daoFactory.getCustomerDAO().getCustomerByName(customerName);
        if (customer == null) {
            throw new WebApplicationException("Customer Not Found", Response.Status.NOT_FOUND);
        }
        return customer;
    }
    
    /**
     * Create Customer
     * @param customer
     * @return Customer
     * @throws BaseException
     */
    @POST
    @Path("/create")
    public Customer createCustomer(Customer customer) throws BaseException {
        if (daoFactory.getCustomerDAO().getCustomerByName(customer.getCustomerName()) != null) {
            throw new WebApplicationException("User name already exist", Response.Status.BAD_REQUEST);
        }
        final Long uId = daoFactory.getCustomerDAO().insertCustomer(customer);
        return daoFactory.getCustomerDAO().getCustomerById(uId);
    }
    
    /**
     * Update by Customer Id
     * @param customerId
     * @param customer
     * @return Response
     * @throws BaseException
     */
    @PUT
    @Path("/{customerId}")
    public Response updateCustomer(@PathParam("customerId") Long customerId, Customer customer) throws BaseException {
        final int updateCount = daoFactory.getCustomerDAO().updateCustomer(customerId, customer);
        if (updateCount == 1) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    
    /**
     * Delete by User Id
     * @param customerId
     * @return Response
     * @throws BaseException
     */
    @DELETE
    @Path("/{customerId}")
    public Response deleteCustomer(@PathParam("customerId") Long customerId) throws BaseException {
        int deleteCount = daoFactory.getCustomerDAO().deleteCustomer(customerId);
        if (deleteCount == 1) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
