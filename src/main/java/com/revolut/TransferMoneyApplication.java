package com.revolut;

import com.revolut.dao.factory.DAOFactory;
import com.revolut.service.CustomerAccountService;
import com.revolut.service.CustomerService;
import com.revolut.service.CustomerTransactionService;
import com.revolut.service.ServiceExceptionMapper;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * @author Guang_Yang
 * @version V1.0
 * @Title: TransferMoneyApplication
 * @Package com.revolut
 * @Description: Main Class
 */
public class TransferMoneyApplication {
    private static Logger log = Logger.getLogger(TransferMoneyApplication.class);
    public static void main(String[] args) throws Exception {
        log.info("Transfer Money Service Initializing ...");
        DAOFactory h2DaoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);
        h2DaoFactory.populateTestData();
        log.info("Transfer Money Service Initialized ...");

        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/*");
        servletHolder.setInitParameter("jersey.config.server.provider.classnames",
                CustomerService.class.getCanonicalName() + "," + CustomerAccountService.class.getCanonicalName() + ","
                        + ServiceExceptionMapper.class.getCanonicalName() + ","
                        + CustomerTransactionService.class.getCanonicalName());
        try {
            log.info("Server start on port 8080 ...");
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }
}
