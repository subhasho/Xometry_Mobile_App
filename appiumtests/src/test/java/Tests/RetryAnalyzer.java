package Tests;

import org.testng.IRetryAnalyzer;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int count = 0;
    private static final int maxRetryCount = 3;  // Set the maximum retry attempts

    @Override
    public boolean retry(ITestResult result) {
        if (count < maxRetryCount) {
            count++;
            System.out.println("Retrying test " + result.getName() + " with status " + getResultStatusName(result.getStatus()) + " for the " + count + " time.");
            return true;  // Return true to retry the test
        }
        return false;  // Return false to stop retrying
    }

    // Helper method to get the test result status
    private String getResultStatusName(int status) {
        String resultName = null;
        if (status == 1) {
            resultName = "SUCCESS";
        }
        if (status == 2) {
            resultName = "FAILURE";
        }
        if (status == 3) {
            resultName = "SKIP";
        }
        return resultName;
    }
}
