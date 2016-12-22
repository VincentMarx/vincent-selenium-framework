package com.vincent.core.page;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.vincent.core.config.Config;

public class PageClassLoader extends ClassLoader {

	public Class<?> defineClass(File classFile) throws IOException {
		String fileName = StringUtils.substringBefore(classFile.getName(), ".");
		String className = Config.pagePackage + "." + fileName;
		byte[] b = FileUtils.readFileToByteArray(classFile);
		return defineClass(className, b, 0, b.length);
	}

}
