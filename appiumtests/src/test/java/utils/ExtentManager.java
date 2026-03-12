package utils;



	 
	import com.aventstack.extentreports.ExtentReports;
	import com.aventstack.extentreports.reporter.ExtentSparkReporter;
	 
	public class ExtentManager {
	 
	    private static ExtentReports extent;
	 
	    public static ExtentReports getInstance() {
	        if (extent == null) {
	            createInstance();
	        }
	        return extent;
	    }
	 
	    private static ExtentReports createInstance() {
	        ExtentSparkReporter reporter = new ExtentSparkReporter(System.getProperty("user.dir") + "/test-output/ExtentReport.html");
	        reporter.config().setDocumentTitle("Automation Test Report");
	        reporter.config().setReportName("Regression Suite");
	 
	        extent = new ExtentReports();
	        extent.attachReporter(reporter);
	        extent.setSystemInfo("Tester", "Test");
	        extent.setSystemInfo("Environment", "QA");
	        return extent;
	    }
	}
	 
	


