package com.bs.pageobjects;

import org.openqa.selenium.WebElement;

import io.appium.java_client.pagefactory.AndroidFindBy;

public class Login_page_objects {
	
	@AndroidFindBy(xpath ="//android.view.ViewGroup[@content-desc=\\\"Login\\\"]")
	private WebElement loginButton;
	public Login_page_objects click_login_Button( WebElement loginButton) {
		
		click(loginButton);
		return this;
	}
	private void click(WebElement loginButton2) {
		// TODO Auto-generated method stub
		
	}

	

}
