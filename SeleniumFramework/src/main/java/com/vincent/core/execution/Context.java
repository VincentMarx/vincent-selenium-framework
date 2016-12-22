package com.vincent.core.execution;

import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.vincent.core.report.Report;

public class Context {
	private Report report;
	private Map<String, String> stepData;
	private Map<String, String> caseData;
	private WebDriver driver;

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public Map<String, String> getStepData() {
		return stepData;
	}

	public void setStepData(Map<String, String> stepData) {
		this.stepData = stepData;
	}

	public Map<String, String> getCaseData() {
		return caseData;
	}

	public void setCaseData(Map<String, String> caseData) {
		this.caseData = caseData;
	}

	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

}
