package com.vincent.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

public class ExcelUtil {
	private static final Log log = LogFactory.getLog(ExcelUtil.class);
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public static String getCellValue(Cell cell, FormulaEvaluator evaluator) {
		String value = "";
		if (cell == null) {
			return value;
		}
		switch (cell.getCellType()) {

		case Cell.CELL_TYPE_NUMERIC:
			try {
				double doubleValue = cell.getNumericCellValue();
				value = String.valueOf(doubleValue);
				if (value.endsWith(".0")) {
					Long a = new Double(doubleValue).longValue();
					value = String.valueOf(a);
				}
			} catch (Exception e) {
				if (cell.getDateCellValue() != null) {
					value = formatDate(cell.getDateCellValue());
				}
			}
			break;
		case Cell.CELL_TYPE_STRING:
			value = cell.getRichStringCellValue().toString();
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			value = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_FORMULA:
			CellValue cellValue = evaluator.evaluate(cell);
			value = getCellValue(cellValue);
		default:
			// do nothing.
		}
		return value;

	}

	private static String getCellValue(CellValue cellValue) {
		String value = "";
		if (cellValue == null) {
			return value;
		}
		switch (cellValue.getCellType()) {

		case Cell.CELL_TYPE_NUMERIC:
			try {
				double doubleValue = cellValue.getNumberValue();
				value = String.valueOf(doubleValue);
				if (value.endsWith(".0")) {
					Long a = new Double(doubleValue).longValue();
					value = String.valueOf(a);
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			break;
		case Cell.CELL_TYPE_STRING:
			value = cellValue.getStringValue();
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			value = String.valueOf(cellValue.getBooleanValue());
			break;
		default:
			// do nothing.
		}
		return value;
	}

	public static String formatDate(Date date) {
		return dateFormat.format(date);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
