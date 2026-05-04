package Tests;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import utils.TakeScreenshot;

public class BaseClass {

    public static AppiumDriver<MobileElement> driver;
    private static boolean isDriverStarted = false;
    private static long lastCommandTime = System.currentTimeMillis();

    /**
     * Called from {@link AppiumSuiteListener} once per suite (not per test class).
     */
    public static synchronized void createDriverOnce() {
        if (isDriverStarted && driver != null) {
            try {
                if (driver.getSessionId() != null) {
                    System.out.println("♻️ Appium driver already active");
                    return;
                }
            } catch (Exception ignored) {
                isDriverStarted = false;
                driver = null;
            }
        }
        startDriverInternal();
    }

    public static synchronized void quitDriverOnce() {
        try {
            if (driver != null) {
                System.out.println("✅ Closing Appium driver after suite...");
                driver.quit();
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error while closing driver: " + e.getMessage());
        } finally {
            driver = null;
            isDriverStarted = false;
        }
    }

    /** First serial with state {@code device} from {@code adb devices}, or null. */
    static String firstAuthorizedDeviceFromAdb() {
        try {
            Process proc = new ProcessBuilder("adb", "devices")
                .redirectErrorStream(true)
                .start();
            String line;
            try (BufferedReader r = new BufferedReader(
                new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8))) {
                while ((line = r.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("List of")) {
                        continue;
                    }
                    String[] parts = line.split("\\s+");
                    if (parts.length >= 2 && "device".equals(parts[1])) {
                        return parts[0];
                    }
                }
            }
            proc.waitFor(15, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.err.println("Could not read adb devices: " + e.getMessage());
        }
        return null;
    }

    private static void startDriverInternal() {
        if (driver == null) {
            isDriverStarted = false;
        }
        try {
            if (!isDriverStarted) {
                DesiredCapabilities cap = new DesiredCapabilities();
                String udid = System.getenv("APPIUM_UDID");
                if (udid == null || udid.isBlank()) {
                    udid = firstAuthorizedDeviceFromAdb();
                }
                if (udid == null || udid.isBlank()) {
                    throw new RuntimeException(
                        "No device UDID: set env APPIUM_UDID or connect one USB device with adb authorized (adb devices).");
                }
                cap.setCapability("udid", udid.trim());

                String deviceName = System.getenv("APPIUM_DEVICE_NAME");
                if (deviceName == null || deviceName.isBlank()) {
                    deviceName = udid;
                }
                cap.setCapability("deviceName", deviceName);

                cap.setCapability("platformName", System.getenv().getOrDefault("APPIUM_PLATFORM_NAME", "Android"));
                String platVer = System.getenv("APPIUM_PLATFORM_VERSION");
                if (platVer != null && !platVer.isBlank()) {
                    cap.setCapability("platformVersion", platVer);
                }

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
                System.out.println("✅ Appium driver started (udid=" + udid + ", url=" + appiumUrl + ")");
            } else {
                System.out.println("♻️ Reusing existing Appium driver session");
            }
        } catch (Exception e) {
            System.out.println("❌ Driver setup failed: " + e.getMessage());
            e.printStackTrace();
            isDriverStarted = false;
            driver = null;
            throw new RuntimeException(
                "Appium driver failed to start. Check APPIUM_URL, device in adb devices, Appium server, and APK_PATH.",
                e);
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void ensureAppSessionAlive() {
        try {
            if (driver == null || driver.getSessionId() == null) {
                System.out.println("⚠️ Driver session lost — recreating driver...");
                synchronized (BaseClass.class) {
                    isDriverStarted = false;
                    startDriverInternal();
                }
            } else {
                long now = System.currentTimeMillis();
                if ((now - lastCommandTime) > 120000) {
                    driver.getPageSource();
                    lastCommandTime = now;
                    System.out.println("🔄 Session kept alive");
                }
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("⚠️ Session check failed: " + e.getMessage());
        }
    }

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

    public MobileElement waitForElement(By locator, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        return (MobileElement) wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

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

    public boolean isAppRunning() {
        try {
            return driver != null && driver.getSessionId() != null;
        } catch (Exception e) {
            return false;
        }
    }
}
