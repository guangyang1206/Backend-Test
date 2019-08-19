package com.revolut.service;

import com.revolut.dao.factory.DAOFactory;
import com.revolut.model.CustomerTransaction;
import com.revolut.util.BaseException;
import com.revolut.util.MoneyUtils;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/transaction")
@Produces(MediaType.APPLICATION_JSON)
public class CustomerTransactionService {

	private final DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);
	
	/**
	 * Transfer fund between two accounts.
	 * @param customerTransaction
	 * @return Response
	 * @throws BaseException
	 */
	@POST
	public Response transferFund(CustomerTransaction customerTransaction) throws BaseException {

		String currency = customerTransaction.getCurrencyCode();
		if (MoneyUtils.INSTANCE.validateCcyCode(currency)) {
			int updateCount = daoFactory.getCustomerAccountDAO().transferCustomerAccountBalance(customerTransaction);
			if (updateCount == 2) {
				return Response.status(Response.Status.OK).build();
			} else {
				// transaction failed
				throw new WebApplicationException("Transaction failed", Response.Status.BAD_REQUEST);
			}
		} else {
			throw new WebApplicationException("Currency Code Invalid ", Response.Status.BAD_REQUEST);
		}

	}

}
