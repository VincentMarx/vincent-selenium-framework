package com.vincent.core.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.vincent.core.log.LoggingOutputStream;

public class Config {
	private static final Log log = LogFactory.getLog(Config.class);

	public static String configPath = "config.properties";

	public static String dataPath = "";

	public static String dataFileFolder = "dataFiles";

	public static String outputRootPath = "output";

	public static String driverPath = "driver/";

	public static BrowserType browserType = BrowserType.Firefox;

	public static String webDriver = "local";

	public static String remoteWebDriverUrl = "http://localhost:4444/wd/hub";

	public static String pagePackage = "com.test.page";

	// for fire fox
	public static String firefoxPath = "D:/Program Files/Mozilla Firefox 32/firefox.exe";

	public static String firefoxProfile = "selenium";

	// for IE
	public static String IEDriverPath = driverPath + "IEDriverServer.exe";

	// for Chrome
	public static String chromePath;

	public static String chromeOrg;

	public static String chromeDriverPath = driverPath + "chromedriver.exe";

	// for safari
	public static String safariDriverPath = driverPath + "safaridriver.exe";

	// driver implicitly wait timeout seconds
	public static long implicitlyWaitTimeoutSeconds = 30L;

	// is close browser at the end of each test case
	public static boolean isCloseBrowserAtEnd = true;

	// is overwrite report, if true, keep the latest report only; if false, will
	// create separate report for each run.
	public static boolean isOverwriteReport = true;

	// image file type of screen shot, i.e. png, jpg, gif
	public static String reportImageType = "gif";

	// if true, test case will stop running when fail, if true, will continue to
	// run
	public static boolean isStopOnReportFail = false;

	public static String pomGroupId;
	public static String pomArtifactId;

	// ################# junit #################
	// generate junit test case at this package
	public static String junitPackage = "com.vincent.runtime.junit";
	// filter test case with regular expression
	public static String junitTestCaseFilter = "";
	// if true, will delete execution result before start running
	public static boolean isDeleteResult = true;
	// ################# junit #################

	// load project config
	static {
		// redirect system.err to logger
		System.setErr(new PrintStream(new LoggingOutputStream(Config.log, 5), true));

		// Load config file
		loadConfigFile(Config.configPath);

		// read POM info
		File pomFile = new File("pom.xml");
		if (pomFile.exists()) {
			SAXReader reader = new SAXReader();
			try {

				Element root = reader.read(pomFile).getRootElement();
				pomGroupId = root.element("groupId").getText();
				pomArtifactId = root.element("artifactId").getText();
				log.info("pomGroupId = " + pomGroupId + ", pomArtifactId = " + pomArtifactId);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

	}

	private static void loadConfigFile(String configPath) {
		// get config file from Environment variable.
		String confPath = System.getenv("config");
		if (StringUtils.isBlank(confPath)) {
			// if not in Environment variable, try to get it from System
			// properties
			confPath = System.getProperty("config");
		}

		if (StringUtils.isBlank(confPath)) {
			confPath = configPath;
		}

		log.info("config = " + confPath);

		Properties prop = new Properties();
		FileInputStream fis;
		try {
			fis = new FileInputStream(confPath);
			prop.load(fis);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		Field[] fields = Config.class.getDeclaredFields();
		for (Field field : fields) {

			String fieldName = field.getName();
			// get field value from environment variable
			String fieldValue = System.getenv(fieldName);
			if (StringUtils.isBlank(fieldValue)) {
				// get field value from system properties
				fieldValue = System.getProperty(fieldName);
			}
			if (StringUtils.isBlank(fieldValue)) {
				fieldValue = prop.getProperty(fieldName);
			}

			if (StringUtils.isNotBlank(fieldValue)) {
				try {
					Class<?> fieldType = field.getType();
					// String field
					if (fieldType.isAssignableFrom(String.class)) {
						field.set(null, fieldValue);

						// boolean field
					} else if (fieldType.isAssignableFrom(Boolean.class)) {

						Boolean bValue = Boolean.valueOf(fieldValue);
						field.set(null, bValue);

						// int field
					} else if (fieldType.isAssignableFrom(Integer.class)) {

						Integer iValue = Integer.valueOf(fieldValue);
						field.set(null, iValue);

					} else if (fieldType.isAssignableFrom(BrowserType.class)) {

						field.set(null, BrowserType.valueOf(fieldValue));
					} else {
						log.debug("field : " + fieldName + " was not set.  field type = " + fieldType.getSimpleName()
								+ " field value = " + fieldValue);
					}

				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}

		}
	}
}
