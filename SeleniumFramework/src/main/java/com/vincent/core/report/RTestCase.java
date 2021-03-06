package com.vincent.core.report;

import java.util.ArrayList;
import java.util.List;

import com.vincent.core.testcase.Status;
import com.vincent.core.util.DateUtil;

public class RTestCase {
	private String testCaseName;
	private String startTime;
	private String endTime;
	private Status status;
	private List<RTestStep> testSteps;

	public RTestCase(String testCaseName) {
		this.testCaseName = testCaseName;
		this.startTime = DateUtil.getCurrentDateTime();
		testSteps = new ArrayList<>();
	}

	public String getTestCaseName() {
		return testCaseName;
	}

	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public List<RTestStep> getTestSteps() {
		return testSteps;
	}

	public void addStep(RTestStep step) {
		testSteps.add(step);
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Status getStatus() {
		// if the test case status haven't been set yet, set it to minimum value of Status.
		// test case status will be updated according to status of test step status.
		if (status == null){
			status = Status.Awaiting;
		}
		// a test case should have at least 1 test step
		for (RTestStep testStep : testSteps) {
			if (testStep.getStatus().compareTo(status) > 0) {
				status = testStep.getStatus();
			}
		}
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
