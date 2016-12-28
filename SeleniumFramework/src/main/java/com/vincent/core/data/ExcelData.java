package com.vincent.core.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.vincent.core.testcase.TestCase;
import com.vincent.core.testcase.TestStep;
import com.vincent.core.util.ExcelUtil;

public class ExcelData {
	private static Log log = LogFactory.getLog(ExcelData.class);

	private static final String SHEET_NAME = "TestCase";
	// key find start row, start column
	private static final String HEADER_TEST_CASE = "TestCaseName";
	// 0 - not run, 1 - run one time, 2 - run 2 times
	private static final String HEADER_RUNS = "Runs";
	private static final String HEADER_STEPS = "Steps";
	private static final String STEP_HEADER = "Step";


	private long lastModified = 0;

	private List<TestCase> testSet = new ArrayList<>();

	public void loadTestCase(String dataFilePath) {
		log.debug("dataFilePath : " + dataFilePath);
		File dataFile = new File(dataFilePath);
		if (!dataFile.exists()) {
			log.error("data file: " + dataFilePath + " does not exist.");
		}
		// check if data file was updated.
		if (lastModified != dataFile.lastModified()) {
			testSet.clear();
			List<TestCase> testCaseList = loadTestCase(dataFile);
			log.debug("Total number of TestCase : " + testCaseList.size());

			// set current status to new testCaseList
			for (TestCase testCase : testCaseList) {
				int index = testSet.indexOf(testCase);
				if (index >= 0) {
					testCase.setChecked(testSet.get(index).isChecked());
					testCase.setStatus(testSet.get(index).getStatus());
				}
			}
			testSet = testCaseList;
			lastModified = dataFile.lastModified();
		}
	}

