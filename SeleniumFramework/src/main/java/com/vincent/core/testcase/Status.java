package com.vincent.core.testcase;

public enum Status {
	Done, Pass, Warning, Fail, Fatal, Stop;

	public static void main(String[] args) {
		Status status = Status.Warning;
		System.out.println(status.compareTo(Status.Fatal));
	}
}
