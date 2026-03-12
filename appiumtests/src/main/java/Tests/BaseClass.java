package Tests;

import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;

public class BaseClass {

    public static AppiumDriver<MobileElement> driver;

    @BeforeClass
    public void setup() {
        try {
            DesiredCapabilities cap = new DesiredCapabilities();
            cap.setCapability("deviceName", "vivo 1916");
            cap.setCapability("udid", "6609da97");
            cap.setCapability("platformName", "Android");
            cap.setCapability("platformVersion", "9");
            cap.setCapability("appPackage", "com.xometry.workcenter.preview.stage");
            cap.setCapability("appActivity", "com.xometry.workcenter.preview.stage.MainActivity");
            cap.setCapability("noReset", false);
            cap.setCapability("fullReset", false);
            cap.setCapability("automationName", "UiAutomator2");
            cap.setCapability("adbExecTimeout", 120000);
            cap.setCapability("uiautomator2ServerLaunchTimeout", 60000);
            cap.setCapability("uiautomator2ServerInstallTimeout", 60000);
            cap.setCapability("newCommandTimeout", 300);
            cap.setCapability("app", "C:\\Users\\subhashohal\\Downloads\\base.apk");

            URL url = new URL("http://127.0.0.1:4723/wd/hub");
            driver = new AndroidDriver<>(url, cap);
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            System.out.println("Appium driver started with implicit wait");

        } catch (Exception e) {
            System.out.println("Cause: " + e.getCause());
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void sampleTest() {
        System.out.println("Inside sample test");
    }

    @AfterTest
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
