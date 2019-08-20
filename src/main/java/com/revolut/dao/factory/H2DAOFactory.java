package com.revolut.dao.factory;

import com.revolut.dao.CustomerAccountDAO;
import com.revolut.dao.CustomerAccountDAOImpl;
import com.revolut.dao.CustomerDAO;
import com.revolut.dao.CustomerDAOImpl;
import com.revolut.util.CommonUtils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.h2.tools.RunScript;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Guang_Yang
 * @version V1.0
 * @Title: DAOFactory
 * @Package com.revolut.dao.factory
 * @Description: H2 Database Data Access Object Factory Class
 */
public class H2DAOFactory extends DAOFactory {
	private static final String h2_driver = CommonUtils.getStringProperty("h2_driver");
	private static final String h2_connection_url = CommonUtils.getStringProperty("h2_connection_url");
	private static final String h2_user = CommonUtils.getStringProperty("h2_user");
	private static final String h2_password = CommonUtils.getStringProperty("h2_password");
	private static Logger log = Logger.getLogger(H2DAOFactory.class);

	private final CustomerDAOImpl customerDAO = new CustomerDAOImpl();
	private final CustomerAccountDAOImpl customerAccountDAO = new CustomerAccountDAOImpl();

	H2DAOFactory() {
		// init: load driver
		DbUtils.loadDriver(h2_driver);
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(h2_connection_url, h2_user, h2_password);

	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public CustomerAccountDAO getCustomerAccountDAO() {
		return customerAccountDAO;
	}

	@Override
	public void populateTestData() {
		log.info("Populating Customer Table and Data ... ");
		Connection conn = null;
		try {
			conn = H2DAOFactory.getConnection();
			RunScript.execute(conn, new FileReader("src/main/resources/transfer-money.sql"));
		} catch (SQLException e) {
			log.error("populateTestData(): Error populating customer data: ", e);
			throw new RuntimeException(e);
		} catch (FileNotFoundException e) {
			log.error("populateTestData(): Error finding test script file ", e);
			throw new RuntimeException(e);
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}

}
