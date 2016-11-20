package com.vincent.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	
	public static final String dateFormat = "yyyy-MM-dd HH:mm:ss";
	
	public static String getCurrentDateTime(){
		return new SimpleDateFormat(dateFormat).format(new Date());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
