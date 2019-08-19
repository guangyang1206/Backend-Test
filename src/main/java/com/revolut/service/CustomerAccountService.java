package com.revolut.service;

import com.revolut.dao.factory.DAOFactory;
import com.revolut.model.CustomerAccount;
import com.revolut.util.BaseException;
import com.revolut.util.MoneyUtils;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author Guang_Yang
 * @version V1.0
 * @Title: CustomerAccountService
 * @Package com.revolut.model
 * @Description: CustomerTransaction Model
 */
@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class CustomerAccountService {
	
    private final DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);
    
    private static Logger log = Logger.getLogger(CustomerAccountService.class);

    
    /**
     * getAllCustomerAccounts
     * @return List<CustomerAccount>
     * @throws BaseException
     */
    @GET
    @Path("/all")
    public List<CustomerAccount> getAllCustomerAccounts() throws BaseException {
        return daoFactory.getCustomerAccountDAO().getAllCustomerAccounts();
    }

    /**
     * Find by account id
     * @param accountId
     * @return CustomerAccount
     * @throws BaseException
     */
    @GET
    @Path("/{accountId}")
    public CustomerAccount getCustomerAccount(@PathParam("accountId") Long accountId) throws BaseException {
        return daoFactory.getCustomerAccountDAO().getCustomerAccountById(accountId);
    }
    
    /**
     * Find balance by account Id
     * @param accountId
     * @return BigDecimal
     * @throws BaseException
     */
    @GET
    @Path("/{accountId}/balance")
    public BigDecimal getBalance(@PathParam("accountId") Long accountId) throws BaseException {
        final CustomerAccount customerAccount = daoFactory.getCustomerAccountDAO().getCustomerAccountById(accountId);

        if(customerAccount == null){
            throw new WebApplicationException("CustomerAccount not found", Response.Status.NOT_FOUND);
        }
        return customerAccount.getBalance();
    }
    
    /**
     * Create Account
     * @param customerAccount
     * @return CustomerAccount
     * @throws BaseException
     */
    @PUT
    @Path("/create")
    public CustomerAccount createAccount(CustomerAccount customerAccount) throws BaseException {
        final Long accountId = daoFactory.getCustomerAccountDAO().createCustomerAccount(customerAccount);
        return daoFactory.getCustomerAccountDAO().getCustomerAccountById(accountId);
    }

    /**
     * Deposit amount by account Id
     * @param accountId
     * @param amount
     * @return CustomerAccount
     * @throws BaseException
     */
    @PUT
    @Path("/{accountId}/deposit/{amount}")
    public CustomerAccount deposit(@PathParam("accountId") Long accountId,@PathParam("amount") BigDecimal amount) throws BaseException {

        if (amount.compareTo(MoneyUtils.zeroAmount) <=0){
            throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
        }
        daoFactory.getCustomerAccountDAO().updateCustomerAccountBalance(accountId, amount.setScale(4, RoundingMode.HALF_EVEN));
        return daoFactory.getCustomerAccountDAO().getCustomerAccountById(accountId);
    }

    /**
     * Withdraw amount by account Id
     * @param accountId
     * @param amount
     * @return CustomerAccount
     * @throws BaseException
     */
    @PUT
    @Path("/{accountId}/withdraw/{amount}")
    public CustomerAccount withdraw(@PathParam("accountId") Long accountId,@PathParam("amount") BigDecimal amount) throws BaseException {

        if (amount.compareTo(MoneyUtils.zeroAmount) <=0){
            throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
        }
        BigDecimal delta = amount.negate();
        if (log.isDebugEnabled())
            log.debug("Withdraw service: delta change to account  " + delta + " Account ID = " +accountId);
        daoFactory.getCustomerAccountDAO().updateCustomerAccountBalance(accountId, amount.setScale(4, RoundingMode.HALF_EVEN));
        return daoFactory.getCustomerAccountDAO().getCustomerAccountById(accountId);
    }


    /**
     * Delete amount by account Id
     * @param accountId
     * @return Response
     * @throws BaseException
     */
    @DELETE
    @Path("/{accountId}")
    public Response deleteAccount(@PathParam("accountId") Long accountId) throws BaseException {
        int deleteCount = daoFactory.getCustomerAccountDAO().deleteCustomerAccountById(accountId);
        if (deleteCount == 1) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
