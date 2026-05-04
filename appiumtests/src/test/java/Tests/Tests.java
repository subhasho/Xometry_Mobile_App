package Tests;

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
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;

import java.time.Duration;
import java.util.List;

public class Tests extends BaseClass {

    @Test(priority = 1, retryAnalyzer = RetryAnalyzer.class)
    public void testone() {
        MobileElement loginButton = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"Log In\"]"));
        loginButton.click();
        System.out.println("completed Testone..");
    }

    @Test(priority = 2, dependsOnMethods = {"testone"}, alwaysRun = true, retryAnalyzer = RetryAnalyzer.class)
    public void testtwo() {
        MobileElement emailField = driver.findElement(By.xpath("//android.widget.EditText"));
        emailField.sendKeys("gsage@sylvarant.com");
        System.out.println("completed Testtwo..");
    }

    @Test(priority = 3, dependsOnMethods = {"testtwo"}, alwaysRun = true, retryAnalyzer = RetryAnalyzer.class)
    public void testthree() {
        MobileElement continueBtn = driver.findElement(By.xpath("//android.widget.Button[@text=\"Continue\"]"));
        continueBtn.click();
        System.out.println("completed Testthree..");
    }

    @Test(priority = 4, dependsOnMethods = {"testthree"}, alwaysRun = true, retryAnalyzer = RetryAnalyzer.class)
    public void testFour() throws InterruptedException {
        Thread.sleep(2000);
        AndroidDriver<MobileElement> ad = (AndroidDriver<MobileElement>) driver;
        try {
            ad.hideKeyboard();
        } catch (Exception ignored) {
        }
        Thread.sleep(500);

        // Do not use //android.widget.EditText alone: after "Continue" the email field can still
        // be first in the tree, so sendKeys hits the wrong box or the IME blocks input. Prefer the
        // password field (usually last EditText) or an explicit Password hint.
        WebDriverWait wait = new WebDriverWait(driver, 25);
        By byPasswordHint = By.xpath(
            "//android.widget.EditText[@password='true' or contains(@hint,'Password') or contains(@hint,'password')]");
        List<MobileElement> hinted = driver.findElements(byPasswordHint);
        MobileElement passwordField;
        if (!hinted.isEmpty()) {
            passwordField = (MobileElement) wait.until(ExpectedConditions.elementToBeClickable(byPasswordHint));
        } else {
            // Email field often stays in the hierarchy; password step is usually the last EditText.
            passwordField = (MobileElement) wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//android.widget.EditText)[last()]")));
        }
        passwordField.click();
        Thread.sleep(400);
        try {
            passwordField.clear();
        } catch (Exception ignored) {
        }
        passwordField.sendKeys("bubbles101");
        try {
            ad.hideKeyboard();
        } catch (Exception ignored) {
        }
        System.out.println("completed TestFour..");
    }
    @Test(priority = 5, dependsOnMethods = {"testFour"}, alwaysRun = true, retryAnalyzer = RetryAnalyzer.class)
    public void testFive() {
        WebDriverWait wait = new WebDriverWait(driver, 30);
        MobileElement continueBtn1 = (MobileElement) wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("(//android.widget.Button[@text=\"Continue\"])[last()]")));
        continueBtn1.click();
        System.out.println("completed TestFive..");
    }

    @Test(priority = 6, groups = "login", dependsOnMethods = {"testFive"}, alwaysRun = true, retryAnalyzer = RetryAnalyzer.class)
    public void testsix() throws InterruptedException {
        Thread.sleep(3000);
        handleOpenAppPopup(driver);
        Thread.sleep(3000);
        System.out.println("completed Testsix..");
    }

    @Test(priority = 7, dependsOnMethods = {"testsix"}, alwaysRun = true, retryAnalyzer = RetryAnalyzer.class)
    public void testSeven() throws InterruptedException {
        Thread.sleep(8000);
        MobileElement jobBoardSection = driver.findElement(By.xpath(
            "//android.view.View[@content-desc=\"Job Board\"]/com.horcrux.svg.SvgView/com.horcrux.svg.GroupView/com.horcrux.svg.PathView"
        ));
        jobBoardSection.click();
        System.out.println("completed TestSeven..");
    }

    @Test(priority = 8, dependsOnMethods = {"testSeven"}, alwaysRun = true, retryAnalyzer = RetryAnalyzer.class)
    public void testEight_ViewOfferDetails() throws InterruptedException {
        Thread.sleep(8000);
        AndroidDriver<MobileElement> ad = (AndroidDriver<MobileElement>) this.driver;
        Dimension size = ad.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.8);
        int endY = (int) (size.height * 0.3);

        for (int i = 0; i < 5; i++) {
            try {
                MobileElement el = ad.findElement(By.xpath(
                    "//*[contains(@content-desc,'View Offer Details') or contains(@text,'View Offer Details')]"
                ));
                el.click();
                System.out.println("✅ Clicked 'View Offer Details'");
                Thread.sleep(5000);
                return;
            } catch (Exception e) {
                new TouchAction<>(ad)
                    .press(PointOption.point(startX, startY))
                    .waitAction(WaitOptions.waitOptions(Duration.ofMillis(800)))
                    .moveTo(PointOption.point(startX, endY))
                    .release()
                    .perform();
                System.out.println("🔄 Swiping up... " + (i + 1));
            }
        }
        Assert.fail("❌ 'View Offer Details' not found after scrolling");
    }

    @Test(priority = 9, dependsOnMethods = {"testEight_ViewOfferDetails"}, alwaysRun = true, retryAnalyzer = RetryAnalyzer.class)
    public void testNine_ProvideFeedback() throws InterruptedException {
        Thread.sleep(8000);
        WebDriverWait wait = new WebDriverWait(driver, 20);
        MobileElement feedbackBtn = (MobileElement) wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//android.widget.Button[@content-desc='Provide Feedback']")
            )
        );
        feedbackBtn.click();
        System.out.println("completed TestNine_ProvideFeedback..");
    }

    @Test(priority = 10, dependsOnMethods = {"testNine_ProvideFeedback"}, alwaysRun = true, retryAnalyzer = RetryAnalyzer.class)
    public void testTen_JobManagement() throws InterruptedException {
        Thread.sleep(8000);
        MobileElement jobManagementSection = driver.findElement(By.xpath(
            "//android.view.View[@content-desc=\"Job Management\"]/com.horcrux.svg.SvgView/com.horcrux.svg.GroupView/com.horcrux.svg.PathView"
        ));
        jobManagementSection.click();
        System.out.println("completed TestTen_JobManagement..");
    }

    @Test(priority = 11, dependsOnMethods = {"testTen_JobManagement"}, alwaysRun = true, retryAnalyzer = RetryAnalyzer.class)
    public void testEleven_SearchJob() throws InterruptedException {
        Thread.sleep(8000);
        MobileElement searchJobs = driver.findElement(By.xpath("//android.widget.EditText[@text=\"Search Jobs\"]"));
        searchJobs.sendKeys("J004CD16");
        System.out.println("valid search input ");
        System.out.println("completed TestEleven_SearchJob..");
    }

    @Test(priority = 12, dependsOnMethods = {"testEleven_SearchJob"}, alwaysRun = true, retryAnalyzer = RetryAnalyzer.class)
    public void testTwelve_ViewDetails() throws InterruptedException {
        Thread.sleep(8000);
        AndroidDriver<MobileElement> ad = (AndroidDriver<MobileElement>) this.driver;

        ad.findElement(MobileBy.AndroidUIAutomator(
            "new UiScrollable(new UiSelector().scrollable(true))"
                + ".scrollIntoView(new UiSelector().description(\"View Details\"))"
        ));

        WebDriverWait wait = new WebDriverWait(ad, 30);
        MobileElement viewDetails = (MobileElement) wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//android.view.ViewGroup[@content-desc='View Details']")
            )
        );
        viewDetails.click();
        System.out.println("Navigated to Job overview page");
    }

    //  Add this method to fix the missing method error
    public void handleOpenAppPopup(AppiumDriver<MobileElement> driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, 30);
           // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

            MobileElement allowButton = (MobileElement) wait.until(ExpectedConditions.elementToBeClickable(
                MobileBy.AndroidUIAutomator("new UiSelector().text(\"Allow\")")
            ));
            allowButton.click();
        } catch (Exception e) {
            System.out.println("Popup not found or already handled: " + e.getMessage());
        }
    } 
}
