package com.vincent.core.page;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PageLoader {
	protected static final Log log = LogFactory.getLog(PageLoader.class);
	private static Map<String, Class<PageObject>> pageMap;
	private static ClassLoader classLoader;

	public static PageObject getPageObject(String pageName) {
		Class<PageObject> pageClass = pageMap.get(pageName);
		if (pageClass == null) {
			log.error("Page object : " + pageName + " was not found.");
		}
		PageObject pageObject = null;
		try {
			pageObject = pageClass.newInstance();
		} catch (Exception e) {
			log.error("error occurred when init page object : " + pageName);
			log.error(e.getMessage(), e);
		}
		return pageObject;
	}
}
