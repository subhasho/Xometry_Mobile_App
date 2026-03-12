package utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import utils.TakeScreenshot;


public class TakeScreenshot {

    public static String capture(WebDriver driver, String testName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String screenshotDir = System.getProperty("user.dir") + "/Screenshots/";
        String screenshotPath = screenshotDir + testName + "_" + timestamp + ".png";

        File dir = new File(screenshotDir);
        if (!dir.exists()) {
            dir.mkdirs(); // ✅ creates folder if missing
        }

        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(src, new File(screenshotPath));
            System.out.println("📸 Screenshot saved at: " + screenshotPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return screenshotPath;
    }
}
