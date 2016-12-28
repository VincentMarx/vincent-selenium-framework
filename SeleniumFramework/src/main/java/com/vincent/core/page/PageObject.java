package com.vincent.core.page;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.safari.SafariDriver;

import com.vincent.core.config.Config;
import com.vincent.core.execution.Context;
import com.vincent.core.testcase.Status;

public abstract class PageObject {
	protected static final Log log = LogFactory.getLog(PageObject.class);
	private static final String BLANK = "<BLANK>";
	private Context context;

    /**
     * <p>Set value to a text field, i.e. input, textarea. </p>
     *
     * <p>If the text field was not found, will throw exception</p>
     *
     * <pre>
     * setValue(field, ""), nothing will be done
     * setValue(field, null), nothing will be done
     * setValue(field, "<BLANK>"), will clear the field
     * setValue(field, "abc"), "abc" will be set to field
     * </pre>
     *
     * @param fieldName  method name of a subclass of this class which represents a field (return By object).
     * @param value  String to be set to the field.
     */
	protected void setValue(String fieldName, String value) throws Exception{
			By by = getBy(fieldName);
			setValue(by, value);
	}

    /**
     * <p>Set value to a text field, i.e. input, textarea. </p>
     *
     * <p>If the text field was not found, will throw exception</p>
     *
     * <pre>
     * setValue(field, ""), nothing will be done
     * setValue(field, null), nothing will be done
     * setValue(field, "<BLANK>"), will clear the field
     * setValue(field, "abc"), "abc" will be set to field
     * </pre>
     *
     * @param byField  Instance of By which represents a field
     * @param value  String to be set to the field.
     */
	protected void setValue(By byField, String value) {
		setValue(null, byField, value);
	}

    /**
     * <p>Set value to a text field, i.e. input, textarea. </p>
     *
     * <p>If the text field was not found, will throw exception</p>
     *
     * <pre>
     * setValue(parent, field, ""), nothing will be done
     * setValue(parent, field, null), nothing will be done
     * setValue(parent, field, "<BLANK>"), will clear the field
     * setValue(parent, field, "abc"), "abc" will be set to field
     * </pre>
     *
     ** @param parent  Parent element to find the field, if null, will find the field at root element.
     * @param byField  Instance of By which represents a field
     * @param value  String to be set to the field.
     */
	protected void setValue(WebElement parent, By byField, String value) {
        log.info("setValue | " + toString(parent, byField));
        if (value==null || "".equals(value)){
            return;
        }
		WebElement field;
		if (parent != null) {
			field = parent.findElement(byField);
		} else {
			field = context.getDriver().findElement(byField);
		}
		setValue(field, value);
	}

    /**
     * <p>Set value to a text field, i.e. input, textarea. </p>
     *
     * <p>If the text field was not found, will throw exception</p>
     *
     * <pre>
     * setValue(field, ""), nothing will be done
     * setValue(field, null), nothing will be done
     * setValue(field, "<BLANK>"), will clear the field
     * setValue(field, "abc"), "abc" will be set to field
     * </pre>
     *
     * @param field  Instance of WebElement which represents a field
     * @param value  String to be set to the field.
     */
	protected void setValue(WebElement field, String value) {
        if (value==null || "".equals(value)){
            return;
        }
		field.clear();
		if (!BLANK.equalsIgnoreCase(value)){
            field.sendKeys(value);
        }
	}

