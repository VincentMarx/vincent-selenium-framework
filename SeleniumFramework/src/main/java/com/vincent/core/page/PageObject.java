package com.vincent.core.page;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.safari.SafariDriver;

import com.vincent.core.config.Config;
import com.vincent.core.execution.Context;
import com.vincent.core.testcase.Status;

public abstract class PageObject {
	protected static final Log log = LogFactory.getLog(PageObject.class);
	protected Context context;

	/*
	 * Open a new browser
	 */

	protected WebDriver newDriver(final boolean isInit) {
		WebDriver driver = null;
		switch (Config.browserType) {
		case Firefox:
			// open firefox browser
			FirefoxProfile profile = new FirefoxProfile(new File(Config.firefoxProfile));
			driver = new FirefoxDriver(profile);
			break;
		case IE:
			// open IE browser
			driver = new InternetExplorerDriver();
			break;
		case Chrome:
			// open Chrome browser
			driver = new ChromeDriver();
			break;
		case Safari:
			driver = new SafariDriver();
			// open safari browser
			break;
		default:
			break;
		}
		context.setDriver(driver);
		return driver;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	protected String getValue(String fieldName) {
		return context.getStepData().get(fieldName);
	}

	protected void report(Status status, String remark) {
		context.getReport().write(status, remark);
	}

	protected void report(boolean flag, String remark) {
		if (flag) {
			context.getReport().write(Status.Pass, remark);
		} else {
			context.getReport().write(Status.Fail, remark);
		}
	}

}
