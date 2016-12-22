package com.vincent.core.execution;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vincent.core.data.ExcelData;
import com.vincent.core.page.PageLoader;
import com.vincent.core.page.PageObject;
import com.vincent.core.report.Report;
import com.vincent.core.testcase.Status;
import com.vincent.core.testcase.TestCase;
import com.vincent.core.testcase.TestStep;

public class ExecutionService {
	private static final Log log = LogFactory.getLog(ExecutionService.class);

	private AtomicBoolean isStop = new AtomicBoolean(false);
	private String dataFilePath = "";
	private String outputPath = "";
	private int total = 0;
	private int curTest;

	public ExecutionService(String dataFilePath) {
		this.dataFilePath = dataFilePath;
	}

	public void executeTestSet(List<TestCase> testSet) {
		total = 0;
		for (TestCase testCase : testSet) {
			if (testCase.isChecked()) {
				total++;
			}
		}
		init();
		log.info("Executing data file : " + dataFilePath);
		log.info("Number of test case to be executed: " + total);
		for (TestCase testCase : testSet) {
			if (testCase.isChecked()) {
				log.info("Executing " + curTest + " test case : " + testCase.getTestCaseName());
				if (!isStop.get()) {
					Context context = new Context();
					Report report = new Report(this.outputPath, testCase.getTestCaseName());
					context.setReport(report);
					try {
						executeTestCase(testCase, context);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
					testCase.setStatus(report.getTestCaseStatus());
				}
			}
		}
	}

	private void executeTestCase(TestCase testCase, Context context) throws Exception {
		Report report = context.getReport();
		for (TestStep testStep : testCase.getTestSteps()) {
			if (testStep.isChecked()) {
				report.addStep(testStep.getStepName());
				if (!isStop.get()) {
					try {
						executeTestStep(testStep, context);
					} catch (Exception e) {
						// if catch exception, report fatal status
						report.write(Status.Fatal, e.getMessage());
						throw e;
					}
					testStep.setStatus(report.getCurrentStepStatus());
				} else {
					report.write(Status.Stop, "Stop by user before executing this step.");
					testStep.setStatus(report.getCurrentStepStatus());
				}
			}
		}

	}

	private void executeTestStep(TestStep testStep, Context context) throws Exception {
		String stepName = testStep.getStepName();
		log.info("Step : " + stepName);
		String[] stepNameArr = stepName.split("\\.");
		String pageName = stepNameArr[0];
		String methodName = stepNameArr[1];
		PageObject page = PageLoader.getPageObject(pageName);
		context.setStepData(testStep.getParams());
		page.setContext(context);
		Method method = page.getClass().getMethod(methodName);
		method.invoke(page);
	}

	private void init() {
		// TODO copy js, css, report template to outputPath
		File output = new File(this.outputPath);
		if (!output.exists()) {
			output.mkdirs();
		}
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public String getDataFilePath() {
		return dataFilePath;
	}

	public int getTotal() {
		return total;
	}

	public int getCurTest() {
		return curTest;
	}

	public void setIsStop(AtomicBoolean isStop) {
		this.isStop = isStop;
	}

	public static void main(String[] args) {
		String dataFilePath = "dataFiles/MavenCentralRepository.xls";
		ExcelData excelData = new ExcelData(dataFilePath);
		excelData.loadTestCase();

		ExecutionService executionService = new ExecutionService(dataFilePath);

		executionService.executeTestSet(excelData.getTestSet());

	}

}
