package com.vincent.core.testcase;

public enum Status {
	Awaiting, Running, Done, Pass, Warning, Fail, Fatal, Stop;

	public static void main(String[] args) {
		Status status = Status.valueOf("Warning");
		System.out.println(status.toString());
	}
}