    /**
     * <p>Init WebDriver according to the values in config file </p>
     *
     * <pre>
     * Config.browserType=Firefox, will init FirefoxDriver, if Config.firefoxProfile is specified, will init FirefoxDriver with that profile
     * Config.browserType=IE, will init InternetExplorerDriver
     * Config.browserType=Chrome, will init ChromeDriver
     * Config.browserType=Safari, will init SafariDriver
     * </pre>
     *
     * @param isInit  no use in this version
     * @return new WebDriver instance
     */
	protected WebDriver newDriver(final boolean isInit) {
		WebDriver driver = null;
		switch (Config.browserType) {
		case Firefox:
			// open firefox browser
			if (StringUtils.isNotBlank(Config.firefoxProfile)) {
				log.info("Open firefox with profile: " + Config.firefoxProfile);
				ProfilesIni profiles = new ProfilesIni();
				FirefoxProfile profile = profiles.getProfile(Config.firefoxProfile);
				if (profile == null) {
					log.error("firefox profile does not exist. profile name : = " + Config.firefoxProfile);
				}
				FirefoxBinary binary = new FirefoxBinary(new File(Config.firefoxPath));
				driver = new FirefoxDriver(binary, profile);
			} else {
				log.info("Open firefox without profile");
				driver = new FirefoxDriver();
			}

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

    /**
     * <p>Get test data</p>
     *
     * <p>The test data in data file of each step will be loaded to map</p>
     *
     * @param key  key of the data value
     * @return data value of the key
     */
	protected String getData(String key) {
		return context.getData(key);
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

    /**
     * <p>Report status of a check point</p>
     *
     * <p>this method should be used after verifying something, i.e.</p>
     *
     * <pre>
     * public void verifyAccountName(){
     *     String actualName = nameField.getText();
     *     String expectedName = getData("ExpectedName");
     *     if (actualName.equalsIgnoreCase(expectedName)){
     *         report(Status.Pass, "check account name passed");
     *     }
     * }
     * </pre>
     *
     * @param status  status of the check point
     * @param remark  remark of the check point
     */
	protected void report(Status status, String remark) {
		takeScreenshot();
		context.getReport().write(status, remark);
	}

    /**
     * <p>Report status of a check point</p>
     *
     * <p>See {@link #report(Status status, String remark)} </p>
     *
     * @param flag  if true status = Pass, else status = Fail
     * @param remark  remark of the check point
     */
	protected void report(boolean flag, String remark) {
		takeScreenshot();
		if (flag) {
			context.getReport().write(Status.Pass, remark);
		} else {
			context.getReport().write(Status.Fail, remark);
		}
	}

    /**
     * <p>Click an element, i.e. link, button, etc</p>
     *
     * @param byObj  instance of By represents the object to be clicked
     */
	protected void clickObject(By byObj) {
		log.info("clickObject | " + byObj.toString());
		context.getDriver().findElement(byObj).click();
	}

    /**
     * <p>Get rows on a web table</p>
     *
     * @param byTable  instance of By represents the web table
     * @return List of row elements
     * @throws org.openqa.selenium.NoSuchElementException
     */
	protected List<WebElement> getTableRows(By byTable) {
		log.info("getTableRows | " + byTable.toString());
		WebElement table = findElement(byTable);
		return table.findElements(By.xpath("//tbody/tr"));
	}

	protected WebElement findElement(By by) {
		return findElement(null, by);
	}

    /**
     * <p>Find element represented by param 'by' in parent element</p>
     *
     * @param parent  parent element of the element being found.  If null, find the element in root element.
     * @param by the element being found
     * @return a WebElement
     * @throws org.openqa.selenium.NoSuchElementException
     */
	protected WebElement findElement(WebElement parent, By by) {
        log.info("findElement | " + toString(parent, by));
		if (parent == null) {
			return context.getDriver().findElement(by);
		} else {
			return parent.findElement(by);
		}
	}

    /**
     * <p>Find element represented by param 'by' in root element</p>
     *
     * @param by the element being found
     * @return a WebElement
     * @throws org.openqa.selenium.NoSuchElementException
     */
	protected boolean waitElement(By by) {
		return waitElement(null, by);
	}

	protected boolean waitElement(WebElement parent, By by) {
	    log.info("waitElement | " + toString(parent, by));
		try {
			if (parent == null) {
				context.getDriver().findElement(by);
			} else {
				parent.findElement(by);
			}
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
		String fileName = stepName + "_" + checkPointIndex + ".gif";
		log.info("takeScreenshot | " + fileName);
		String outputPath = context.getReport().getTestCaseOutputPath() + File.separator + fileName;
		try {
			FileUtils.copyFile(screenshotFile, new File(outputPath));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	private String toString(WebElement parent, By byField){
	    if (parent==null){
	        return byField.toString();
        } else {
	        return parent.toString() + " -> " + byField.toString();
        }
    }

}
