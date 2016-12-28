package com.vincent.core.page;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vincent.core.config.Config;

public class PageLoader {
	protected static final Log log = LogFactory.getLog(PageLoader.class);
	private static Map<String, Class<?>> pageMap;
	private static Map<String, Long> pageClassLastModifiedMap;
	private static String classFolder = "target/test-classes/" + Config.pagePackage.replace(".", File.separator);

	static {
		loadPages();
	}

	private static void loadPages() {
		PageClassLoader classLoader = new PageClassLoader();
		pageMap = new HashMap<>();
		pageClassLastModifiedMap = new HashMap<>();
		File[] classFiles = new File(classFolder).listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return  file.getName().toLowerCase().endsWith(".class");
            }
        });
		if (classFiles==null){
		    log.warn("no .class file could be found in " + classFolder);
		    return;
        }
		for (File classFile : classFiles) {
			String fileName = StringUtils.substringBefore(classFile.getName(), ".");
			long lastModified = classFile.lastModified();
			pageClassLastModifiedMap.put(fileName, lastModified);
			try {
				Class<?> pageClass = classLoader.defineClass(classFile);
				pageMap.put(fileName, pageClass);
				// log.debug("found page: " + fileName + ", class name = " +
				// pageClass.getName());
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}

		}
	}

	public static PageObject getPageObject(String pageName) throws PageNotFoundException, IOException {
		checkPageObjectUpdate();
		Class<?> pageClass = pageMap.get(pageName);
		if (pageClass == null) {
			log.error("Page object : " + pageName + " was not found.");
			return null;
		}
		PageObject pageObject = null;
		try {
			pageObject = (PageObject) pageClass.newInstance();
		} catch (Exception e) {
			log.error("error occurred when init page object : " + pageName);
			log.error(e.getMessage(), e);
		}
		return pageObject;
	}

	private static void checkPageObjectUpdate() {
		boolean flag = false;
		File classFolderFile = new File(classFolder);
		if (!classFolderFile.exists()){
			log.error("page class folder does not exist." + classFolder);
			return;
		}
		File[] classFiles = classFolderFile.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
					return file.getName().toLowerCase().endsWith(".class");
			}
		});
        if (classFiles == null){
            log.error("no page class was found.");
            return;
        }
		for (File classFile : classFiles) {
			String fileName = StringUtils.substringBefore(classFile.getName(), ".");
			Long lastModified = pageClassLastModifiedMap.get(fileName);
			if (lastModified == null) {
				lastModified = 0L;
			}
			if (classFile.lastModified() != lastModified) {
				log.debug("page object " + fileName + " was updated.");
				flag = true;
			}
		}
		if (flag) {
			loadPages();
		}
	}

	public static void main(String[] args)
			throws PageNotFoundException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, InterruptedException {
		for (int i = 0; i < 30; i++) {
			PageObject pageObj = PageLoader.getPageObject("TestPage");
			Class<? extends PageObject> clazz = pageObj.getClass();
			Method m = clazz.getDeclaredMethod("pln");
			m.invoke(pageObj);
			Thread.sleep(1000);
		}

	}

}
