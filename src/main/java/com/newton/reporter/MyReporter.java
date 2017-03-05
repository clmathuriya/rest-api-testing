package com.newton.reporter;

import org.apache.commons.lang3.time.StopWatch;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.relevantcodes.extentreports.NetworkMode;

public class MyReporter extends ExtentReports {

	private static final long serialVersionUID = 1L;

	static MyReporter instance;

	private StopWatch stopWatch;

	private MyReporter(String filepath) {
		super(filepath);
		try {

			stopWatch = new StopWatch();
			stopWatch.start();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public synchronized static MyReporter getInstance(String filepath) {

		if (instance == null) {
			instance = new MyReporter(filepath);
		}
		return instance;

	}

	public StopWatch getStopWatch() {
		return stopWatch;
	}

	public void log(LogStatus status, String log, ExtentTest test) {
		test.log(status, log);
	}

}
