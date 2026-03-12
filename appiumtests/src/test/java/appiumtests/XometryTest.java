package appiumtests;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;

public class XometryTest {

    static AndroidDriver<MobileElement> driver;

    public static void main(String[] args) {
        try {
            startApp();
        } catch (Exception exp) {
            System.out.println("❌ ERROR: " + exp.getMessage());
            exp.printStackTrace();
        }
    }

    public static void startApp() throws MalformedURLException, InterruptedException {

        // Set up device capabilities
        DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability("deviceName", "vivo 1916");
        cap.setCapability("udid", "6609da97");
        cap.setCapability("platformName", "Android");
        cap.setCapability("platformVersion", "9");
        cap.setCapability("appPackage", "com.xometry.workcenter.preview.stage");
        cap.setCapability("appActivity", ".MainActivity");
        cap.setCapability("noReset", false);
        cap.setCapability("fullReset", false);
        cap.setCapability("automationName", "UiAutomator2");
        cap.setCapability("adbExecTimeout", 120000);
        cap.setCapability("app", "C:\\Users\\subhashohal\\Downloads\\base.apk");

        // Start Appium session
        URL url = new URL("http://127.0.0.1:4723/wd/hub");
        driver = new AndroidDriver<>(url, cap);

        System.out.println("✅ Application started successfully!");
        Thread.sleep(8000);

        // Tap Login (native view)
        MobileElement loginButton = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"Login\"]"));
        loginButton.click();
        System.out.println("➡️ Clicked Login button.");
        Thread.sleep(8000);

        // List available contexts
        Set<String> contexts = driver.getContextHandles();
        System.out.println("🌐 Available contexts:");
        for (String context : contexts) {
            System.out.println("👉 " + context);
        }

        // Try switching to WebView
        boolean webViewFound = false;
        for (String context : contexts) {
            if (context.toLowerCase().contains("webview")) {
                driver.context(context);
                System.out.println("✅ Switched to WebView: " + context);
                webViewFound = true;
                break;
            }
        }

        if (!webViewFound) {
            System.out.println("❌ WebView context not found. Make sure WebView debugging is enabled in the app.");
            driver.quit();
            return;
        }

        Thread.sleep(5000);

        // Fill in email using WebView XPath
        try {
            WebElement emailField = driver.findElement(By.xpath("//input[@placeholder='Business Email']"));
            emailField.sendKeys("gsage@sylvarant.com");
            System.out.println("✅ Email entered.");
        } catch (Exception e) {
            System.out.println("❌ Failed to locate email field: " + e.getMessage());
            driver.quit();
            return;
        }

        // Click "Continue" button
        try {
            WebElement continueBtn1 = driver.findElement(By.xpath("//button[contains(text(),'Continue')]"));
            continueBtn1.click();
            System.out.println("➡️ Clicked Continue after email.");
        } catch (Exception e) {
            System.out.println("❌ Failed to locate Continue button: " + e.getMessage());
            driver.quit();
            return;
        }

        Thread.sleep(5000);

        // Fill in password using placeholder
        try {
            WebElement passwordField = driver.findElement(By.xpath("//input[@placeholder='Password*']"));
            passwordField.sendKeys("bubbles101");
            System.out.println("✅ Password entered.");
        } catch (Exception e) {
            System.out.println("❌ Failed to locate password field: " + e.getMessage());
            driver.quit();
            return;
        }

        // Click "Continue" again
        try {
            WebElement continueBtn2 = driver.findElement(By.xpath("//button[contains(text(),'Continue')]"));
            continueBtn2.click();
            System.out.println("➡️ Clicked Continue after password.");
        } catch (Exception e) {
            System.out.println("❌ Failed to click Continue after password: " + e.getMessage());
        }

        Thread.sleep(5000);

        // Switch back to native context if needed
        driver.context("NATIVE_APP");
        driver.closeApp();
        System.out.println("✅ Login test completed and app closed.");
    }
}
