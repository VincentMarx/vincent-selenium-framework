package com.vincent.core.testcase;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class TestCase implements Test {
	private String testCaseName;
	private boolean isChecked = true;
	private int runs;
	private Status status;
	private List<TestStep> testSteps;

	public TestCase(String testCaseName) {
		this.testCaseName = testCaseName;
		this.testSteps = new ArrayList<TestStep>();
	}

	public String getTestCaseName() {
		return testCaseName;
	}

	public List<TestStep> getTestSteps() {
		return testSteps;
	}

	public boolean isChecked() {
		return isChecked;
	}

	@Override
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public int getRuns() {
		return runs;
	}

	public void setRuns(int runs) {
		this.runs = runs;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TestCase) {
			TestCase testCase = (TestCase) obj;
			if (this.testCaseName.equalsIgnoreCase(testCase.testCaseName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("testCaseName   : " + testCaseName + "\n");
		sb.append("isChecked      : " + isChecked + "\n");
		sb.append("runs           : " + runs + "\n");
		sb.append("status         : " + status + "\n");

		int stepCounter = 1;
		for (TestStep testStep : testSteps) {
			sb.append("step " + StringUtils.rightPad(String.valueOf(stepCounter), 2) + "        : "
					+ testStep.toString() + "\n");
			stepCounter++;
		}
		return sb.toString();
	}

	@Override
	public Object getValueAt(int col) {
		switch (col) {
		case 0:
			return isChecked;
		case 1:
			return testCaseName;
		case 2:
			return runs;
		case 3:
			return status;
		}
		return null;
	}

}
