package com.vincent.core.report;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.xstream.XStream;
import com.vincent.core.testcase.Status;
import com.vincent.core.util.DateUtil;

public class Report {
	private static final Log log = LogFactory.getLog(Report.class);

	private String testCaseOutputPath;
	private RTestCase testCase;
	private RTestStep currentStep;

	private static XStream xStream;

	static {
		xStream = new XStream();
		xStream.setMode(XStream.NO_REFERENCES);
	}

	public Report(String outputPath, String testCaseName) {
		this.testCaseOutputPath = outputPath + File.separator + testCaseName;
		File output = new File(this.testCaseOutputPath);
		if (!output.exists()) {
			if(!output.mkdirs()){
				log.error("fail to create output folder: " + testCaseOutputPath);
			}
		}
		testCase = new RTestCase(testCaseName);
		// copy js, css, template, xslt
	}

	public void addStep(String stepName) {
		this.currentStep = new RTestStep(stepName);
		testCase.addStep(currentStep);
		// create step node in xml

	}

	public void write(Status status, String remark) {
		// take screen shot
		String screenShot = getCurrentStepName() + "_" + checkPointIndex() + ".gif"; // TODO
		log.info("Step : " + currentStep.getStepName() + ", " + status.name() + ", " + remark);
		RCheckPoint checkPoint = new RCheckPoint(status, screenShot, remark);
		this.currentStep.addCheckPoint(checkPoint);
	}

	public void save() throws IOException {

		String xml = xStream.toXML(testCase);
		String xmlPath = new File(testCaseOutputPath).getAbsolutePath() + File.separator + "result.xml";
		FileUtils.writeStringToFile(new File(xmlPath), xml);
	}

	public Status getCurrentStepStatus() {
		return currentStep.getStatus();
	}

	public String getCurrentStepName() {
		return currentStep.getStepName();
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

	public int checkPointIndex() {
		return currentStep.checkPointIndex();
	}

	public void setTestCase(RTestCase testCase) {
		this.testCase = testCase;
	}

	public void setEndTime() {
		this.testCase.setEndTime(DateUtil.getCurrentDateTime());
	}

}
