package com.revolut;

import com.revolut.dao.TestCustomerAccountDAO;
import com.revolut.dao.TestCustomerDAO;
import com.revolut.dao.TestCustomerTransaction;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Guang_Yang
 * @version V1.0
 * @Title: TransferMoneyTests
 * @Package com.revolut
 * @Description: TransferMoney Test Suites
 */

@RunWith(Suite.class)
public class TransferMoneyTests {
    public static void main(String[] a) {
        // add the test's in the suite
        TestSuite suite = new TestSuite(TestCustomerAccountDAO.class, TestCustomerDAO.class, TestCustomerTransaction.class );
        TestResult result = new TestResult();
        suite.run(result);
        System.out.println("Number of test cases = " + result.runCount());
    }
}
