package com.vincent.core.report;

import java.util.ArrayList;
import java.util.List;

import com.vincent.core.testcase.Status;

public class RTestStep {
	private String stepName;
	private Status status;
	private List<RCheckPoint> checkPoints;

	public RTestStep(String stepName) {
		this.stepName = stepName;
		this.checkPoints = new ArrayList<RCheckPoint>();
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public Status getStatus() {
		if (status == null) {
			status = Status.Done;
		}
		for (RCheckPoint checkPoint : checkPoints) {
			if (checkPoint.getStatus().compareTo(status) > 0) {
				status = checkPoint.getStatus();
			}
		}
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void addCheckPoint(RCheckPoint checkPoint) {
		this.checkPoints.add(checkPoint);
	}

}
