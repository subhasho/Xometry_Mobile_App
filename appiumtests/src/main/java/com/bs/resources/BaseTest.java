package com.bs.resources;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import com.bs.config.Config;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

public class BaseTest {
	
	public  static Properties prop;
	
	
	@Parameters({"deviceName","udid","platformName","platformVersion"})
	@BeforeClass
	public void setup(String deviceName,String udid,String platformName,String platformVersion ) throws IOException {
		
		prop=new Properties();
		FileInputStream fis= FileInputStream(System.getProperty("user.dir")+"src/main/resources/config.properties");
		prop.load(fis);
		
		try {
		
		  // Set up device capabilities
        DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability("deviceName",deviceName );
        cap.setCapability("udid",udid );
        cap.setCapability("platformName", platformName);
        cap.setCapability("platformVersion", platformVersion);
        cap.setCapability("appPackage", prop.getProperty("AndroidAppPackageName"));
        cap.setCapability("appActivity", prop.getProperty("AndroidAppActivityName"));
        cap.setCapability("noReset", false);
        cap.setCapability("fullReset", false);
        cap.setCapability("automationName", prop.getProperty("AndroidAppautomationName"));
        cap.setCapability("adbExecTimeout", 120000);
        cap.setCapability("app", "C:\\Users\\subhashohal\\Downloads\\base.apk");
        cap.setCapability("uiautomator2ServerLaunchTimeout", 60000);
        cap.setCapability("uiautomator2ServerInstallTimeout", 60000);
        cap.setCapability("newCommandTimeout", 300);

        URL url = new URL(prop.getProperty("AppiumURL"));
       Config.driver =new AppiumDriver<MobileElement>(url,cap);
        
        //driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        //driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
       Config. driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        System.out.println("Appium driver started with implicit wait");
        
		}catch(Exception Exp){
			System.out.println("cause is :"+Exp.getCause());
			System.out.println("Message is"+Exp.getMessage());
			Exp.printStackTrace();
			
			
		}
		
		
		
	}

	private FileInputStream FileInputStream(String string) {
		// TODO Auto-generated method stub
		return null;
	}

}
