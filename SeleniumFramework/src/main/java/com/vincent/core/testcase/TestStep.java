package com.vincent.core.testcase;

import java.util.LinkedHashMap;
import java.util.Map;

public class TestStep implements Test {
	private String stepName;
	private boolean isChecked = true;
	private Status status;
	private Map<String, String> params;

	public TestStep(String stepName) {
		this.stepName = stepName;
		this.params = new LinkedHashMap<String, String>();
	}

	public String getStepName() {
		return stepName;
	}

	public Map<String, String> getParams() {
		return params;
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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TestStep) {
			TestStep testStep = (TestStep) obj;
			if (this.stepName.equalsIgnoreCase(testStep.stepName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("stepName    = " + stepName + "\n");
		sb.append("                 isChecked   = " + isChecked + "\n");
		sb.append("                 status      = " + status + "\n");
		sb.append("                 params      = " + params.toString() + "\n");
		return sb.toString();
	}

	@Override
	public Object getValueAt(int col) {
		switch (col) {
		case 0:
			return isChecked;
		case 1:
			return stepName;
		case 2:
			return status;
		}
		return null;
	}

}