	private List<TestCase> loadTestCase(File dataFile) {
		List<TestCase> testCasesList = new ArrayList<>();
		InputStream is = null;
		FormulaEvaluator evaluator;
		try {
			is = new FileInputStream(dataFile);
            Workbook wb = WorkbookFactory.create(is);
			evaluator = wb.getCreationHelper().createFormulaEvaluator();
			Sheet sh = wb.getSheet(SHEET_NAME);
			if (sh == null) {
				log.debug("Sheet : " + SHEET_NAME + " was not found, load test case from the first sheet instead.");
				sh = wb.getSheetAt(0);
			} else {
				log.debug("Load test case from sheet : " + SHEET_NAME);
			}
			// get start row, start column
			int lastRowNum = sh.getLastRowNum();
			int startRow = 0, testCaseCol = 0, runsCol = 0, stepsCol = 0;
			boolean isTestCaseHeaderFound = false;
			// loop rows
			for (int rowNum = sh.getFirstRowNum(); rowNum <= lastRowNum; rowNum++) {
				Row row = sh.getRow(rowNum);
				if (row == null) {
					continue;
				}
				// loop cells of each row
				for (int cellNum = row.getFirstCellNum(); cellNum <= row.getLastCellNum(); cellNum++) {
					Cell cell = row.getCell(cellNum);
					if (cell != null) {
						String cellValue = ExcelUtil.getCellValue(cell, evaluator);
						// check if cellValue == HEADER_TEST_CASE
						if (HEADER_TEST_CASE.equalsIgnoreCase(cellValue)) {
							startRow = rowNum;
							testCaseCol = cellNum;
							log.debug("Start row : " + startRow);
							log.debug("Test Case column : " + testCaseCol);
							isTestCaseHeaderFound = true;
						}
						if (isTestCaseHeaderFound) {
							// get Runs col
							if (HEADER_RUNS.equalsIgnoreCase(cellValue)) {
								runsCol = cellNum;
								log.debug("Runs column : " + runsCol);
								// get Steps col
							} else if (HEADER_STEPS.equalsIgnoreCase(cellValue)) {
								stepsCol = cellNum;
								log.debug("Steps column : " + stepsCol);
							}
						}
					}
				}
				if (isTestCaseHeaderFound) {
					break;
				}
			}
			// check if HEADER_TEST_CASE was found
			if (isTestCaseHeaderFound) {
				for (int rowNum = startRow + 1; rowNum <= lastRowNum; rowNum++) {
					Row row = sh.getRow(rowNum);
					if (row == null) {
						continue;
					}

					// check if Runs > 0
					Cell runsCell = row.getCell(runsCol);
					if (runsCell == null) {
						continue;
					}

					String runsStr = ExcelUtil.getCellValue(runsCell, evaluator);
					if (StringUtils.isBlank(runsStr)) {
						continue;
					}

					int runs = 0;
					try {
						runs = Integer.valueOf(runsStr);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}

					if (runs < 1) {
						continue;
					}

					// check if Runs > 0

					// check if test case name blank
					Cell testCaseCell = row.getCell(testCaseCol);
					if (testCaseCell == null) {
						continue;
					}
					String testCaseName = ExcelUtil.getCellValue(testCaseCell, evaluator);
					if (StringUtils.isBlank(testCaseName)) {
						continue;
					}
					// check if test case name blank

					TestCase testCase = new TestCase(testCaseName);
					// check if test case exist in test case list
					if (testCasesList.contains(testCase)) {
						testCase = testCasesList.get(testCasesList.indexOf(testCase));
					} else {
						log.debug("Load test case : " + testCaseName);
						testCasesList.add(testCase);
					}

					// check if test step blank
					Cell testStepCell = row.getCell(stepsCol);
					if (testStepCell == null) {
						continue;
					}
					String testStepName = ExcelUtil.getCellValue(testStepCell, evaluator);
					if (StringUtils.isBlank(testStepName)) {
						continue;
					}
					// check if test step blank

					// testStepName = Step, this row is Step header;
					if (StringUtils.equalsIgnoreCase(STEP_HEADER, testStepName)) {

						continue;
					}

					TestStep testStep = new TestStep(testStepName);

					int stepHeaderRowNum = startRow; // init stepHeaderRow =
														// start
														// row
					// check if above row is step header row
					Row stepHeaderRow = sh.getRow(rowNum - 1);
					if (stepHeaderRow != null) {
						Cell stepHeaderCell = stepHeaderRow.getCell(stepsCol);
						if (stepHeaderCell != null) {
							String stepHeader = ExcelUtil.getCellValue(stepHeaderCell, evaluator);
							if (StringUtils.equalsIgnoreCase(STEP_HEADER, stepHeader)) {
								stepHeaderRowNum = rowNum - 1;
							}
						}
					}
					stepHeaderRow = sh.getRow(stepHeaderRowNum);
					for (int stepParamCol = stepsCol + 1; stepParamCol <= stepHeaderRow
							.getLastCellNum(); stepParamCol++) {
						Cell paramHeaderCell = stepHeaderRow.getCell(stepParamCol);
						// check if header cell blank
						if (paramHeaderCell == null) {
							continue;
						}
						String paramHeader = ExcelUtil.getCellValue(paramHeaderCell, evaluator);
						if (StringUtils.isBlank(paramHeader)) {
							continue;
						}
						// check if header cell blank
						String paramValue = ExcelUtil.getCellValue(row.getCell(stepParamCol), evaluator);
						testStep.getParams().put(paramHeader, paramValue);
					}
					// check if above row is step header row
					testCase.getTestSteps().add(testStep);
					testCase.setRuns(runs);
				}
			}
		} catch (Exception e) {
			log.error("Error occurred when opening data file.");
			log.error(e.getMessage(), e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error(e.getMessage(),e);
				}
			}
		}

		return testCasesList;
	}

	public List<TestCase> getTestSet() {
		return testSet;
	}

	public static void main(String[] args) {
		String dataFilePath = "dataFiles/MavenCentralRepository.xls";
		ExcelData excelData = new ExcelData();
		excelData.loadTestCase(dataFilePath);
		for (TestCase testCase : excelData.getTestSet()) {
			System.out.println(testCase);
		}
	}
}
