package com.vincent.core.page;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;

import com.vincent.core.execution.Context;

public abstract class PageObject {
	protected static final Log log = LogFactory.getLog(PageObject.class);
	protected Context context;
	/*
	 * Open a new browser
	 */

	protected WebDriver newDriver(final boolean isInit) {
		WebDriver driver = null;
		return driver;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	// protected String getValue(String fieldName) {
	// return data.get(fieldName);
	// }

}
