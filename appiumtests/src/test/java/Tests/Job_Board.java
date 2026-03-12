package Tests;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;

public class Job_Board extends BaseClass {

  /*  // --- Common reusable function for selecting reason ---
    public void selectReason(String reasonText) throws InterruptedException {
        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        Thread.sleep(8000);

        try {
            MobileElement dropdown = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc='Price too low...']"));
            dropdown.click();
            Thread.sleep(2000);

            MobileElement reasonOption = driver.findElement(MobileBy.xpath("//android.widget.TextView[@text='" + reasonText + "']"));
            reasonOption.click();
            System.out.println(" Selected reason: " + reasonText);

        } catch (Exception e) {
            System.out.println(" Failed to select reason: " + reasonText + " | Error: " + e.getMessage());
        }
    }

    // --- Common navigation steps ---
	*/

    @Test(priority = 7,dependsOnGroups = {"login"},  retryAnalyzer = RetryAnalyzer.class)
    public void testSeven() throws InterruptedException {
        Thread.sleep(8000);
        MobileElement JobBoardSection = driver.findElement(By.xpath(
            "//android.view.View[@content-desc=\"Job Board\"]/com.horcrux.svg.SvgView/com.horcrux.svg.GroupView/com.horcrux.svg.PathView"
        ));
        JobBoardSection.click();
        System.out.println("completed TestSeven..");
    }

    
    @Test(priority = 8, dependsOnMethods = {"testSeven"}, retryAnalyzer = RetryAnalyzer.class)
    public void testEight_ViewOfferDetails() throws InterruptedException {
        Thread.sleep(8000);
        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.8);
        int endY = (int) (size.height * 0.3);

