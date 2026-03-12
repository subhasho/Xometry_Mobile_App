package appiumtests;



	
	import org.testng.ITestContext;
	import org.testng.ITestListener;
	import org.testng.ITestResult;
	 
	import com.aventstack.extentreports.ExtentReports;
	import com.aventstack.extentreports.ExtentTest;
	import com.aventstack.extentreports.Status;

import java.io.IOException;

import org.openqa.selenium.WebDriver;
	 
	import utils.ExtentManager;
	import utils.TakeScreenshot;
	 
	public class TestListener implements ITestListener {
	 
	    private static ExtentReports extent = ExtentManager.getInstance();
	    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
	    public static WebDriver driver;
	 
	    @Override
	    public void onTestStart(ITestResult result) {
	        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
	        test.set(extentTest);
	    }
	 
	    @Override
	    public void onTestSuccess(ITestResult result) {
	        test.get().log(Status.PASS, "Test Passed");
	    }
	 
	    @Override
	    public void onTestFailure(ITestResult result) {
	        test.get().log(Status.FAIL, "Test Failed: " + result.getThrowable());
	 
	        if (driver != null) {
	            String screenshotPath = TakeScreenshot.capture(driver, result.getName());
	            try {
					test.get().addScreenCaptureFromPath(screenshotPath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    }
	 
	    @Override
	    public void onFinish(ITestContext context) {
	        extent.flush();
	    }
	 
	    public static void setDriver(WebDriver driverInstance) {
	        driver = driverInstance;
	    }
	}

