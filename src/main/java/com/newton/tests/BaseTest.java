package com.newton.tests;

import java.lang.reflect.Method;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;

import com.newton.executor.Executioner;
import com.newton.reporter.MyReporter;
import com.newton.utils.Config;
import com.newton.utils.JsonParser;
import com.newton.utils.Util;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.sun.jersey.api.client.Client;

/**
 * 
 * @author chhagan
 *
 */
public class BaseTest {
	Client client;
	Executioner executor = new Executioner();
	String baseURL = Config.baseURL;

	JsonParser jsonParser;
	String startReport = "<!DOCTYPE html> <html> <head> <style> table, th, td { border: 1px solid black; border-collapse: collapse; } th, td { padding: 5px; text-align: left; } </style> </head> <body> ";
	String startTable = "<table style='width:100%'> <caption><h3>TESTCASE_NAME</h3></caption> <tr> <th>Start Time</th> <th>Duration</th> <th>Step Description</th> <th>Status</th> <th>Response</th> </tr>";
	String endReport = "</body> </html>";
	String endTable = "</table>";
	String requestData;
	Util util;
	String urlParams;
	MyReporter extentReporter;
	ExtentTest test;

	@BeforeSuite
	public void beforeSuite() {

		// reporter = MyReporter.getInstance();
		System.out.println("existing value : " + System.getProperty("hudson.model.DirectoryBrowserSupport.CSP"));
		System.clearProperty("hudson.model.DirectoryBrowserSupport.CSP");

		System.setProperty("hudson.model.DirectoryBrowserSupport.CSP", "sandbox allow-scripts; default-src 'self'; script-src * 'unsafe-eval'; img-src *; style-src * 'unsafe-inline'; font-src *");

		System.out.println("after value : " + System.getProperty("hudson.model.DirectoryBrowserSupport.CSP"));
		util = Util.getInstance();
		extentReporter = MyReporter.getInstance(util.getReportPath());

		jsonParser = new JsonParser();

	}

	@BeforeMethod
	public void setup(@Optional Method method) {
		// System.setProperty("http.proxyHost", "localhost");
		// System.setProperty("http.proxyPort", "8090");
		// System.setProperty("https.proxyHost", "localhost");
		// System.setProperty("https.proxyPort", "8090");
		String tempUrl = System.getenv("BASE_URL");
		if (tempUrl != null && tempUrl.length() > 10) {
			baseURL = tempUrl;
		}
		client = Client.create();
	}

	@AfterMethod(alwaysRun = true)
	public void clean(ITestResult result) {
		// endTable();
		if (result.getStatus() == ITestResult.FAILURE) {
			test.log(LogStatus.FAIL, result.getThrowable());
		} else if (result.getStatus() == ITestResult.SKIP) {
			test.log(LogStatus.SKIP, "Test skipped " + result.getThrowable());
		} else {
			test.log(LogStatus.PASS, "Test passed");
		}
		extentReporter.endTest(test);

	}

	@AfterSuite
	public void afterSuite() {
		
		System.out.println(util.getReportPath());
		extentReporter.flush();

	}

}
