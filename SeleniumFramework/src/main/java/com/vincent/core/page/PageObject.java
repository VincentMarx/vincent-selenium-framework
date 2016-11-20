package com.vincent.core.page;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;

import com.vincent.core.report.Report;

public abstract class PageObject {
	protected static final Log log = LogFactory.getLog(PageObject.class);
	protected Report report;
	protected Map<String, String> data;
	/*
	 * Open a new browser
	 */

	protected WebDriver newDriver(final boolean isInit) {
		WebDriver driver = null;
		return driver;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	// protected String getValue(String fieldName) {
	// return data.get(fieldName);
	// }

}
