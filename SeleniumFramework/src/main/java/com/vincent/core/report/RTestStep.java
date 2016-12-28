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
		this.checkPoints = new ArrayList<>();
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public Status getStatus() {
		// if the test step status haven't been set yet, set it to minimum value of Status.
		// test step status will be updated according to status of check point status.
		if (status == null) {
		    if (checkPoints.size()==0){
                status = Status.Done;
            } else {
                status = Status.Awaiting;
            }
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

	public int checkPointIndex() {
		return checkPoints.size() + 1;
	}

}
