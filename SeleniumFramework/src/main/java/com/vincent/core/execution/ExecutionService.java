package com.vincent.core.execution;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vincent.core.page.PageLoader;
import com.vincent.core.page.PageObject;
import com.vincent.core.report.Report;
import com.vincent.core.testcase.TestCase;
import com.vincent.core.testcase.TestStep;

public class ExecutionService {
	private static final Log log = LogFactory.getLog(ExecutionService.class);

	private static AtomicBoolean isStop = new AtomicBoolean(false);
	private static int total = 0;
	private static int curTest;

	public static void executeTestSet(List<TestCase> testSet) {
		total = 0;
		for (TestCase testCase : testSet) {
			if (testCase.isChecked()) {
				total++;
			}
		}
		for (TestCase testCase : testSet) {
			if (!isStop.get()) {
				try {
					executeTestCase(testCase);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	private static void executeTestCase(TestCase testCase) throws Exception {
		Report report = new Report();
		report.init(testCase.getTestCaseName());
		for (TestStep testStep : testCase.getTestSteps()) {
			if (!isStop.get()) {
				executeTestStep(testStep, report);
			}
		}

	}

	private static void executeTestStep(TestStep testStep, Report report) throws Exception {
		String stepName = testStep.getStepName();
		String[] stepNameArr = stepName.split("\\.");
		String pageName = stepNameArr[0];
		String methodName = stepNameArr[1];
		PageObject page = PageLoader.getPageObject(pageName);
		page.setReport(report);
		page.setData(testStep.getParams());
		Method method = page.getClass().getMethod(methodName);
		method.invoke(page);

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
