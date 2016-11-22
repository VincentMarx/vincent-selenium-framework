package com.vincent.core.report;

import com.vincent.core.testcase.Status;

public class RCheckPoint {
	private Status status;
	private String screenShot;
	private String remark;

	public RCheckPoint(Status status, String screenShot, String remark) {
		this.status = status;
		this.screenShot = screenShot;
		this.remark = remark;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getScreenShot() {
		return screenShot;
	}

	public void setScreenShot(String screenShot) {
		this.screenShot = screenShot;
	}

}