        for (int i = 0; i < 5; i++) {
            try {
                MobileElement el = driver.findElement(By.xpath(
                    "//*[contains(@content-desc,'View Offer Details') or contains(@text,'View Offer Details')]"
                ));
                el.click();
                System.out.println("✅ Clicked 'View Offer Details'");
                // Wait for navigation to complete
                Thread.sleep(5000);
                return;
            } catch (Exception e) {
                new TouchAction<>(driver)
                    .press(PointOption.point(startX, startY))
                    .waitAction(WaitOptions.waitOptions(Duration.ofMillis(800)))
                    .moveTo(PointOption.point(startX, endY))
                    .release()
                    .perform();
                System.out.println("🔄 Swiping up... " + (i + 1));
            }
        }
        Assert.fail(" 'View Offer Details' not found after scrolling");
    }
    
    @Test(priority = 9, dependsOnMethods = {"testEight_ViewOfferDetails"}, retryAnalyzer = RetryAnalyzer.class)
    public void testNine_ProvideFeedback() throws InterruptedException {
        Thread.sleep(6000);
        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;

        try {
            WebDriverWait wait = new WebDriverWait(driver, 20);
            MobileElement feedbackBtn = (MobileElement) wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.Button[@content-desc='Provide Feedback']")
                )
            );
            feedbackBtn.click();
            System.out.println("Clicked 'Provide Feedback'");

            // Wait briefly to allow page transition
            Thread.sleep(5000);

        } catch (Exception e) {
            System.out.println("Error clicking 'Provide Feedback': " + e.getMessage());
            Assert.fail("Provide Feedback failed or app closed unexpectedly.");
        }
    }


    @Test(priority = 10, dependsOnMethods = {"testNine_ProvideFeedback"}, retryAnalyzer = RetryAnalyzer.class)
    public void testTen_EnterFeedbackDetails() throws InterruptedException {
        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;

        try {
            // Wait for feedback screen to load
            WebDriverWait wait = new WebDriverWait(driver, 25);
            MobileElement priceField = (MobileElement) wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.widget.EditText[@content-desc=\"Input Field\" and @text=\"0.00\"]")
                )
            );

            // Enter price
            priceField.sendKeys("400");
            System.out.println("Entered price successfully.");

            // Select material cost checkbox
            MobileElement costCheckbox = driver.findElement(
                By.xpath("//android.view.ViewGroup[@content-desc=\"Material cost\"]/android.view.ViewGroup")
            );
            costCheckbox.click();
            System.out.println("Selected Machining cost checkbox.");

            // Scroll to Submit Feedback button
            try {
                driver.findElement(MobileBy.AndroidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true))" +
                    ".scrollIntoView(new UiSelector().description(\"Submit feedback\"))"
                ));

                MobileElement Submit_feedback = (MobileElement) wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//android.widget.Button[@content-desc=\"Submit feedback\"]")
                    )
                );
                //Submit_feedback.click();
                //System.out.println("Navigated to Job overview page");
            } catch (Exception e) {
                System.out.println("Element not found or error occurred while scrolling: " + e.getMessage());
            }

            // Enter notes
            MobileElement note = driver.findElement(
                By.xpath("//android.widget.EditText[@text='Additional Notes (Optional)']")
            );
            note.sendKeys("Price feedback provided.");
            System.out.println("Entered notes.");

            // Click Submit
            MobileElement submit = driver.findElement(By.xpath("//android.widget.Button[@content-desc=\"Submit feedback\"]"));
            submit.click();
            System.out.println("✅ Submitted feedback successfully.");

        } catch (Exception e) {
            System.out.println("❌ Error filling feedback form: " + e.getMessage());
            Assert.fail("Failed to interact with feedback screen.");
        }
    }

    
    @Test(priority = 11, dependsOnMethods = {"testSeven"}, retryAnalyzer = RetryAnalyzer.class)
    public void testElevan_ViewOfferDetails() throws InterruptedException {
        Thread.sleep(8000);
        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.8);
        int endY = (int) (size.height * 0.3);

        for (int i = 0; i < 5; i++) {
            try {
                MobileElement el = driver.findElement(By.xpath(
                    "//*[contains(@content-desc,'View Offer Details') or contains(@text,'View Offer Details')]"
                ));
                el.click();
                System.out.println("✅ Clicked 'View Offer Details'");
                // Wait for navigation to complete
                Thread.sleep(5000);
                return;
            } catch (Exception e) {
                new TouchAction<>(driver)
                    .press(PointOption.point(startX, startY))
                    .waitAction(WaitOptions.waitOptions(Duration.ofMillis(800)))
                    .moveTo(PointOption.point(startX, endY))
                    .release()
                    .perform();
                System.out.println("🔄 Swiping up... " + (i + 1));
            }
        }
        Assert.fail(" 'View Offer Details' not found after scrolling");
    }
    
    @Test(priority = 12, dependsOnMethods = {"testElevan_ViewOfferDetails"}, retryAnalyzer = RetryAnalyzer.class)
    public void testTwelve_ProvideFeedback() throws InterruptedException {
        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;

        try {
            // Wait for Offer Details page to fully load
            WebDriverWait waitPage = new WebDriverWait(driver, 25);
            waitPage.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(@content-desc,'Provide Feedback') or contains(@text,'Provide Feedback')]")
            ));
            System.out.println("✅ Offer Details page loaded successfully");

            // Now click 'Provide Feedback' button
            WebDriverWait waitBtn = new WebDriverWait(driver, 20);
            MobileElement feedbackBtn = (MobileElement) waitBtn.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.Button[@content-desc='Provide Feedback']")
                )
            );
            feedbackBtn.click();
            System.out.println("✅ Clicked 'Provide Feedback' successfully");

            // Wait for transition to feedback form
            Thread.sleep(8000);

        } catch (Exception e) {
            System.out.println("❌ Error in 'Provide Feedback': " + e.getMessage());
            driver.getPageSource(); // Optional: debug current page content
            Assert.fail("Failed to click 'Provide Feedback' or page did not load properly.");
        }
    }

    @Test(priority = 13, dependsOnMethods = {"testTwelve_ProvideFeedback"}, retryAnalyzer = RetryAnalyzer.class)
    public void testThirteen_LeadTimeTooShort_SelectCurrentDate() throws InterruptedException {
        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;

        try {
            // 🔹 Wait until Feedback screen is actually loaded
            WebDriverWait wait = new WebDriverWait(driver, 50);
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//android.view.ViewGroup[@content-desc=\"Price too low...\"]")
            ));
            System.out.println("✅ Feedback screen loaded successfully");

          
            // Step 1️⃣: Click dropdown
            MobileElement dropdown = driver.findElement(
                By.xpath("//android.view.ViewGroup[@content-desc='Price too low...']")
            );
            dropdown.click();
            System.out.println("📂 Opened reason dropdown popup");

            Thread.sleep(2000);

            // Step 2️⃣: Select “Lead time too short...” from popup
            try {
                MobileElement leadTimeOption = driver.findElement(
                    By.xpath("//android.widget.TextView[@text='Lead time too short...']")
                );
                leadTimeOption.click();
                System.out.println("✅ Selected 'Lead time too short...' from popup");
            } catch (Exception e) {
                System.out.println("⚠️ Option not visible, trying scroll...");
                driver.findElement(MobileBy.AndroidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true))" +
                    ".scrollTextIntoView(\"Lead time too short...\")"
                ));
                MobileElement leadTimeOption = driver.findElement(
                    By.xpath("//android.widget.TextView[@text='Lead time too short...']")
                );
                leadTimeOption.click();
                System.out.println("✅ Selected 'Lead time too short...' after scroll");
            }

            Thread.sleep(2000);

            // Step 3️⃣: Select today's date
            MobileElement dateField = driver.findElement(
                By.xpath("//android.view.ViewGroup[@content-desc='MM/DD/YYYY']")
            );
            dateField.click();
            System.out.println("📅 Opened date picker");

            Thread.sleep(2000);

            String currentDay = String.valueOf(java.time.LocalDate.now().getDayOfMonth());
            MobileElement today = driver.findElement(
                By.xpath("//android.view.View[@text='" + currentDay + "']")
            );
            today.click();
            System.out.println("✅ Selected today's date: " + currentDay);

            MobileElement okButton = driver.findElement(By.id("android:id/button1"));
            okButton.click();
            System.out.println("✅ Date confirmed (OK clicked)");

            Thread.sleep(2000);

            // Step 4️⃣: Enter note and submit
            MobileElement reasonCheckbox = driver.findElement(
                By.xpath("//android.view.ViewGroup[@content-desc='Current capacity']/android.view.ViewGroup")
            );
            reasonCheckbox.click();

            MobileElement note = driver.findElement(
                By.xpath("//android.widget.EditText[@text='Additional Notes (Optional)']")
            );
            note.sendKeys("Selected current date automatically for lead time feedback.");

            MobileElement submit = driver.findElement(
                By.xpath("//android.widget.Button[@content-desc='Submit feedback']")
            );
            submit.click();

            System.out.println("✅ Completed feedback for 'Lead time too short' with current date");

        } catch (Exception e) {
            System.out.println("❌ Failed in Test 13: " + e.getMessage());
            System.out.println(driver.getPageSource());
            Assert.fail("Feedback page not loaded or element missing.");
        }
    }

    @Test(priority = 14, dependsOnMethods = {"testThirteen_LeadTimeTooShort_SelectCurrentDate"}, retryAnalyzer = RetryAnalyzer.class)
    public void testForteen_ViewOfferDetails() throws InterruptedException {
        Thread.sleep(8000);
        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.8);
        int endY = (int) (size.height * 0.3);

        for (int i = 0; i < 5; i++) {
            try {
                MobileElement el = driver.findElement(By.xpath(
                    "//*[contains(@content-desc,'View Offer Details') or contains(@text,'View Offer Details')]"
                ));
                el.click();
                System.out.println("✅ Clicked 'View Offer Details'");
                // Wait for navigation to complete
                Thread.sleep(5000);
                return;
            } catch (Exception e) {
                new TouchAction<>(driver)
                    .press(PointOption.point(startX, startY))
                    .waitAction(WaitOptions.waitOptions(Duration.ofMillis(800)))
                    .moveTo(PointOption.point(startX, endY))
                    .release()
                    .perform();
                System.out.println("🔄 Swiping up... " + (i + 1));
            }
        }
        Assert.fail(" 'View Offer Details' not found after scrolling");
    }
    
    @Test(priority = 15, dependsOnMethods = {"testForteen_ViewOfferDetails"}, retryAnalyzer = RetryAnalyzer.class)
    public void testFifteen_ProvideFeedback() throws InterruptedException {
        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;

        try {
            // Wait for Offer Details page to fully load
            WebDriverWait waitPage = new WebDriverWait(driver, 40);
            waitPage.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(@content-desc,'Provide Feedback') or contains(@text,'Provide Feedback')]")
            ));
            System.out.println("✅ Offer Details page loaded successfully");

            // Now click 'Provide Feedback' button
            WebDriverWait waitBtn = new WebDriverWait(driver, 40);
            MobileElement feedbackBtn = (MobileElement) waitBtn.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.Button[@content-desc='Provide Feedback']")
                )
            );
            feedbackBtn.click();
            System.out.println("✅ Clicked 'Provide Feedback' successfully");

            // Wait for transition to feedback form
            Thread.sleep(8000);

        } catch (Exception e) {
            System.out.println("❌ Error in 'Provide Feedback': " + e.getMessage());
            driver.getPageSource(); // Optional: debug current page content
            Assert.fail("Failed to click 'Provide Feedback' or page did not load properly.");
        }
    }
    
    
 // 🔹 Test Case 3: Manufacturability issue
    @Test(priority = 16, dependsOnMethods = {"testFifteen_ProvideFeedback"}, retryAnalyzer = RetryAnalyzer.class)
    public void testSixteen_Manufacturability() throws InterruptedException {

        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;

        try {
            // 🔹 Wait until Feedback screen is actually loaded
            WebDriverWait wait = new WebDriverWait(driver, 50);
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//android.view.ViewGroup[@content-desc='Price too low...']")
            ));
            System.out.println("✅ Feedback screen loaded successfully");

            // Step 1️⃣: Click dropdown
            MobileElement dropdown = driver.findElement(
                By.xpath("//android.view.ViewGroup[@content-desc='Price too low...']")
            );
            dropdown.click();
            System.out.println("📂 Opened reason dropdown popup");

            Thread.sleep(2000);

            // Step 2️⃣: Select “Manufacturability...” from popup
            try {
                MobileElement manufacturabilityOption = driver.findElement(
                    By.xpath("//android.widget.TextView[@text='Manufacturability...']")
                );
                manufacturabilityOption.click();
                System.out.println("✅ Selected 'Manufacturability...' from popup");
            } catch (Exception e) {
                System.out.println("⚠️ Option not visible, trying scroll...");
                driver.findElement(MobileBy.AndroidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true))" +
                    ".scrollTextIntoView(\"Manufacturability...\")"
                ));
                MobileElement manufacturabilityOption = driver.findElement(
                    By.xpath("//android.widget.TextView[@text='Manufacturability...']")
                );
                manufacturabilityOption.click();
                System.out.println("✅ Selected 'Manufacturability...' after scroll");
            }

            Thread.sleep(2000);

            // Step 3️⃣: Select Radio Button and add note
            MobileElement selectRB = driver.findElement(
                By.xpath("//android.view.ViewGroup[@content-desc='Bend radii too small']/android.view.ViewGroup")
            );
            selectRB.click();
            System.out.println("✅ Selected reason: 'Bend radii too small'");

            // Step 4️⃣: Add note
            MobileElement note = driver.findElement(
                By.xpath("//android.widget.EditText[@text='Additional Notes (Optional)']")
            );
            note.sendKeys("Design complexity causing manufacturing issues.");
            System.out.println("📝 Added note for manufacturability issue");

            // Step 5️⃣: Submit feedback
            MobileElement submit = driver.findElement(
                By.xpath("//android.widget.Button[@content-desc='Submit feedback']")
            );
            submit.click();
            System.out.println("✅ Completed feedback for 'Manufacturability'");

        } catch (Exception e) {
            System.out.println("❌ Error in Manufacturability feedback: " + e.getMessage());
            Assert.fail("Failed to complete 'Manufacturability' feedback due to exception.");
        }
    }

    @Test(priority = 17, dependsOnMethods = {"testSixteen_Manufacturability"}, retryAnalyzer = RetryAnalyzer.class)
    public void testseventeen_ViewOfferDetails() throws InterruptedException {
        Thread.sleep(8000);
        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.8);
        int endY = (int) (size.height * 0.3);

        for (int i = 0; i < 5; i++) {
            try {
                MobileElement el = driver.findElement(By.xpath(
                    "//*[contains(@content-desc,'View Offer Details') or contains(@text,'View Offer Details')]"
                ));
                el.click();
                System.out.println("✅ Clicked 'View Offer Details'");
                // Wait for navigation to complete
                Thread.sleep(5000);
                return;
            } catch (Exception e) {
                new TouchAction<>(driver)
                    .press(PointOption.point(startX, startY))
                    .waitAction(WaitOptions.waitOptions(Duration.ofMillis(800)))
                    .moveTo(PointOption.point(startX, endY))
                    .release()
                    .perform();
                System.out.println("🔄 Swiping up... " + (i + 1));
            }
        }
        Assert.fail(" 'View Offer Details' not found after scrolling");
    }
    
    @Test(priority = 18, dependsOnMethods = {"testseventeen_ViewOfferDetails"}, retryAnalyzer = RetryAnalyzer.class)
    public void testEighteen_AcceptOffer() throws InterruptedException {
        System.out.println("🔹 Starting 'Accept Offer' test");

        // Reuse flow: Already on offer details page
        MobileElement acceptOffer = driver.findElement(By.xpath("//android.widget.Button[@content-desc='Accept Offer']"));
        acceptOffer.click();
        System.out.println("✅ Clicked on 'Accept Offer' button");

        Thread.sleep(2000);
        
        handleOpenAppPopup(driver);

        // Verify confirmation or navigation
        try {
            MobileElement confirmation = driver.findElement(By.xpath("//*[contains(@text, 'Offer accepted')]"));
            System.out.println("🎯 Confirmation message: " + confirmation.getText());
        } catch (Exception e) {
            System.out.println("⚠️ Confirmation not found — possibly different screen flow.");
        }
    }
    
    @Test(priority = 19, dependsOnMethods = {"testEight_ViewOfferDetails"}, retryAnalyzer = RetryAnalyzer.class)
    public void testNineteen_NotInterested() throws InterruptedException {
        System.out.println("🔹 Starting 'Not Interested' test");

        // Reuse common flow
        MobileElement notInterested = driver.findElement(By.xpath("//android.widget.Button[@content-desc='Not Interested']"));
        notInterested.click();
        System.out.println("✅ Clicked on 'Not Interested' button");

        Thread.sleep(2000);
        
        handleOpenAppPopup1(driver);
        

        // Verify success or redirection
        try {
            MobileElement confirmation = driver.findElement(By.xpath("//*[contains(@text, 'Feedback submitted')]"));
            System.out.println("🎯 Not Interested confirmation: " + confirmation.getText());
        } catch (Exception e) {
            System.out.println("⚠️ No confirmation message — may redirect to Job Board.");
        }
    }

    @Test(priority = 20,  groups = "Job_Board", retryAnalyzer = RetryAnalyzer.class)
    public void testTwenty_NotInterested() throws InterruptedException {
        System.out.println("🔹 Starting 'Not Interested' test");
        
        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;

        try {
            driver.findElement(MobileBy.AndroidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))" +
                ".scrollIntoView(new UiSelector().description(\"Line Items\"))"
            ));
           // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebDriverWait wait = new WebDriverWait(driver, 30);
            MobileElement Line_Items = (MobileElement) wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.widget.TextView[@text=\"Line Items\"]")
                )
            );
            
        } catch (Exception e) {
            System.out.println("Element not found or error occurred: " + e.getMessage());
        }
        
        

        // Reuse common flow
        MobileElement Download_All_Files = driver.findElement(By.xpath("//android.widget.TextView[@text=\"Download All Files\"]"));
        		Download_All_Files.click();
        System.out.println(" Clicked on Download All Files button");

        Thread.sleep(2000);
        
        driver.navigate().back();
        driver.navigate().back();
        driver.navigate().back();
        

      
    }
    
    public void handleOpenAppPopup1(AppiumDriver<MobileElement> driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, 30);
           // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

            MobileElement ConfirmButton = (MobileElement) wait.until(ExpectedConditions.elementToBeClickable(
                MobileBy.AndroidUIAutomator("new UiSelector().text(\"Confirm\")")
            ));
            ConfirmButton.click();
        } catch (Exception e) {
            System.out.println("Popup not found or already handled: " + e.getMessage());
        }
    } 
    
    
    
    public void handleOpenAppPopup(AppiumDriver<MobileElement> driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, 30);
           // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

            MobileElement ConfirmButton = (MobileElement) wait.until(ExpectedConditions.elementToBeClickable(
                MobileBy.AndroidUIAutomator("new UiSelector().text(\"Confirm\")")
            ));
            ConfirmButton.click();
        } catch (Exception e) {
            System.out.println("Popup not found or already handled: " + e.getMessage());
        }
    } 
    
    
    

 /*   @Test(priority = 11, dependsOnMethods = {"testNine_ProvideFeedback"}, retryAnalyzer = RetryAnalyzer.class)
    public void testElevan() throws InterruptedException {
        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;

        try {
            // Wait for feedback screen to load
            WebDriverWait wait = new WebDriverWait(driver, 25);
            MobileElement priceField = (MobileElement) wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.widget.EditText[@content-desc=\"Input Field\" and @text=\"0.00\"]")
                )
            );

            // Enter price
            priceField.sendKeys("400");
            System.out.println("Entered price successfully.");

            // Select material cost checkbox
            MobileElement costCheckbox = driver.findElement(
                By.xpath("//android.view.ViewGroup[@content-desc=\"Material cost\"]/android.view.ViewGroup")
            );
            costCheckbox.click();
            System.out.println("Selected Machining cost checkbox.");

            // Scroll to Submit Feedback button
            try {
                driver.findElement(MobileBy.AndroidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true))" +
                    ".scrollIntoView(new UiSelector().description(\"Submit feedback\"))"
                ));

                MobileElement Submit_feedback = (MobileElement) wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//android.widget.Button[@content-desc=\"Submit feedback\"]")
                    )
                );
                //Submit_feedback.click();
                //System.out.println("Navigated to Job overview page");
            } catch (Exception e) {
                System.out.println("Element not found or error occurred while scrolling: " + e.getMessage());
            }

            // Enter notes
            MobileElement note = driver.findElement(
                By.xpath("//android.widget.EditText[@text='Additional Notes (Optional)']")
            );
            note.sendKeys("Price feedback provided.");
            System.out.println("Entered notes.");

            // Click Submit
            MobileElement submit = driver.findElement(By.xpath("//android.widget.Button[@content-desc=\"Submit feedback\"]"));
            submit.click();
            System.out.println("✅ Submitted feedback successfully.");

        } catch (Exception e) {
            System.out.println("❌ Error filling feedback form: " + e.getMessage());
            Assert.fail("Failed to interact with feedback screen.");
        }
    }
*/
    

  
       
	
    /*  // 🔹 Test Case 1: Price too low
     @Test(priority = 10, dependsOnMethods = {"testNine_ProvideFeedback"}, retryAnalyzer = RetryAnalyzer.class)
      public void testTen_PriceTooLow() 
    
    
/*    @Test(priority = 8, dependsOnMethods = {"testSeven"}, retryAnalyzer = RetryAnalyzer.class)
    public void testEight_ViewOfferDetails() throws InterruptedException {
        Thread.sleep(8000);
        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;

        try {
            driver.findElement(MobileBy.AndroidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))" +
                ".scrollIntoView(new UiSelector().description(\"View Offer Details\"))"
            ));

            WebDriverWait wait = new WebDriverWait(driver, 30);
            MobileElement viewDetails = (MobileElement) wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.view.ViewGroup[@content-desc=\"View Offer Details\"]")
                )
            );
            viewDetails.click();
            System.out.println("Navigated to Job overview page");
        } catch (Exception e) {
            System.out.println("Element not found or error occurred: " + e.getMessage());
        }

      //  MobileElement View_Offer_Details = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"View Offer Details\"]"));
       // View_Offer_Details.click();
       // System.out.println("completed TestEight_ViewOfferDetails..");
    }

    @Test(priority = 9, dependsOnMethods = {"testEight_ViewOfferDetails"}, retryAnalyzer = RetryAnalyzer.class)
    public void testNine_ProvideFeedback() throws InterruptedException {
        Thread.sleep(8000);
        MobileElement Provide_Feedback = driver.findElement(By.xpath("//android.widget.Button[@content-desc=\"Provide Feedback\"]"));
        Provide_Feedback.click();
        System.out.println("completed TestNine_ProvideFeedback..");
    }
*/
	
	 
	
	
  /*  // 🔹 Test Case 1: Price too low
   @Test(priority = 10, dependsOnMethods = {"testNine_ProvideFeedback"}, retryAnalyzer = RetryAnalyzer.class)
    public void testTen_PriceTooLow() throws InterruptedException {
    	
    	
        selectReason("Price too low...");

        // Enter price
     // wait for price field to be clickable, click, clear, enter value and verify
        WebDriverWait wait = new WebDriverWait(driver, 25);
        MobileElement enterPrice = (MobileElement) wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//android.widget.EditText[contains(@text,'$') or contains(@content-desc,'price') or contains(@resource-id,'price')]")
            )
        );

        enterPrice.click();         // focus
        enterPrice.clear();         // remove any placeholder
        enterPrice.sendKeys("400"); // type

        // hide keyboard if shown
        try { driver.hideKeyboard(); } catch (Exception ignored) {}

        // verify value; if sendKeys didn't stick, try setValue as fallback
        String current = enterPrice.getText();
        if (current == null || !current.contains("400")) {
            enterPrice.setValue("400");        // alternative for stubborn fields
            try { driver.hideKeyboard(); } catch (Exception ignored) {}
            current = enterPrice.getText();
        }

        System.out.println("Entered price -> " + current);
        Assert.assertTrue(current != null && current.contains("400"), "Price was not entered correctly");

        
        
        
        
        MobileElement EnterPrice = driver.findElement(By.xpath("//android.widget.EditText[contains(@text,'$')]"));
        EnterPrice.sendKeys("400");

        // Select cost checkbox
        MobileElement costCheckbox = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc='Machining cost']/android.view.ViewGroup"));
        costCheckbox.click();

        // Add notes
        MobileElement note = driver.findElement(By.xpath("//android.widget.EditText[@text='Additional Notes (Optional)']"));
        note.sendKeys("Price feedback provided.");

        // Submit
        MobileElement submit = driver.findElement(By.xpath("//android.widget.Button[@content-desc='Submit feedback']"));
        submit.click();

        System.out.println(" Completed feedback for 'Price too low'");
    }

    // 🔹 Test Case 2: Lead time too short
    
    @Test(priority = 11, dependsOnMethods = {"testNine_ProvideFeedback"}, retryAnalyzer = RetryAnalyzer.class)
    public void testEleven_LeadTimeTooShort_SelectCurrentDate() throws InterruptedException {
        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        
        MobileElement dropdown = driver.findElement(
                By.xpath("//android.view.ViewGroup[@content-desc='Price too low...']")
            );
            dropdown.click();
            System.out.println("📂 Opened reason dropdown");

            Thread.sleep(2000); // give time for dropdown options to appear

            // 🔹 Step 2: Select “Lead time too short...”
            try {
                MobileElement leadTimeOption = driver.findElement(
                    By.xpath("//android.widget.TextView[@text='Lead time too short...']")
                );
                leadTimeOption.click();
                System.out.println("✅ Selected 'Lead time too short...' from dropdown");
            } catch (Exception e) {
                System.out.println("⚠️ Option 'Lead time too short...' not found: " + e.getMessage());
                // Optional fallback: scroll if not visible
                driver.findElement(MobileBy.AndroidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true))" +
                    ".scrollTextIntoView(\"Lead time too short...\")"
                ));
                driver.findElement(By.xpath("//android.widget.TextView[@text='Lead time too short...']")).click();
                System.out.println("✅ Selected after scroll");
            }



        //selectReason("Lead time too short...");
        Thread.sleep(3000);

        // 🔹 Step 1: Click date field to open the date picker
        MobileElement Select_Date = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc='MM/DD/YYYY']"));
        Select_Date.click();
        System.out.println("📅 Opened date picker");

        Thread.sleep(2000);

        // 🔹 Step 2: Get current day as string (e.g., "28")
        String currentDay = java.time.LocalDate.now().getDayOfMonth() + "";
        System.out.println("📆 Current day: " + currentDay);

        try {
            // 🔹 Step 3: Click the current day in the calendar
            MobileElement currentDate = driver.findElement(MobileBy.xpath("//android.view.View[@text='" + currentDay + "']"));
            currentDate.click();
            System.out.println("✅ Selected today's date: " + currentDay);
        } catch (Exception e) {
            System.out.println("⚠️ Could not click current day directly, trying fallback...");
            try {
                // fallback: match content-desc if available
                MobileElement currentDateAlt = driver.findElement(MobileBy.AndroidUIAutomator(
                    "new UiSelector().descriptionContains(\"" + currentDay + "\")"
                ));
                currentDateAlt.click();
            } catch (Exception ex) {
                System.out.println("❌ Failed to select current date: " + ex.getMessage());
            }
        }

        Thread.sleep(1000);

        // 🔹 Step 4: Click OK to confirm
        MobileElement okButton = driver.findElement(By.id("android:id/button1"));
        okButton.click();
        System.out.println("✅ Date confirmed (OK clicked)");

        Thread.sleep(2000);

        // 🔹 Step 5: Enter note and submit feedback
        
        MobileElement Select_RB = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"Current capacity\"]/android.view.ViewGroup"));
        Select_RB.click();
        
        
        MobileElement note = driver.findElement(By.xpath("//android.widget.EditText[@text='Additional Notes (Optional)']"));
        note.sendKeys("Selected current date automatically for lead time feedback.");

        MobileElement submit = driver.findElement(By.xpath("//android.widget.Button[@content-desc='Submit feedback']"));
        submit.click();

        System.out.println("✅ Completed feedback for 'Lead time too short' with current date");
       
        
        
        
    }

   

    // 🔹 Test Case 3: Manufacturability issue
    @Test(priority = 12, dependsOnMethods = {"testNine_ProvideFeedback"}, retryAnalyzer = RetryAnalyzer.class)
    public void testTwelve_Manufacturability() throws InterruptedException {
       // selectReason("Manufacturability...");

        // Specific steps for manufacturability
        
        MobileElement Select_RB = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"Bend radii too small\"]/android.view.ViewGroup"));
        Select_RB.click();
        
        MobileElement note = driver.findElement(By.xpath("//android.widget.EditText[@text='Additional Notes (Optional)']"));
        note.sendKeys("Design complexity causing manufacturing issues.");

        MobileElement submit = driver.findElement(By.xpath("//android.widget.Button[@content-desc='Submit feedback']"));
        submit.click();

        System.out.println("✅ Completed feedback for 'Manufacturability'");
    }
    
    @Test(priority = 13, dependsOnMethods = {"testEight_ViewOfferDetails"}, retryAnalyzer = RetryAnalyzer.class)
    public void testThirteen_AcceptOffer() throws InterruptedException {
        System.out.println("🔹 Starting 'Accept Offer' test");

        // Reuse flow: Already on offer details page
        MobileElement acceptOffer = driver.findElement(By.xpath("//android.widget.Button[@content-desc='Accept Offer']"));
        acceptOffer.click();
        System.out.println("✅ Clicked on 'Accept Offer' button");

        Thread.sleep(2000);

        // Verify confirmation or navigation
        try {
            MobileElement confirmation = driver.findElement(By.xpath("//*[contains(@text, 'Offer accepted')]"));
            System.out.println("🎯 Confirmation message: " + confirmation.getText());
        } catch (Exception e) {
            System.out.println("⚠️ Confirmation not found — possibly different screen flow.");
        }
    }

    @Test(priority = 14, dependsOnMethods = {"testEight_ViewOfferDetails"}, retryAnalyzer = RetryAnalyzer.class)
    public void testForteen_NotInterested() throws InterruptedException {
        System.out.println("🔹 Starting 'Not Interested' test");

        // Reuse common flow
        MobileElement notInterested = driver.findElement(By.xpath("//android.widget.Button[@content-desc='Not Interested']"));
        notInterested.click();
        System.out.println("✅ Clicked on 'Not Interested' button");

        Thread.sleep(2000);

        // Verify success or redirection
        try {
            MobileElement confirmation = driver.findElement(By.xpath("//*[contains(@text, 'Feedback submitted')]"));
            System.out.println("🎯 Not Interested confirmation: " + confirmation.getText());
        } catch (Exception e) {
            System.out.println("⚠️ No confirmation message — may redirect to Job Board.");
        }
    }

    @Test(priority = 15,  retryAnalyzer = RetryAnalyzer.class)
    public void testFifteen_NotInterested() throws InterruptedException {
        System.out.println("🔹 Starting 'Not Interested' test");
        
        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;

        try {
            driver.findElement(MobileBy.AndroidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))" +
                ".scrollIntoView(new UiSelector().description(\"Line Items\"))"
            ));
           // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebDriverWait wait = new WebDriverWait(driver, 30);
            MobileElement Line_Items = (MobileElement) wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.widget.TextView[@text=\"Line Items\"]")
                )
            );
            
        } catch (Exception e) {
            System.out.println("Element not found or error occurred: " + e.getMessage());
        }
        
        

        // Reuse common flow
        MobileElement Download_All_Files = driver.findElement(By.xpath("//android.widget.TextView[@text=\"Download All Files\"]"));
        		Download_All_Files.click();
        System.out.println(" Clicked on Download All Files button");

        Thread.sleep(2000);
        
        driver.navigate().back();
        driver.navigate().back();
        driver.navigate().back();
        

      
    }
    
    */
    
    
}
