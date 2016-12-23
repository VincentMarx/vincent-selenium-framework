package com.vincent.core.page;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
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

	protected boolean setValue(String fieldName, String value) {
		try {
			By by = getBy(fieldName);
			return setValue(by, value);
		} catch (Exception e) {
			log.error("field " + fieldName + " was not defined.");
			log.error(e.getMessage(), e);
			return false;
		}
	}

	protected boolean setValue(By byField, String value) {
		return setValue(null, byField, value);
	}

	protected boolean setValue(WebElement parent, By byField, String value) {
		WebElement field;
		if (parent != null) {
			field = parent.findElement(byField);
		} else {
			field = context.getDriver().findElement(byField);
		}
		if (field == null) {
			log.error("field was not found! " + byField.toString());
		}
		return setValue(field, value);
	}

	protected boolean setValue(WebElement field, String value) {
		field.clear();
		field.sendKeys(value);
		return true;
	}

	/*
	 * Open a new browser
	 */

	protected WebDriver newDriver(final boolean isInit) {
		WebDriver driver = null;
		switch (Config.browserType) {
		case Firefox:
			// open firefox browser
			// FirefoxProfile profile = new FirefoxProfile(new
			// File(Config.firefoxProfile));
			// driver = new FirefoxDriver(profile);
			FirefoxBinary binary = new FirefoxBinary(new File(Config.firefoxPath));
			FirefoxProfile profile = new FirefoxProfile();
			driver = new FirefoxDriver(binary, profile);
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
		driver.manage().timeouts().implicitlyWait(Config.implicitlyWaitTimeoutSeconds, TimeUnit.SECONDS);
		context.setDriver(driver);
		return driver;
	}

	protected String getData(String key) {
		return context.getData(key);
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
		takeScreenshot();
		context.getReport().write(status, remark);
	}

	protected void report(boolean flag, String remark) {
		takeScreenshot();
		if (flag) {
			context.getReport().write(Status.Pass, remark);
		} else {
			context.getReport().write(Status.Fail, remark);
		}
	}

	protected void clickObject(By byObj) {
		log.debug("clickObject | " + byObj.toString());
		context.getDriver().findElement(byObj).click();
	}

	protected List<WebElement> getRows(By byTable) {
		WebElement table = findElement(byTable);
		List<WebElement> rows = table.findElements(By.xpath("//tbody/tr"));
		return rows;
	}

	protected WebElement findElement(By by) {
		return findElement(null, by);
	}

	protected WebElement findElement(WebElement parent, By by) {
		if (parent == null) {
			return context.getDriver().findElement(by);
		} else {
			return parent.findElement(by);
		}
	}

	protected boolean waitElement(By by) {
		try {
			this.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	private By getBy(String objName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		Method byMethod = this.getClass().getMethod(objName);
		Object obj = byMethod.invoke(this);
		return (By) obj;
	}

	private void takeScreenshot() {
		WebDriver driver = context.getDriver();
		File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		String stepName = context.getReport().getCurrentStepName();
		int checkPointIndex = context.getReport().checkPointIndex();
		String outputPath = context.getReport().getTestCaseOutputPath() + File.separator + stepName + "_"
				+ checkPointIndex + ".gif";
		try {
			FileUtils.copyFile(screenshotFile, new File(outputPath));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

}
