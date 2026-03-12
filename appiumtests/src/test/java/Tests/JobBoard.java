package Tests;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
//import io.appium.java_client.AppiumBy;



public class JobBoard extends Tests{
	
	
	
	@Test(priority = 11)
	public void testElevan()  {
				
		   
		// Thread.sleep(8000);
  
		  MobileElement JobBoradSection = driver.findElement(By.xpath("//android.view.View[@content-desc=\"Job Board\"]/com.horcrux.svg.SvgView/com.horcrux.svg.GroupView/com.horcrux.svg.PathView"));
		  JobBoradSection.click();
	      
	      	      
	        //Thread.sleep(8000);
	        System.out.println("completed Testone..");

}
	
	@Test(priority = 2)
	public void testtwo()  {
				
		   
		// Thread.sleep(8000);
		
		
		AndroidDriver<MobileElement> androidDriver = (AndroidDriver<MobileElement>) driver;

		MobileElement element = androidDriver.findElementByAndroidUIAutomator(
		    "new UiScrollable(new UiSelector().scrollable(true))" +
		    ".scrollIntoView(new UiSelector().text(\"Job Board\"));"
		);

		element.click();

		
	      
	      	      
	        //Thread.sleep(8000);
	        System.out.println("completed Testwo..");

}
	
	@Test
    public void testTwelve() throws InterruptedException {
        Thread.sleep(8000);
        AndroidDriver<MobileElement> driver = (AndroidDriver<MobileElement>) this.driver;
        try {
            driver.findElement(MobileBy.AndroidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))" +
                ".scrollIntoView(new UiSelector().description(\"View Offer Details\"))"
            ));

            WebDriverWait wait = new WebDriverWait(driver, 30);
            MobileElement View_Offer_Details = (MobileElement) wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.view.ViewGroup[@content-desc=\"View Offer Details\"]")
                )
            );
            View_Offer_Details.click();
            System.out.println("Navigated to offer detail page");
        } catch (Exception e) {
            System.out.println("Element not found or error occurred: " + e.getMessage());
        }
    }	
	
	
	
	
	
	
	
}  

	
