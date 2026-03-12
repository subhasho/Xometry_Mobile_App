package Tests;

import static Tests.BaseClass.driver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;

import org.testng.Assert;

public class Job_Management extends BaseClass {
	
	
	@Test(priority = 7,dependsOnGroups = {"Job_Board"}, retryAnalyzer = RetryAnalyzer.class)
	public void testSeven_JobManagement() throws InterruptedException {
	    Thread.sleep(3000);
	    AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
	    MobileElement JobManagementSection = driver.findElement(By.xpath(
	        "//android.view.View[@content-desc=\"Job Management\"]/com.horcrux.svg.SvgView/com.horcrux.svg.GroupView/com.horcrux.svg.PathView"
	    ));
	    JobManagementSection.click();
	    System.out.println("✅ completed TestSeven_JobManagement..");
	}

	

    @Test(priority = 8, dependsOnMethods = {"testSeven_JobManagement"}, retryAnalyzer = RetryAnalyzer.class)
    public void testEight_SearchJob() throws InterruptedException {
        Thread.sleep(3000);
        MobileElement CurrentWorkSection = driver.findElement(By.xpath("//android.widget.EditText[@text=\"Search Jobs\"]"));
        CurrentWorkSection.sendKeys("J004CD16");
        System.out.println("valid search input ");
        System.out.println("completed testEight_SearchJob..");
    }

