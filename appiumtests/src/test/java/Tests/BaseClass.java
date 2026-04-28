package Tests;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import utils.TakeScreenshot;

public class BaseClass {

    public static AppiumDriver<MobileElement> driver;
    private static boolean isDriverStarted = false;
    private static long lastCommandTime = System.currentTimeMillis();

    // ✅ Start Appium driver once before the entire suite
    @BeforeSuite(alwaysRun = true)
    public void setup() {
        startDriver();
    }

    // ✅ Initialize driver (launch app only once)
    private void startDriver() {
        try {
            if (!isDriverStarted) { // ✅ Launch only once
                DesiredCapabilities cap = new DesiredCapabilities();
                cap.setCapability("deviceName", System.getenv().getOrDefault("APPIUM_DEVICE_NAME", "vivo 1916"));
                cap.setCapability("udid", System.getenv().getOrDefault("APPIUM_UDID", "6609da97"));
                cap.setCapability("platformName", System.getenv().getOrDefault("APPIUM_PLATFORM_NAME", "Android"));
                cap.setCapability("platformVersion", System.getenv().getOrDefault("APPIUM_PLATFORM_VERSION", "9"));
                cap.setCapability("appPackage", "com.xometry.workcenter.preview.stage");
                cap.setCapability("appActivity", "com.xometry.workcenter.preview.stage.MainActivity");
                cap.setCapability("noReset", false);
                cap.setCapability("fullReset", false);
                cap.setCapability("automationName", "UiAutomator2");
                cap.setCapability("adbExecTimeout", 180000);
                cap.setCapability("uiautomator2ServerLaunchTimeout", 90000);
                cap.setCapability("uiautomator2ServerInstallTimeout", 90000);
                cap.setCapability("newCommandTimeout", 1200);
                String apkPathEnv = System.getenv("APK_PATH");
                if (apkPathEnv != null && !apkPathEnv.isEmpty()) {
                    Path apkUser = Paths.get(apkPathEnv);
                    if (Files.isRegularFile(apkUser)) {
                        cap.setCapability("app", apkUser.toAbsolutePath().toString());
                    }
                } else {
                    Path apk = Paths.get("C:\\Users\\subhashohal\\Downloads\\base.apk");
                    if (Files.isRegularFile(apk)) {
                        cap.setCapability("app", apk.toAbsolutePath().toString());
                    }
                }

                String appiumUrl = System.getenv("APPIUM_URL");
                if (appiumUrl == null || appiumUrl.isEmpty()) {
                    String host = System.getenv().getOrDefault("APPIUM_HOST", "127.0.0.1");
                    String port = System.getenv().getOrDefault("APPIUM_PORT", "4723");
                    String path = System.getenv().getOrDefault("APPIUM_BASE_PATH", "/wd/hub");
                    if (!path.startsWith("/")) {
                        path = "/" + path;
                    }
                    appiumUrl = "http://" + host + ":" + port + path;
                }
                URL url = new URL(appiumUrl);
                driver = new AndroidDriver<>(url, cap);
                driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

                isDriverStarted = true;
                System.out.println("✅ Appium driver started successfully (only once)");
            } else {
                System.out.println("♻️ Reusing existing Appium driver session");
            }
        } catch (Exception e) {
            System.out.println("❌ Driver setup failed: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // ✅ Just verify driver session (don’t restart app)
    @BeforeMethod(alwaysRun = true)
    public void ensureAppSessionAlive() {
        try {
            if (driver == null || driver.getSessionId() == null) {
                System.out.println("⚠️ Driver session lost — restarting driver...");
                startDriver();
            } else {
                long now = System.currentTimeMillis();
                if ((now - lastCommandTime) > 120000) {
                    driver.getPageSource(); // keep session alive
                    lastCommandTime = now;
                    System.out.println("🔄 Session kept alive");
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Session check failed: " + e.getMessage());
        }
    }

    // ✅ Close driver once after all tests are complete
    @AfterSuite(alwaysRun = true)
    public void teardown() {
        try {
            if (driver != null) {
                System.out.println("✅ Closing Appium driver after all tests...");
                driver.quit();
                driver = null;
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error while closing driver: " + e.getMessage());
        }
    }

    // ✅ Capture screenshot on test failure
    @AfterMethod(alwaysRun = true)
    public void captureOnFailure(ITestResult result) {
        try {
            if (result.getStatus() == ITestResult.FAILURE && driver != null) {
                TakeScreenshot.capture(driver, result.getName());
                System.out.println("📸 Screenshot captured for failed test: " + result.getName());
            }
        } catch (Exception e) {
            System.out.println("⚠️ Screenshot capture failed: " + e.getMessage());
        }
    }

    // ✅ Utility: wait for element visibility
    public MobileElement waitForElement(By locator, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        return (MobileElement) wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // ✅ Handle runtime "Allow" popup
    public void handleOpenAppPopup() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, 5);
            MobileElement allowButton = (MobileElement) wait.until(
                ExpectedConditions.elementToBeClickable(
                    MobileBy.AndroidUIAutomator("new UiSelector().text(\"Allow\")")
                )
            );
            allowButton.click();
            System.out.println("✅ Handled 'Allow' popup");
        } catch (Exception e) {
            // no popup, ignore
        }
    }

    // ✅ Utility: check if driver is still valid
    public boolean isAppRunning() {
        try {
            return driver != null && driver.getSessionId() != null;
        } catch (Exception e) {
            return false;
        }
    }
}
