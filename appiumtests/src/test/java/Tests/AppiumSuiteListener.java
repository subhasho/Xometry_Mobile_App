package Tests;

import org.testng.ISuite;
import org.testng.ISuiteListener;

/**
 * Single suite hook so the Appium session starts once and stays open for every class in
 * testng.xml (e.g. {@code Tests} then {@code OPC}). Inherited {@code @BeforeSuite}/{@code @AfterSuite}
 * on {@link BaseClass} would otherwise run per test class and could quit the driver between classes.
 */
public class AppiumSuiteListener implements ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        System.out.println(">>> AppiumSuiteListener.onStart suite=" + suite.getName());
        BaseClass.createDriverOnce();
    }

    @Override
    public void onFinish(ISuite suite) {
        System.out.println(">>> AppiumSuiteListener.onFinish suite=" + suite.getName());
        BaseClass.quitDriverOnce();
    }
}
