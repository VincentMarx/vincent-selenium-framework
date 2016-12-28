package com.vincent.core.execution;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;

import com.vincent.core.config.Config;
import com.vincent.core.data.ExcelData;
import com.vincent.core.page.PageLoader;
import com.vincent.core.page.PageObject;
import com.vincent.core.report.Report;
import com.vincent.core.testcase.Status;
import com.vincent.core.testcase.TestCase;
import com.vincent.core.testcase.TestStep;

/**
 *
 */
public class ExecutionService {
	private static final Log log = LogFactory.getLog(ExecutionService.class);

	private boolean stop = false;
	private boolean pause = false;
	private String dataFilePath = "";
	private String outputPath = "";
	private ExcelData data;
	private int curTest;
	private int total = 0;
	private int executed = 0;
	private int curTestCaseIdx = -1;
	private int curStepIdx = -1;

	public void init(String dataFilePath, String outputPath) {
		this.dataFilePath = dataFilePath;
		this.outputPath = outputPath;
		log.info("init Execution Service, data file: " + dataFilePath + ", output path: " + outputPath);
		data = new ExcelData();
		data.loadTestCase(dataFilePath);
		// TODO copy js, css, report template to outputPath
		File output = new File(this.outputPath);
		if (!output.exists()) {
			if (!output.mkdirs()){
			    log.error("Fail to create folder: " + this.outputPath);
            }
		}
	}

	public void executeTestSet() {
		log.info("Executing data file : " + dataFilePath);
		log.info("Number of test case to be executed: " + getTotal());
		executed = 0;
		curTestCaseIdx = 0;
		for (TestCase testCase : data.getTestSet()) {
			while (pause) {
				Thread.currentThread();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
				}
			}
			if (testCase.isChecked()) {
				log.info("Executing " + curTest++ + " test case : " + testCase.getTestCaseName());
				if (!stop) {
					Context context = new Context();

					try {
						executeTestCase(testCase, context);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
					executed++;
					// close browser
					if (Config.isCloseBrowserAtEnd) {
						WebDriver driver = context.getDriver();
						if (driver != null) {
							driver.quit();
						}
					}
				}
			}
			curTestCaseIdx++;
		}
		curTestCaseIdx = -1;
	}

	public void executeTestCase(TestCase testCase, Context context) throws Exception {
		testCase.setStatus(Status.Running);
		Report report = new Report(this.outputPath, testCase.getTestCaseName());
		context.setReport(report);
		for (TestStep testStep : testCase.getTestSteps()) {
			if (testStep.isChecked()) {
				testStep.setStatus(Status.Awaiting);
			}
		}
		curStepIdx = 0;
		try {
			for (TestStep testStep : testCase.getTestSteps()) {
				while (pause) {
					Thread.currentThread();
					Thread.sleep(500);
				}
				if (testStep.isChecked()) {
					report.addStep(testStep.getStepName());
					testStep.setStatus(Status.Running);
					if (!stop) {
						try {
							executeTestStep(testStep, context);
						} catch (Exception e) {
							// if catch exception, report fatal status
							report.write(Status.Fatal, e.getMessage());
							throw e;
						} finally {
							testStep.setStatus(report.getCurrentStepStatus());
						}
					} else {
						report.write(Status.Stop, "Stop by user before executing this step.");
						testStep.setStatus(report.getCurrentStepStatus());
					}
				}
				curStepIdx++;
			}
		} finally {
			curStepIdx = -1;
			testCase.setStatus(report.getTestCaseStatus());
			// save report
			report.setEndTime();
			try {
				report.save();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
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

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public String getDataFilePath() {
		return dataFilePath;
	}

	public synchronized int getTotal() {
		if (total == 0) {
			for (TestCase testCase : data.getTestSet()) {
				if (testCase.isChecked()) {
					testCase.setStatus(Status.Awaiting);
					total++;
				}
			}
		}
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getCurTest() {
		return curTest;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}

	public List<TestCase> getTestSet() {
		if (data == null) {
			return null;
		}
		return data.getTestSet();
	}

	public int getExecuted() {
		return executed;
	}

	public int getCurTestCaseIdx() {
		return curTestCaseIdx;
	}

	public int getCurStepIdx() {
		return curStepIdx;
	}

	public void refresh() {
		if (data == null) {
			return;
		}
		this.data.loadTestCase(dataFilePath);
	}

	public static void main(String[] args) {
		String dataFilePath = "dataFiles/MavenCentralRepository.xls";
		ExecutionService executionService = new ExecutionService();
		executionService.init(dataFilePath, "output");
		executionService.executeTestSet();

	}

}
