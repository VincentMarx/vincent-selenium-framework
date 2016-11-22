package com.vincent.core.report;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.xstream.XStream;
import com.vincent.core.testcase.Status;

public class Report {
	private static final Log log = LogFactory.getLog(Report.class);

	private String testCaseOutputPath;
	private RTestCase testCase;
	private RTestStep currentStep;

	public Report(String outputPath, String testCaseName) {
		this.testCaseOutputPath = outputPath + File.separator + testCaseName;
		File output = new File(this.testCaseOutputPath);
		if (!output.exists()) {
			output.mkdirs();
		}
		testCase = new RTestCase(testCaseName);
		// copy js, css, template, xslt
	}

	public void addStep(String stepName) {
		this.currentStep = new RTestStep(stepName);
		testCase.addSteps(currentStep);
		// create step node in xml

	}

	public void write(Status status, String remark) {
		// take screen shot
		String screenShot = ""; // TODO
		log.info("Step : " + currentStep.getStepName() + ", " + status.name() + ", " + remark);
		RCheckPoint checkPoint = new RCheckPoint(status, screenShot, remark);
		this.currentStep.addCheckPoint(checkPoint);
	}

	public void save() throws IOException {
		XStream xStream = new XStream();
		String xml = xStream.toXML(testCase);
		String xmlPath = new File(testCaseOutputPath).getAbsolutePath() + File.separator + "result.xml";
		FileUtils.writeStringToFile(new File(xmlPath), xml);
	}

	public Status getCurrentStepStatus() {
		return currentStep.getStatus();
	}

	public String getTestCaseOutputPath() {
		return testCaseOutputPath;
	}

	public void setTestCaseOutputPath(String testCaseOutputPath) {
		this.testCaseOutputPath = testCaseOutputPath;
	}

	public Status getTestCaseStatus() {
		return testCase.getStatus();
	}

	public void setTestCase(RTestCase testCase) {
		this.testCase = testCase;
	}

}