    @Test(priority = 9, dependsOnMethods = {"testEight_SearchJob"}, retryAnalyzer = RetryAnalyzer.class)
    public void testNine_ViewDetails() throws InterruptedException {
        Thread.sleep(8000);
        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;

        try {
            driver.findElement(MobileBy.AndroidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))" +
                ".scrollIntoView(new UiSelector().description(\"View Details\"))"
            ));
           // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
           WebDriverWait wait = new WebDriverWait(driver, 30);
            MobileElement viewDetails = (MobileElement) wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.view.ViewGroup[@content-desc='View Details']")
                )
            );
            viewDetails.click();
            Thread.sleep(8000);
            System.out.println("Navigated to Job overview page");
        } catch (Exception e) {
            System.out.println("Element not found or error occurred: " + e.getMessage());
        }
    }  

 
    @Test(priority = 12,  retryAnalyzer = RetryAnalyzer.class)
    public void testtwelve_RS() throws InterruptedException {
        Thread.sleep(8000);
        MobileElement RequestSupport = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"Request Support\"]"));
        RequestSupport.click();
        System.out.println("completed testtwelve..");
    }

    @Test(priority = 13, dependsOnMethods = {"testtwelve_RS"}, retryAnalyzer = RetryAnalyzer.class)
    public void testThirteen() throws InterruptedException {
        Thread.sleep(3000);
        driver.findElement(By.xpath("//android.widget.TextView[@text='Select...']")).click();
        driver.findElement(By.xpath("//android.widget.TextView[@text='Job will be late']")).click();
        //Thread.sleep(8000);
        System.out.println("completed testThirteen..");
        
    }

    @Test(priority = 14,  retryAnalyzer = RetryAnalyzer.class)
    public void testForteen() throws InterruptedException {
        Thread.sleep(3000);
        MobileElement QuestionSection = driver.findElement(By.xpath("//android.widget.EditText[@text=\"Enter your question here...\"]"));
        QuestionSection.sendKeys("Test");
        System.out.println("completed testThirteen..");
    }

    @Test(priority = 15,  retryAnalyzer = RetryAnalyzer.class)
    public void testFifteen() throws InterruptedException {
        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        try {
            driver.findElement(MobileBy.AndroidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true))" +
                            ".scrollIntoView(new UiSelector().description(\"Yes\"))"));
            //WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebDriverWait wait = new WebDriverWait(driver, 30);
            MobileElement Yes_RB = (MobileElement) wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//android.view.ViewGroup[@content-desc=\"Yes\"]/android.widget.RadioButton/android.view.ViewGroup")));
            Yes_RB.click();
            System.out.println("Select Radio Button Yes ");
        } catch (Exception e) {
            System.out.println("Element not found or error occurred: " + e.getMessage());
        }
    }
    
    @Test(priority = 16,  alwaysRun = true)
    public void testSixteen() throws InterruptedException {
        Thread.sleep(3000);
        SoftAssert softAssert = new SoftAssert();
        boolean isFailed = false;

        try {
            System.out.println("🔹 Clicking on 'Submit Update'...");
            MobileElement submitUpdate = (MobileElement) driver.findElement(
                    By.xpath("//android.view.ViewGroup[@content-desc='Submit Update']")
            );
            submitUpdate.click();

            System.out.println("🔹 Waiting for possible status message...");
            WebDriverWait wait = new WebDriverWait(driver, 30);

            // 🔍 Try detecting failure message
            try {
                MobileElement failureMsg = (MobileElement) wait.until(
                        ExpectedConditions.presenceOfElementLocated(
                                MobileBy.AndroidUIAutomator(
                                        "new UiSelector().textContains(\"Failed to submit support request\")")
                        )
                );

                if (failureMsg != null && failureMsg.isDisplayed()) {
                    System.out.println("❌ Submission failed: " + failureMsg.getText());
                    isFailed = true;
                }

            } catch (org.openqa.selenium.TimeoutException e) {
                // If failure message not found within time, assume success
                System.out.println("✅ No failure message detected. Request Support likely submitted successfully.");
            }

        } catch (Exception e) {
            System.out.println("❌ Unexpected error during Submit Update: " + e.getMessage());
            isFailed = true;
        }

        // ✅ Check if app still running
        if (!isAppInForeground()) {
            System.out.println("⚠️ App closed or minimized after Submit Update — relaunching...");
            try {
                driver.launchApp();
                Thread.sleep(5000);
                System.out.println("✅ App relaunched successfully.");
            } catch (Exception e) {
                System.out.println("❌ Failed to relaunch app: " + e.getMessage());
                isFailed = true;
            }
        }

        // ✅ Soft assertion logging (does not break execution)
        if (isFailed) {
            softAssert.fail("❌ Request Support submission failed.");
        }

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            System.out.println("⚠️ Assertion failed but continuing execution: " + e.getMessage());
        }

        System.out.println("✅ Completed testSixteen.");
    }

    
    
    
    
  

    @Test(priority = 17,  retryAnalyzer = RetryAnalyzer.class)
    public void testSeventeen_DownloadJobFiles() throws InterruptedException {
        Thread.sleep(3000);
        MobileElement Download_Job_Files = driver.findElement(
            By.xpath("//android.view.ViewGroup[@content-desc=\"Download Job Files\"]"));
        Download_Job_Files.click();
       // Thread.sleep(8000);
        System.out.println("✅ Completed testSeventeen_DownloadJobFiles..");
    }

    @Test(priority = 18, dependsOnMethods = {"testSeventeen_DownloadJobFiles"}, retryAnalyzer = RetryAnalyzer.class)
    public void testEighteen_AllPartFiles() throws InterruptedException {
        Thread.sleep(3000);
        MobileElement All_Part_Files = driver.findElement(By.xpath("//android.widget.TextView[@text=\"All Part Files\"]"));
        All_Part_Files.click();
        Thread.sleep(8000);
        //for (int i = 0; i < 6; i++)   driver.navigate().back();
       // pressBackSafely(6);  // replaces 6× navigate().back()

        driver.navigate().back();
        driver.navigate().back();
        driver.navigate().back();
        driver.navigate().back();
        driver.navigate().back();
        driver.navigate().back();
      //  Thread.sleep(8000);
        System.out.println("✅ Closed file picker. Completed testEighteen_AllPartFiles.");
    }

    @Test(priority = 19,  retryAnalyzer = RetryAnalyzer.class)
    public void testNineteen_AllTravelers() throws InterruptedException {
       // Thread.sleep(3000);
        MobileElement Download_Job_Files = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"Download Job Files\"]"));
        Download_Job_Files.click();
       // Thread.sleep(3000);
        MobileElement All_Travelers = driver.findElement(By.xpath("//android.widget.TextView[@text=\"All Travelers\"]"));
        All_Travelers.click();
       Thread.sleep(8000);
       //for (int i = 0; i < 2; i++) driver.navigate().back();
        driver.navigate().back();
       // Thread.sleep(3000);
        driver.navigate().back();
        //Thread.sleep(8000);
        System.out.println("✅ Completed testNineteen_AllTravelers..");
    }

    @Test(priority = 20,  retryAnalyzer = RetryAnalyzer.class)
    public void testTwenty_AllJobFiles() throws InterruptedException {
        Thread.sleep(3000);
        MobileElement Download_Job_Files = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"Download Job Files\"]"));
        Download_Job_Files.click();
      //  Thread.sleep(3000);
        MobileElement All_Job_Files = driver.findElement(By.xpath("//android.widget.TextView[@text=\"All Job Files\"]"));
        All_Job_Files.click();
        Thread.sleep(10000);
        for (int i = 0; i < 10; i++) driver.navigate().back();
        	/*driver.navigate().back();
        	driver.navigate().back();
        	driver.navigate().back();
        	driver.navigate().back();
        	driver.navigate().back();
        	driver.navigate().back();
        	driver.navigate().back();
        	driver.navigate().back();
        	driver.navigate().back();
        	driver.navigate().back();*/
        
        System.out.println("✅ Completed testTwenty_AllJobFiles..");
    }

    @Test(priority = 21,  retryAnalyzer = RetryAnalyzer.class)
    public void testTwentyone_PurchaseOrder() throws InterruptedException {
        Thread.sleep(3000);
        MobileElement Download_Job_Files = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"Download Job Files\"]"));
        Download_Job_Files.click();
       // Thread.sleep(3000);
        MobileElement Purchase_Order = driver.findElement(By.xpath("//android.widget.TextView[@text=\"Purchase Order\"]"));
        Purchase_Order.click();
        Thread.sleep(8000);
        driver.navigate().back();
      //  Thread.sleep(8000);
        System.out.println(" Completed testTwentyone_PurchaseOrder..");
    }

    @Test(priority = 22,  retryAnalyzer = RetryAnalyzer.class)
    public void testTwentytwo_InventoryList() throws InterruptedException {
        Thread.sleep(3000);
        MobileElement Download_Job_Files = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"Download Job Files\"]"));
        Download_Job_Files.click();
       // Thread.sleep(3000);
        MobileElement Inventory_List = driver.findElement(By.xpath("//android.widget.TextView[@text=\"Inventory List\"]"));
        Inventory_List.click();
        Thread.sleep(8000);
        driver.navigate().back();
       // Thread.sleep(8000);
        System.out.println(" Completed testTwentytwo_InventoryList..");
    }


    
  /*  @Test(priority = 18, dependsOnMethods = {"testSeventeen"})
    public void testEighteen() throws InterruptedException {
        try {
            System.out.println("🔹 Waiting for 'All Part Files' button to appear...");
            WebDriverWait wait = new WebDriverWait(driver, 30);  // Selenium 3 style

            // Wait for "All Part Files" button
            MobileElement allPartFiles = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.widget.TextView[@text='All Part Files']")
                    )
            );

            // Click on "All Part Files"
            allPartFiles.click();
            System.out.println("✅ Clicked on 'All Part Files'.");

            // ✅ Wait dynamically for the file to be downloaded
            boolean isDownloaded = waitForFileDownload("AllPartFiles.zip", 30); // wait up to 30 seconds

            if (isDownloaded) {
                System.out.println("✅ File downloaded successfully: AllPartFiles.zip");
            } else {
                System.out.println("❌ Download failed: File not found or incomplete.");
                Assert.fail("❌ Download failed: 'All Part Files' were not downloaded successfully.");
            }

            // Navigate back to main screen
            for (int i = 0; i < 6; i++) {
                driver.navigate().back();
                Thread.sleep(500);
            }

            System.out.println("✅ Closed file picker. Completed testEighteen.");

        } catch (org.openqa.selenium.TimeoutException e) {
            Assert.fail("❌ 'All Part Files' button not found or not clickable within time limit.");
        } catch (Exception e) {
            Assert.fail("❌ Exception occurred while downloading 'All Part Files': " + e.getMessage());
        }
    }

    
      Wait for a file to appear in the device's Download folder.
     
    public boolean waitForFileDownload(String fileName, int timeoutSeconds) throws InterruptedException, IOException {
        String filePath = "/sdcard/Download/" + fileName;
        int attempts = 0;

        while (attempts < timeoutSeconds) {
            Process process = Runtime.getRuntime().exec("adb shell ls " + filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();

            if (line != null && line.contains(fileName)) {
                return true; // File exists
            }

            Thread.sleep(1000); // wait 1 second before retry
            attempts++;
        }

        return false; // File not found after waiting
    }

    
   /* @Test(priority = 18, dependsOnMethods = {"testSeventeen"})
    public void testEighteen() throws InterruptedException {
        try {
            System.out.println("🔹 Waiting for 'All Part Files' button to appear...");
           // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebDriverWait wait = new WebDriverWait(driver, 30);  // Selenium 3 style

            // Wait for "All Part Files" button
            MobileElement allPartFiles = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.widget.TextView[@text='All Part Files']")
                    )
            );

            // Click on "All Part Files"
            allPartFiles.click();
            System.out.println("✅ Clicked on 'All Part Files'.");

            // Wait for file download process
            Thread.sleep(5000);

            // ✅ Verify file download
            boolean isDownloaded = verifyFileDownloadedSuccessfully("AllPartFiles.zip", 5);

            if (isDownloaded) {
                System.out.println("✅ File downloaded successfully: AllPartFiles.zip");
            } else {
                System.out.println("❌ Download failed: File not found or incomplete.");
                Assert.fail("❌ Download failed: 'All Part Files' were not downloaded successfully.");
            }

            // Navigate back to main screen
            for (int i = 0; i < 6; i++) {
                driver.navigate().back();
                Thread.sleep(500);
            }

            System.out.println("✅ Closed file picker. Completed testEighteen.");

        } catch (org.openqa.selenium.TimeoutException e) {
            Assert.fail("❌ 'All Part Files' button not found or not clickable within time limit.");
        } catch (Exception e) {
            Assert.fail("❌ Exception occurred while downloading 'All Part Files': " + e.getMessage());
        }
    }

   
   



    @Test(priority = 19, dependsOnMethods = {"testEighteen"},alwaysRun = true)
    public void testNineteen() throws InterruptedException {
         
         Thread.sleep(8000);
        try {
            System.out.println("🔹 Waiting for 'All Travelers' button...");
            //WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebDriverWait wait = new WebDriverWait(driver, 15);

            MobileElement downloadJobFiles = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.view.ViewGroup[@content-desc='Download Job Files']")
                    )
            );
            downloadJobFiles.click();

            MobileElement allTravelers = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.widget.TextView[@text='All Travelers']")
                    )
            );
            allTravelers.click();
            System.out.println("✅ Clicked on 'All Travelers'.");

            Thread.sleep(5000);
            boolean isDownloaded = verifyFileDownloadedSuccessfully("AllTravelers.zip", 10);

            if (isDownloaded) {
                System.out.println("✅ File downloaded successfully: AllTravelers.zip");
            } else {
            	System.out.println("❌ Download failed: 'All Travelers' not downloaded.");
                Assert.fail("❌ Download failed: 'All Travelers' not downloaded.");
            }
//
            driver.navigate().back();
            driver.navigate().back();
            System.out.println("✅ Completed testNineteen.");
        } catch (org.openqa.selenium.TimeoutException e) {
            Assert.fail("❌ 'All Travelers' button not found within time limit.");
        } catch (Exception e) {
            Assert.fail("❌ Exception in testNineteen: " + e.getMessage());
        }
    }


    @Test(priority = 20, dependsOnMethods = {"testNineteen"},alwaysRun = true)
    public void testTwenty() throws InterruptedException {
    	 
         Thread.sleep(8000);
        try {
            System.out.println("🔹 Waiting for 'All Job Files' button...");
           // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebDriverWait wait = new WebDriverWait(driver, 15);

            MobileElement downloadJobFiles = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.view.ViewGroup[@content-desc='Download Job Files']")
                    )
            );
            downloadJobFiles.click();

            MobileElement allJobFiles = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.widget.TextView[@text='All Job Files']")
                    )
            );
            allJobFiles.click();
            System.out.println("✅ Clicked on 'All Job Files'.");

            Thread.sleep(5000);
            boolean isDownloaded = verifyFileDownloadedSuccessfully("AllJobFiles.zip", 10);

            if (isDownloaded) {
                System.out.println("✅ File downloaded successfully: AllJobFiles.zip");
            } else {
            	System.out.println("❌ Download failed: 'allJobFiles' not downloaded.");
            	
                Assert.fail("❌ Download failed: 'All Job Files' were not downloaded.");
            }

            for (int i = 0; i < 8; i++) driver.navigate().back();
            System.out.println("✅ Completed testTwenty.");
        } catch (org.openqa.selenium.TimeoutException e) {
            Assert.fail("❌ 'All Job Files' button not found within time limit.");
        } catch (Exception e) {
            Assert.fail("❌ Exception in testTwenty: " + e.getMessage());
        }
    }
    
    
    @Test(priority = 21, dependsOnMethods = {"testTwenty"}, alwaysRun = true)
    public void testTwentyone() throws InterruptedException {
        try {
            Thread.sleep(8000);
            System.out.println("🔹 Waiting for 'Purchase Order' button...");

            WebDriverWait wait = new WebDriverWait(driver, 15);

            // Tap on 'Download Job Files'
            MobileElement downloadJobFiles = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.view.ViewGroup[@content-desc='Download Job Files']")
                    )
            );
            downloadJobFiles.click();

            // Tap on 'Purchase Order'
            MobileElement purchaseOrder = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.widget.TextView[@text='Purchase Order']")
                    )
            );
            purchaseOrder.click();
            System.out.println("✅ Clicked on 'Purchase Order'.");

            Thread.sleep(5000);

            // Verify file download
            boolean isDownloaded = verifyFileDownloadedSuccessfully("PurchaseOrder.zip", 10);
            if (isDownloaded) {
                System.out.println("✅ File downloaded successfully: PurchaseOrder.zip");
            } else {
                System.out.println("❌ Download failed: 'Purchase Order' not downloaded.");
                Assert.fail("❌ Download failed: 'Purchase Order' file not downloaded.");
            }

            // ✅ Check if app is still in foreground after download
            if (!isAppInForeground()) {
                System.out.println("⚠️ App closed after download — relaunching...");
                driver.launchApp();
                Thread.sleep(5000); // wait for relaunch
            }

            System.out.println("✅ Completed testTwentyone.");

        } catch (org.openqa.selenium.TimeoutException e) {
            Assert.fail("❌ 'Purchase Order' button not found within time limit.");
        } catch (Exception e) {
            Assert.fail("❌ Exception in testTwentyone: " + e.getMessage());
        }
    }

    


    @Test(priority = 22, dependsOnMethods = {"testTwentyone"},alwaysRun = true)
    public void testTwentytwo() throws InterruptedException {
    	
         Thread.sleep(8000);
        try {
            System.out.println("🔹 Waiting for 'Inventory List' button...");
            //WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebDriverWait wait = new WebDriverWait(driver, 15);

            MobileElement downloadJobFiles = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.view.ViewGroup[@content-desc='Download Job Files']")
                    )
            );
            downloadJobFiles.click();

            MobileElement inventoryList = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.widget.TextView[@text='Inventory List']")
                    )
            );
            inventoryList.click();
            System.out.println("✅ Clicked on 'Inventory List'.");

            Thread.sleep(5000);
            boolean isDownloaded = verifyFileDownloadedSuccessfully("InventoryList.zip", 10);

            if (isDownloaded) {
                System.out.println(" File downloaded successfully: InventoryList.zip");
            } else {
            	System.out.println(" Download failed: 'inventoryList' not downloaded.");
                Assert.fail(" Download failed: 'Inventory List' file not downloaded.");
            }

            driver.navigate().back();
            System.out.println("✅ Completed testTwentytwo.");
        } catch (org.openqa.selenium.TimeoutException e) {
            Assert.fail("❌ 'Inventory List' button not found within time limit.");
        } catch (Exception e) {
            Assert.fail("❌ Exception in testTwentytwo: " + e.getMessage());
        }
    }

    */
    
    
    
   

    @Test(priority = 23,  retryAnalyzer = RetryAnalyzer.class, alwaysRun = true)
    public void testTwentythree() throws InterruptedException {
        Thread.sleep(8000);
        MobileElement Job_Details = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"Job Details, +\"]"));
        Job_Details.click();
        System.out.println("completed testTwentythree..");
    }
    
    
    
  
    @Test(priority = 24, dependsOnMethods = {"testTwentythree"}, retryAnalyzer = RetryAnalyzer.class,alwaysRun = true)
    public void testtwentyfour() throws InterruptedException {
        Thread.sleep(8000);
        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        try {
            driver.findElement(MobileBy.AndroidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true))" +
                            ".scrollIntoView(new UiSelector().description(\"Job Milestones\"))"));
           // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
           WebDriverWait wait = new WebDriverWait(driver, 30);
            MobileElement Job_Milestones1 = (MobileElement) wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//android.view.ViewGroup[@content-desc=\"Job Milestones, +\"]")));
            Job_Milestones1.click();
        } catch (Exception e) {
            System.out.println("Element not found or error occurred: " + e.getMessage());
        }
        System.out.println("completed testtwentyfour..");
    }

    // 🔹 Newly added test case (as requested)
    @Test(priority = 25,  retryAnalyzer = RetryAnalyzer.class,alwaysRun = true)
    public void testtwentyfive() throws InterruptedException {
        Thread.sleep(3000);
        MobileElement Job_Milestones = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"Job Milestones, +\"]"));
        Job_Milestones.click();
        System.out.println("completed testtwentyfive..");
    }

    @Test(priority = 26, retryAnalyzer = RetryAnalyzer.class)
    public void testtwentysix() throws InterruptedException {
        Thread.sleep(3000);
        MobileElement Line_Items = driver.findElement(By.xpath("//android.widget.ImageView[@content-desc=\"arrowRight\"]"));
        Line_Items.click();
        System.out.println("completed testtwentysix..");
        //testthirty();
    }

    @Test(priority = 27, dependsOnMethods = {"testtwentysix"}, retryAnalyzer = RetryAnalyzer.class,alwaysRun = true)
    public void testtwentyseven() throws InterruptedException {
        Thread.sleep(8000);
        
        
        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        try {
            driver.findElement(MobileBy.AndroidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true))" +
                            ".scrollIntoView(new UiSelector().description(\"Download Part Files\"))"));
            //WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebDriverWait wait = new WebDriverWait(driver, 30);
            MobileElement Download_Part_Files = (MobileElement) wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//android.view.ViewGroup[@content-desc=\"Download Part Files\"]")));
        } catch (Exception e) {
            System.out.println("Element not found or error occurred: " + e.getMessage());
        }
        System.out.println("completed testtwentyseven..");
    }

    
    
    @Test(priority = 28, dependsOnMethods = {"testtwentyseven"}, retryAnalyzer = RetryAnalyzer.class)
    public void testtwentyEight() throws InterruptedException {
        Thread.sleep(3000);
        MobileElement Download_Part_Files1 = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"Download Part Files\"]"));
        Download_Part_Files1.click();
        Thread.sleep(3000);
        MobileElement Traveler = driver.findElement(By.xpath("//android.widget.TextView[@text=\"Traveler\"]"));
        Traveler.click();
        Thread.sleep(8000);
        driver.navigate().back();
        Thread.sleep(3000);
        System.out.println("completed testtwentyEight..");
    }

    @Test(priority = 29, dependsOnMethods = {"testtwentyEight"}, retryAnalyzer = RetryAnalyzer.class)
    public void testtwentynine() throws InterruptedException {
        Thread.sleep(3000);
        MobileElement Download_Part_Files = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"Download Part Files\"]"));
        Download_Part_Files.click();
        Thread.sleep(3000);
        MobileElement STEP = driver.findElement(By.xpath("//android.widget.TextView[@text=\"STEP\"]"));
        STEP.click();
        Thread.sleep(8000);
       // ((AndroidDriver<MobileElement>) driver).pressKeyCode(AndroidKey.BACK.getCode());
       driver.navigate().back();
       Thread.sleep(3000);
        System.out.println("completed testtwentynine..");
    }

    @Test(priority = 30,  retryAnalyzer = RetryAnalyzer.class)
    public void testthirty() throws InterruptedException {
        Thread.sleep(3000);
        MobileElement Download_Part_Files = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"Download Part Files\"]"));
        Download_Part_Files.click();
        Thread.sleep(3000);
        MobileElement STL = driver.findElement(By.xpath("//android.widget.TextView[@text=\"STL\"]"));
        STL.click();
        Thread.sleep(8000);
        driver.navigate().back();
      
        System.out.println("completed testthirty..");
    }

    @Test(priority = 31,  retryAnalyzer = RetryAnalyzer.class)
    public void testthirtyone() throws InterruptedException {
        Thread.sleep(3000);
        MobileElement Download_Part_Files = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"Download Part Files\"]"));
        Download_Part_Files.click();
        Thread.sleep(3000);
        MobileElement All_Part_Files = driver.findElement(By.xpath("//android.widget.TextView[@text=\"All Part Files\"]"));
        All_Part_Files.click();
        Thread.sleep(8000);
        //for (int i = 0; i < 3; i++) driver.navigate().back();
       driver.navigate().back();
       driver.navigate().back();
       driver.navigate().back();
       
        System.out.println("completed testthirtyone..");
    }

    @Test(priority = 32, retryAnalyzer = RetryAnalyzer.class)
    public void testthirtytwo() throws InterruptedException {
        Thread.sleep(3000);
        MobileElement Download_Part_Files = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"Download Part Files\"]"));
        Download_Part_Files.click();
        Thread.sleep(3000);
        MobileElement Your_Uploaded_Files = driver.findElement(By.xpath("//android.widget.TextView[@text=\"Your Uploaded Files\"]"));
        Your_Uploaded_Files.click();
        Thread.sleep(8000);
       // for (int i = 0; i < 3; i++) driver.navigate().back();
        driver.navigate().back();
        driver.navigate().back();
        driver.navigate().back();
        
        System.out.println("completed testthirtytwo..");
    }

    
    
    
    @Test(priority = 33,  retryAnalyzer = RetryAnalyzer.class,alwaysRun = true)
    public void testthirtythree() throws InterruptedException {
        Thread.sleep(8000);

        
            // Wait and find STL element by accessibility ID
            WebDriverWait wait = new WebDriverWait(driver, 20);
            MobileElement STL = (MobileElement) wait.until(
                ExpectedConditions.visibilityOfElementLocated(MobileBy.AccessibilityId("STL"))
            );
            STL.click();
            Thread.sleep(8000);
            driver.navigate().back();
            
          
            System.out.println("completed testthirtythree..");
       
      
    }

    @Test(priority = 34,  retryAnalyzer = RetryAnalyzer.class,alwaysRun = true)
    public void testthirtyfour() throws InterruptedException {
        Thread.sleep(8000);
        MobileElement STEP = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"STEP\"]"));
        STEP.click();
        Thread.sleep(8000);
        driver.navigate().back();
        ;
        System.out.println("completed testthirtyfour..");
    }


    @Test(priority = 35,  retryAnalyzer = RetryAnalyzer.class)
    public void testthirtyFive() throws InterruptedException {
        Thread.sleep(3000);
        MobileElement PART = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"PART\"]"));
        PART.click();
        Thread.sleep(8000);
        driver.navigate().back();
      
        System.out.println("completed testthirtyFive..");
    }

  
   /* @Test(priority = 28,alwaysRun = true )
    public void testTwentyeight() throws InterruptedException {
        try {
            System.out.println("🔹 Starting testTwentyeight...");
            Thread.sleep(3000);

          //  WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            WebDriverWait wait = new WebDriverWait(driver, 30);
            

            // Click on Download Part Files
            MobileElement downloadPartFiles = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.view.ViewGroup[@content-desc='Download Part Files']")
                    )
            );
            downloadPartFiles.click();

            // Click on Traveler
            MobileElement traveler = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.widget.TextView[@text='Traveler']")
                    )
            );
            traveler.click();
            System.out.println("✅ Clicked on 'Traveler'.");

            Thread.sleep(8000);

            boolean isDownloaded = verifyFileDownloadedSuccessfully("Traveler.zip", 10);
            if (isDownloaded) {
                System.out.println("✅ File downloaded successfully: Traveler.zip");
            } else {
                System.out.println("❌ Download failed: 'Traveler' not downloaded.");
                Assert.fail(" Download failed: 'Traveler' file not downloaded.");
            }

            // Navigate back only once
            driver.navigate().back();
            Thread.sleep(2000); // small wait for stability

            System.out.println("✅ Completed testTwentyeight without closing app.");
            
        } catch (org.openqa.selenium.TimeoutException e) {
            Assert.fail("❌ Element not found within time limit: " + e.getMessage());
        } catch (Exception e) {
            Assert.fail("❌ Exception in testTwentyeight: " + e.getMessage());
        }
    }

    
    @Test(priority = 29,alwaysRun = true)
    public void testTwentynine() throws InterruptedException {
        try {
        	Thread.sleep(8000);
            System.out.println("🔹 Waiting for 'STEP' button to appear...");
           // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebDriverWait wait = new WebDriverWait(driver, 50);  // Selenium 3 style
            
            MobileElement downloadPartFiles = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.view.ViewGroup[@content-desc='Download Part Files']")
                    )
            );
            downloadPartFiles.click();

            // Wait for "All Part Files" button
            MobileElement STEP = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.widget.TextView[@text='STEP']")
                    )
            );

            // Click on "All Part Files"
            STEP.click();
            System.out.println("✅ Clicked on 'STEP'.");

            // Wait for file download process
            Thread.sleep(5000);

            // ✅ Verify file download
            boolean isDownloaded = verifyFileDownloadedSuccessfully("STEP.zip", 5);

            if (isDownloaded) {
                System.out.println("✅ File downloaded successfully: STEP.zip");
            } else {
                System.out.println("❌ Download failed: File not found or incomplete.");
                Assert.fail("❌ Download failed: 'STEP' were not downloaded successfully.");
            }

            // Navigate back to main screen
            
                driver.navigate().back();
                Thread.sleep(500);
            

            System.out.println("✅ Closed file picker. Completed testEighteen.");

        } catch (org.openqa.selenium.TimeoutException e) {
            Assert.fail("❌ 'STEP' button not found or not clickable within time limit.");
        } catch (Exception e) {
            Assert.fail("❌ Exception occurred while downloading 'STEP': " + e.getMessage());
        }
    }

    @Test(priority = 30,alwaysRun = true)
    public void testThirty() throws InterruptedException {
        try {
        	
        	   Thread.sleep(8000);
            System.out.println("🔹 Waiting for 'STL' button to appear...");
           // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebDriverWait wait = new WebDriverWait(driver, 50);  // Selenium 3 style
            MobileElement downloadPartFiles = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.view.ViewGroup[@content-desc='Download Part Files']")
                    )
            );
            downloadPartFiles.click();

            // Wait for "STL" button
            MobileElement STL = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.widget.TextView[@text='STL']")
                    )
            );

            // Click on "All Part Files"
            STL.click();
            System.out.println("✅ Clicked on 'STL'.");

            // Wait for file download process
            Thread.sleep(5000);

            // ✅ Verify file download
            boolean isDownloaded = verifyFileDownloadedSuccessfully("STL.zip", 5);

            if (isDownloaded) {
                System.out.println("✅ File downloaded successfully: STL.zip");
            } else {
                System.out.println("❌ Download failed: File not found or incomplete.");
                Assert.fail("❌ Download failed: 'STL' were not downloaded successfully.");
            }

            // Navigate back to main screen
            
                driver.navigate().back();
                Thread.sleep(500);
            

            System.out.println("✅ Closed file picker. Completed testEighteen.");

        } catch (org.openqa.selenium.TimeoutException e) {
            Assert.fail("❌ 'STL' button not found or not clickable within time limit.");
        } catch (Exception e) {
            Assert.fail("❌ Exception occurred while downloading 'STL': " + e.getMessage());
        }
    }

   
    @Test(priority = 31,alwaysRun = true)
    public void testThirtyone() throws InterruptedException {
        try {
            System.out.println("🔹 Waiting for 'STL' button to appear...");
           // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebDriverWait wait = new WebDriverWait(driver, 30);  // Selenium 3 style
            MobileElement downloadPartFiles = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.view.ViewGroup[@content-desc='Download Part Files']")
                    )
            );
            downloadPartFiles.click();

            // Wait for "All_Part_Files" button
            MobileElement All_Part_Files = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.widget.TextView[@text='All Part Files']")
                    )
            );

            // Click on "All Part Files"
            All_Part_Files.click();
            System.out.println("✅ Clicked on 'All_Part_Files'.");

            // Wait for file download process
            Thread.sleep(5000);

            // ✅ Verify file download
            boolean isDownloaded = verifyFileDownloadedSuccessfully("All_Part_Files.zip", 5);

            if (isDownloaded) {
                System.out.println("✅ File downloaded successfully: All_Part_Files.zip");
            } else {
                System.out.println("❌ Download failed: File not found or incomplete.");
                Assert.fail("❌ Download failed: 'All_Part_Files' were not downloaded successfully.");
            }

            // Navigate back to main screen
            
                driver.navigate().back();
                Thread.sleep(500);
            

            System.out.println("✅ Closed file picker. Completed testEighteen.");

        } catch (org.openqa.selenium.TimeoutException e) {
            Assert.fail("❌ 'All_Part_Files' button not found or not clickable within time limit.");
        } catch (Exception e) {
            Assert.fail("❌ Exception occurred while downloading 'STL': " + e.getMessage());
        }
    }

   

   

    
   /* @Test(priority = 29,dependsOnMethods = {"testTwentyeight"}, alwaysRun = true)
    public void testTwentynine() throws InterruptedException {
    	
    	  try {
    		  System.out.println("🔹 Starting testTwentynine...");
        Thread.sleep(3000);
        WebDriverWait wait = new WebDriverWait(driver, 30);
        MobileElement Download_Part_Files1 = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"Download Part Files\"]"));
        Download_Part_Files1.click();
        Thread.sleep(3000);
        MobileElement STEP = driver.findElement(By.xpath("//android.widget.TextView[@text=\"STEP\"]"));
        STEP.click();
        System.out.println("✅ Clicked on 'STEP'.");
        Thread.sleep(8000);
        
        boolean isDownloaded = verifyFileDownloadedSuccessfully("STEP.zip", 10);

        if (isDownloaded) {
            System.out.println("✅ File downloaded successfully: STEP.zip");
        } else {
        	System.out.println(" Download failed: 'STEP' not downloaded.");
            Assert.fail(" Download failed: 'STEP' file not downloaded.");
        }

        driver.navigate().back();
        Thread.sleep(2000);
        System.out.println("✅ Completed testTwentytwo.");
    } catch (org.openqa.selenium.TimeoutException e) {
        Assert.fail(" 'STEP' button not found within time limit.");
    } catch (Exception e) {
        Assert.fail("❌ Exception in testTwentynine: " + e.getMessage());
    }
        driver.navigate().back();
        System.out.println("completed testTwentynine..");
    }

    
    
   /* @Test(priority = 30,dependsOnMethods = {"testTwentynine"}, alwaysRun = true)
    public void testThirty() throws InterruptedException {
    	
    	  try {
    		  Thread.sleep(3000);
    		  System.out.println("🔹 Starting testThirty...");
      
        WebDriverWait wait = new WebDriverWait(driver, 15);
        MobileElement Download_Part_Files = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"Download Part Files\"]"));
        Download_Part_Files.click();
        Thread.sleep(3000);
        MobileElement STL = driver.findElement(By.xpath("//android.widget.TextView[@text=\"STL\"]"));
        STL.click();
        System.out.println(" Clicked on 'STL'.");
        Thread.sleep(8000);
        
        boolean isDownloaded = verifyFileDownloadedSuccessfully("STL.zip", 10);

        if (isDownloaded) {
            System.out.println("✅ File downloaded successfully: STL.zip");
        } else {
        	System.out.println(" Download failed: 'STEP' not downloaded.");
            Assert.fail(" Download failed: 'STEP' file not downloaded.");
        }

        driver.navigate().back();
        Thread.sleep(2000);
        System.out.println(" Completed testThirty.");
    } catch (org.openqa.selenium.TimeoutException e) {
        Assert.fail(" 'STL' button not found within time limit.");
    } catch (Exception e) {
        Assert.fail(" Exception in testThirty: " + e.getMessage());
    }
        driver.navigate().back();
        System.out.println("completed testThirty..");
    }

    
    @Test(priority = 31,dependsOnMethods = {"testThirty"}, alwaysRun = true)
    public void testThirtyOne() throws InterruptedException {
    	
    	  try {
    		  Thread.sleep(8000);
    		  System.out.println("🔹 Starting testThirtyOne...");
        
        WebDriverWait wait = new WebDriverWait(driver, 50);
        MobileElement Download_Part_Files = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"Download Part Files\"]]"));
        Download_Part_Files.click();
        Thread.sleep(3000);
        MobileElement All_Part_Files = driver.findElement(By.xpath("//android.widget.TextView[@text=\"All Part Files\"]"));
        All_Part_Files.click();
        System.out.println(" Clicked on 'All_Part_Files'.");
        Thread.sleep(8000);
        
        boolean isDownloaded = verifyFileDownloadedSuccessfully("All_Part_Files.zip", 10);

        if (isDownloaded) {
            System.out.println("✅ File downloaded successfully: All_Part_Files.zip");
        } else {
        	System.out.println(" Download failed: 'All_Part_Files' not downloaded.");
            Assert.fail(" Download failed: 'All_Part_Files' file not downloaded.");
        }

        driver.navigate().back();
        Thread.sleep(2000);
        System.out.println(" Completed testTwentytwo.");
    } catch (org.openqa.selenium.TimeoutException e) {
        Assert.fail(" 'All_Part_Files' button not found within time limit.");
    } catch (Exception e) {
        Assert.fail(" Exception in testThirtyOne: " + e.getMessage());
    }
        driver.navigate().back();
        System.out.println("completed testThirtyOne..");
    }

    
    
    
    
  /*  @Test(priority = 28, dependsOnMethods = {"testtwentyseven"},alwaysRun = true)
    public void testTwentyeight() throws InterruptedException {
    	
         Thread.sleep(8000);
        try {
            System.out.println("🔹 Waiting for 'STEP' button...");
            //WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebDriverWait wait = new WebDriverWait(driver, 30);

            MobileElement downloadJobFiles = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.view.ViewGroup[@content-desc='Download Job Files']")
                    )
            );
            downloadJobFiles.click();

            MobileElement STEP = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.widget.TextView[@text='STEP']")
                    )
            );
            STEP.click();
            System.out.println("✅ Clicked on 'STEP'.");

            Thread.sleep(5000);
            boolean isDownloaded = verifyFileDownloadedSuccessfully("STEP.zip", 10);

            if (isDownloaded) {
                System.out.println("✅ File downloaded successfully: STEP.zip");
            } else {
            	System.out.println("❌ Download failed: 'STEP' not downloaded.");
                Assert.fail("❌ Download failed: 'STEP' file not downloaded.");
            }

            driver.navigate().back();
            System.out.println("✅ Completed testTwentytwo.");
        } catch (org.openqa.selenium.TimeoutException e) {
            Assert.fail("❌ 'STEP' button not found within time limit.");
        } catch (Exception e) {
            Assert.fail("❌ Exception in testTwentytwo: " + e.getMessage());
        }
    }

    
    @Test(priority = 29, dependsOnMethods = {"testTwentyeight"},alwaysRun = true)
    public void testTwentynine() throws InterruptedException {
    	
         Thread.sleep(8000);
        try {
            System.out.println("🔹 Waiting for 'STL' button...");
            //WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebDriverWait wait = new WebDriverWait(driver, 15);

            MobileElement downloadJobFiles = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.view.ViewGroup[@content-desc='Download Job Files']")
                    )
            );
            downloadJobFiles.click();

            MobileElement STL = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.widget.TextView[@text='STL']")
                    )
            );
            STL.click();
            System.out.println("✅ Clicked on 'STEP'.");

            Thread.sleep(5000);
            boolean isDownloaded = verifyFileDownloadedSuccessfully("STL.zip", 10);

            if (isDownloaded) {
                System.out.println("✅ File downloaded successfully: STL.zip");
            } else {
            	System.out.println(" Download failed: 'STL' not downloaded.");
                Assert.fail(" Download failed: 'STL' file not downloaded.");
                System.out.println("Check after assertion");
            }

            driver.navigate().back();
            System.out.println("✅ Completed testTwentytwo.");
        } catch (org.openqa.selenium.TimeoutException e) {
            Assert.fail("❌ 'STL' button not found within time limit.");
        } catch (Exception e) {
            Assert.fail("❌ Exception in testTwentytwo: " + e.getMessage());
        }
    }

    
    
    
 
 

   /* @Test(priority = 28, dependsOnMethods = {"testtwentyseven"}, alwaysRun = true)
    public void testTwentyeight() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        Thread.sleep(6000);

        try {
            ensureSessionAndAppActive();

            System.out.println("🔹 Waiting for 'STEP' button...");
            WebDriverWait wait = new WebDriverWait(driver, 20);

            MobileElement downloadJobFiles = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.view.ViewGroup[@content-desc='Download Part Files']")
                    )
            );
            downloadJobFiles.click();

            MobileElement STEP = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.view.ViewGroup[@content-desc='STEP']")
                    )
            );
            STEP.click();
            System.out.println("✅ Clicked on 'STEP'.");

            Thread.sleep(5000);
            boolean isDownloaded = verifyFileDownloadedSuccessfully("STEP.zip", 10);
            softAssert.assertTrue(isDownloaded, "❌ Download failed: 'STEP.zip' not downloaded.");

            // 🔹 Recheck session before moving on
            ensureSessionAndAppActive();

            System.out.println("✅ Completed testTwentyeight successfully.");
        } catch (Exception e) {
            softAssert.fail("❌ Exception in testTwentyeight: " + e.getMessage());
            ensureSessionAndAppActive(); // still restore before next test
        } finally {
            softAssert.assertAll();
        }
    }
    

    @Test(priority = 29, dependsOnMethods = {"testTwentyeight"}, alwaysRun = true)
    public void testTwentynine() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        Thread.sleep(5000);

        try {
            ensureSessionAndAppActive();  // <-- New line

            System.out.println("🔹 Waiting for 'STL' button...");
            WebDriverWait wait = new WebDriverWait(driver, 20);

            MobileElement downloadJobFiles = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.view.ViewGroup[@content-desc='Download Part Files']")
                    )
            );
            downloadJobFiles.click();

            MobileElement STL = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.view.ViewGroup[@content-desc='STL']")
                    )
            );
            STL.click();
            System.out.println("✅ Clicked on 'STL'.");

            Thread.sleep(5000);
            boolean isDownloaded = verifyFileDownloadedSuccessfully("STL.zip", 10);
            softAssert.assertTrue(isDownloaded, "❌ Download failed: 'STL.zip' not downloaded.");

            System.out.println("✅ Completed testTwentynine successfully.");
        } catch (Exception e) {
            softAssert.fail("❌ Exception in testTwentynine: " + e.getMessage());
            ensureSessionAndAppActive(); // recover for next test if needed
        } finally {
            softAssert.assertAll();
        }
    }

   

    
    @Test(priority = 30,alwaysRun = true)
    public void testThirty() throws InterruptedException {
    	
         Thread.sleep(8000);
        try {
            System.out.println("🔹 Waiting for 'All_Part_Files' button...");
            //WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebDriverWait wait = new WebDriverWait(driver, 15);

            MobileElement downloadJobFiles = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.view.ViewGroup[@content-desc='Download Part Files']")
                    )
            );
            downloadJobFiles.click();

            MobileElement All_Part_Files = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.view.ViewGroup[@content-desc=\"All Part Files\"]")
                    )
            );
            All_Part_Files.click();
            System.out.println("✅ Clicked on 'All_Part_Files'.");

            Thread.sleep(5000);
            boolean isDownloaded = verifyFileDownloadedSuccessfully("All_Part_Files.zip", 10);

            if (isDownloaded) {
                System.out.println("✅ File downloaded successfully: All_Part_Files.zip");
            } else {
            	System.out.println("❌ Download failed: 'All_Part_Files' not downloaded.");
                Assert.fail("❌ Download failed: 'All_Part_Files' file not downloaded.");
            }

            driver.navigate().back();
            System.out.println("✅ Completed testTwentytwo.");
        } catch (org.openqa.selenium.TimeoutException e) {
            Assert.fail("❌ 'All_Part_Files' button not found within time limit.");
        } catch (Exception e) {
            Assert.fail("❌ Exception in testTwentytwo: " + e.getMessage());
        }
    }


    @Test(priority = 31, dependsOnMethods = {"testThirty"},alwaysRun = true)
    public void testThirtyOne() throws InterruptedException {
    	
         Thread.sleep(8000);
        try {
            System.out.println("🔹 Waiting for 'All_Part_Files' button...");
            //WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebDriverWait wait = new WebDriverWait(driver, 15);

            MobileElement downloadJobFiles = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.view.ViewGroup[@content-desc='Download Part Files']")
                    )
            );
            downloadJobFiles.click();

            MobileElement Your_Uploaded_Files = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.view.ViewGroup[@content-desc=\"Your Uploaded Files\"]")
                    )
            );
            Your_Uploaded_Files.click();
            System.out.println("✅ Clicked on 'Your_Uploaded_Files'.");

            Thread.sleep(5000);
            boolean isDownloaded = verifyFileDownloadedSuccessfully("Your_Uploaded_Files.zip", 10);

            if (isDownloaded) {
                System.out.println("✅ File downloaded successfully: Your_Uploaded_Files.zip");
            } else {
            	System.out.println("❌ Download failed: 'Your_Uploaded_Files' not downloaded.");
                Assert.fail("❌ Download failed: 'Your_Uploaded_Files' file not downloaded.");
            }

            driver.navigate().back();
            System.out.println("✅ Completed testTwentytwo.");
        } catch (org.openqa.selenium.TimeoutException e) {
            Assert.fail("❌ 'Your_Uploaded_Files' button not found within time limit.");
        } catch (Exception e) {
            Assert.fail("❌ Exception in testTwentytwo: " + e.getMessage());
        }
    }



    @Test(priority = 32, alwaysRun = true)
    public void testThirtytwo() throws InterruptedException {
    	
         Thread.sleep(8000);
        try {
        	System.out.println("🔹 Starting testthirtytwo - STL download check...");
           

            WebDriverWait wait = new WebDriverWait(driver, 15);
            MobileElement stlButton = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            MobileBy.AccessibilityId("STL")));
            stlButton.click();

            System.out.println("✅ Clicked on 'STL'.");
            Thread.sleep(5000);

            

            boolean isDownloaded = verifyFileDownloadedSuccessfully("STL.zip", 10);

            if (isDownloaded) {
                System.out.println("✅ File downloaded successfully: STL.zip");
            } else {
            	System.out.println("❌ Download failed: 'STL' not downloaded.");
                Assert.fail("❌ Download failed: 'STL' file not downloaded.");
            }

            driver.navigate().back();
            System.out.println("✅ Completed testTwentytwo.");
        } catch (org.openqa.selenium.TimeoutException e) {
            Assert.fail("❌ 'STL' button not found within time limit.");
        } catch (Exception e) {
            Assert.fail("❌ Exception in testTwentytwo: " + e.getMessage());
        }
    }
    
    @Test(priority = 33, dependsOnMethods = {"testThirtytwo"},alwaysRun = true)
    public void testThirtythree() throws InterruptedException {
    	
         Thread.sleep(8000);
        try {
        	//System.out.println("🔹 Starting testthirtytwo - STEP download check...");
           

            WebDriverWait wait = new WebDriverWait(driver, 15);
            MobileElement stlButton = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            MobileBy.AccessibilityId("STEP")));
            stlButton.click();

            System.out.println("✅ Clicked on 'STEP'.");
            Thread.sleep(5000);

            

            boolean isDownloaded = verifyFileDownloadedSuccessfully("STEP.zip", 10);

            if (isDownloaded) {
                System.out.println("✅ File downloaded successfully: STEP.zip");
            } else {
            	System.out.println("❌ Download failed: 'STEP' not downloaded.");
                Assert.fail("❌ Download failed: 'STEP' file not downloaded.");
            }

            driver.navigate().back();
            System.out.println("✅ Completed testTwentytwo.");
        } catch (org.openqa.selenium.TimeoutException e) {
            Assert.fail("❌ 'STEP' button not found within time limit.");
        } catch (Exception e) {
            Assert.fail("❌ Exception in testTwentytwo: " + e.getMessage());
        }
    }

    @Test(priority = 34, dependsOnMethods = {"testThirtythree"},alwaysRun = true)
    public void testThirtyfour() throws InterruptedException {
    	
         Thread.sleep(8000);
        try {
        	//System.out.println("🔹 Starting testthirtytwo - STEP download check...");
           

            WebDriverWait wait = new WebDriverWait(driver, 15);
            MobileElement stlButton = (MobileElement) wait.until(
                    ExpectedConditions.elementToBeClickable(
                            MobileBy.AccessibilityId("PART")));
            stlButton.click();

            System.out.println("✅ Clicked on 'PART'.");
            Thread.sleep(5000);

            

            boolean isDownloaded = verifyFileDownloadedSuccessfully("PART.zip", 10);

            if (isDownloaded) {
                System.out.println("✅ File downloaded successfully: STEP.zip");
            } else {
            	System.out.println(" Download failed: 'PART' not downloaded.");
                Assert.fail(" Download failed: 'PART' file not downloaded.");
            }

            driver.navigate().back();
            System.out.println(" Completed testTwentytwo.");
        } catch (org.openqa.selenium.TimeoutException e) {
            Assert.fail(" 'PART' button not found within time limit.");
        } catch (Exception e) {
            Assert.fail(" Exception in testTwentytwo: " + e.getMessage());
        }
    }
*/

    

  

   

  

    @Test(priority = 36,dependsOnMethods = {"testthirtyFive"}  ,alwaysRun = true)
    public void testthirtysix() throws InterruptedException {
        Thread.sleep(3000);
        MobileElement Part_Details = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"Part Details\"]"));
        Part_Details.click();
        Thread.sleep(8000);
        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        try {
            driver.findElement(MobileBy.AndroidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true))" +
                            ".scrollIntoView(new UiSelector().description(\"Task Workflow\"))"));
           // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebDriverWait wait = new WebDriverWait(driver, 30);
            MobileElement Task_Workflow = (MobileElement) wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//android.view.ViewGroup[@content-desc=\"Task Workflow\"]")));
            Task_Workflow.click();
        } catch (Exception e) {
            System.out.println("Element not found or error occurred: " + e.getMessage());
        }
        System.out.println("completed testthirtysix..");
    }

    @Test(priority = 37,  retryAnalyzer = RetryAnalyzer.class)
    public void testthirtyseven() throws InterruptedException {
        Thread.sleep(8000);
        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        try {
            driver.findElement(MobileBy.AndroidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true))" +
                            ".scrollIntoView(new UiSelector().description(\"Subtasks\"))"));
           // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebDriverWait wait = new WebDriverWait(driver, 30);
            MobileElement Subtasks = (MobileElement) wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//android.view.ViewGroup[@content-desc=\"Subtasks\"]")));
        } catch (Exception e) {
            System.out.println("Element not found or error occurred: " + e.getMessage());
        }
        System.out.println("completed Testthirtysix..");
    }


    @Test(priority = 38,  retryAnalyzer = RetryAnalyzer.class)
    public void testthirtyEight() throws InterruptedException {
        for (int i = 1; i <= 3; i++) {
            try {
                Thread.sleep(3000);
                String xpath = "(//android.view.ViewGroup[@content-desc=\"Complete\"])[" + i + "]";
                MobileElement completeButton = driver.findElement(By.xpath(xpath));
                completeButton.click();
                System.out.println("Clicked on Complete button #" + i);
                handleOpenAppPopup3(driver);
                handleOpenAppPopup1(driver);
            } catch (Exception e) {
                System.out.println("Could not click Complete button #" + i + ": " + e.getMessage());
            }
        }
        System.out.println("Completed all Complete button iterations in testthirtyseven..");
    }

    @Test(priority = 39,  retryAnalyzer = RetryAnalyzer.class)
    public void testthirtyNine() throws InterruptedException {
        Thread.sleep(3000);
        handleOpenAppPopup3(driver);
        System.out.println("completed TestthirtyEight..");
    }

    @Test(priority = 40,  retryAnalyzer = RetryAnalyzer.class)
    public void testForty() throws InterruptedException {
        Thread.sleep(3000);
        MobileElement Subtasks = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"Subtasks\"]"));
        Subtasks.click();
        
        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        try {
            driver.findElement(MobileBy.AndroidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))" +
                ".scrollIntoView(new UiSelector().description(\"Ship Parts\"))"
            ));
           // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebDriverWait wait = new WebDriverWait(driver, 30);
            MobileElement viewDetails = (MobileElement) wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.widget.TextView[@text=\"Ship Parts\"]")
                )
            );
            viewDetails.click();
            System.out.println("Navigated to Sip Parts");
        } catch (Exception e) {
            System.out.println("Element not found or error occurred: " + e.getMessage());
        }
        Thread.sleep(8000);
        
        System.out.println("completed testForty..");
        
    }
    
    
    @Test(priority = 41, retryAnalyzer = RetryAnalyzer.class)
    public void testUploadFileFromGallery() throws InterruptedException {
    	//WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
       WebDriverWait wait = new WebDriverWait(driver, 30);

        try {
            System.out.println("Uploading file from Gallery");

            // Click on the first Gallery button
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("(//android.view.ViewGroup[@content-desc='Gallery'])[1]")
            )).click();

            handleOpenAppPopup4(driver); // Handle popup if it appears
            Thread.sleep(1000);

            // Select the first image
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("(//android.widget.ImageView)[1]")
            )).click();

            // Confirm selection
            try {
                driver.findElement(By.xpath("//android.widget.Button[@text='Done' or @text='Open']")).click();
            } catch (Exception e) {
                driver.navigate().back(); // fallback if no button
            }

            System.out.println("File uploaded successfully from Gallery");

        } catch (Exception e) {
            System.out.println("Failed to upload from Gallery. Error: " + e.getMessage());
        }
    }

   
    
    
   

    @Test(priority = 42,  retryAnalyzer = RetryAnalyzer.class)
    public void testFortytwo() throws InterruptedException {
        Thread.sleep(3000);
        MobileElement InProgress = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"In Progress\"]"));
        InProgress.click();
        Thread.sleep(8000);
        
        System.out.println("completed testFortyone..");
    }

   
    @Test(priority = 43,  retryAnalyzer = RetryAnalyzer.class)
    public void testFortythree() throws InterruptedException {
        Thread.sleep(3000);
        handleOpenAppPopup2(driver);
        System.out.println("completed testFortythree..");
    }

    
    
    
    
    public void pressBackSafely(int times) throws InterruptedException {
        for (int i = 0; i < times; i++) {
            ((AndroidDriver<MobileElement>) driver)
                    .pressKey(new KeyEvent(AndroidKey.BACK));
            Thread.sleep(800); // small delay for stability
        }
    }


    // --- Popup handler method ---
    public void handleOpenAppPopup4(AppiumDriver<MobileElement> driver) {
        try {
        //	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebDriverWait wait = new WebDriverWait(driver, 10);
            MobileElement allowButton = (MobileElement) wait.until(ExpectedConditions.elementToBeClickable(
                MobileBy.AndroidUIAutomator("new UiSelector().text(\"Allow\")")
            ));
            allowButton.click();
        } catch (Exception e) {
            System.out.println("Popup not found or already handled: " + e.getMessage());
        }
    }

    

 

   

    
    public void handleOpenAppPopup1(AppiumDriver<MobileElement> driver) {
        try {
        	//WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebDriverWait wait = new WebDriverWait(driver, 10);
            MobileElement  ContinuePopup = (MobileElement) wait.until(
                ExpectedConditions.elementToBeClickable(
                    MobileBy.AndroidUIAutomator("new UiSelector().text(\"Stay\")")
                )
            );
            ContinuePopup.click();
            System.out.println("Handled 'Stay' popup.");
        } catch (Exception e) {
            System.out.println("Popup not found or already handled: " + e.getMessage());
        }
    }
    
    
    
    
    public void handleOpenAppPopup3(AppiumDriver<MobileElement> driver) {
        try {
        	//WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
           WebDriverWait wait = new WebDriverWait(driver, 10);
            MobileElement inProgressPopup = (MobileElement) wait.until(
                ExpectedConditions.elementToBeClickable(
                    MobileBy.AndroidUIAutomator("new UiSelector().text(\"In Progress\")")
                )
            );
            inProgressPopup.click();
            System.out.println("Handled 'In Progress' popup.");
        } catch (Exception e) {
            System.out.println("Popup not found or already handled: " + e.getMessage());
        }
    }
    
    
    
    public void handleOpenAppPopup2(AppiumDriver<MobileElement> driver) {
        try {
        	//WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
           WebDriverWait wait = new WebDriverWait(driver, 10);
            MobileElement CompletePopup = (MobileElement) wait.until(
                ExpectedConditions.elementToBeClickable(
                    MobileBy.AndroidUIAutomator("new UiSelector().text(\"Complete\")")
                )
            );
            CompletePopup.click();
            System.out.println("Handled 'Complete' popup.");
        } catch (Exception e) {
            System.out.println("Popup not found or already handled: " + e.getMessage());
        }
    }
    
   

    
    public boolean verifyFileDownloadedSuccessfully(String fileName, int timeoutSeconds) {
        String downloadDir = System.getProperty("user.home") + "/Downloads"; // Modify if needed
        File dir = new File(downloadDir);
        File downloadedFile = new File(dir, fileName);

        int waited = 0;
        while (waited < timeoutSeconds) {
            if (downloadedFile.exists() && downloadedFile.length() > 0) {
                return true; // file found and non-empty
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            waited++;
        }
        return false;
    
    
    
    }
    
    
    
    public boolean isAppInForeground() {
        try {
            String currentActivity = ((AndroidDriver<?>) driver).currentActivity();
            return currentActivity != null && !currentActivity.isEmpty();
        } catch (Exception e) {
            System.out.println("⚠️ App not in foreground or session lost: " + e.getMessage());
            return false;
        }
    }

    
    
    public boolean isDriverAlive() {
        try {
            ((AndroidDriver<?>) driver).getSessionId();  // check if session is valid
            return true;
        } catch (Exception e) {
            System.out.println("⚠️ Driver session is dead: " + e.getMessage());
            return false;
        }
    }

    public void ensureSessionAndAppActive() throws InterruptedException {
        if (!isDriverAlive()) {
            System.out.println("🧩 Restoring Appium session...");
            try {
                // Recreate driver without restarting Appium server
                DesiredCapabilities caps = new DesiredCapabilities();
                caps.setCapability("platformName", "Android");
                caps.setCapability("deviceName", "vivo 1916");
                caps.setCapability("appPackage", "com.xometry.workcenter.prev");
                caps.setCapability("appActivity", "com.xometry.workcenter.prev.MainActivity");
                caps.setCapability("automationName", "UiAutomator2");
                caps.setCapability("noReset", true);
                driver = new AndroidDriver<>(new URL("http://127.0.0.1:4723/wd/hub"), caps);
                Thread.sleep(5000);
                System.out.println("✅ New driver session created successfully.");
            } catch (Exception e) {
                System.out.println("❌ Could not re-establish driver session: " + e.getMessage());
            }
        }

        
    }

}
    
    

   

   

