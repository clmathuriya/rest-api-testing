package com.newton.tests;

import java.lang.reflect.Method;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
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
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

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
	String formParams;
	String headers;
	MyReporter extentReporter;
	ExtentTest test;
	String urlParamsJson;
	String requestJson;
	String formParamsJson;
	String headersJson;
	String actualJson = "";
	JSONObject response;

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
	
	public void defaultRestCall(Map <String, String> testData) throws JSONException{
		//Config.vars = testData;
		test = extentReporter.startTest(testData.get("TestName"));
		setParams(testData);
		WebResource webResource = client.resource(baseURL + testData.get("APIEndPoint"));
		webResource = util.setUrlParams(webResource, urlParamsJson);
		switch (testData.get("HTTPMethod").toUpperCase()) {
		case "GET": {
			
			actualJson = executor.doHttpGet(webResource,testData, ClientResponse.class, test);
			break;
		}
		case "POST": {
			doHTTPPost(webResource, testData);
		}
		
		}
		response = new JSONObject(actualJson);
	}
	
	public void setParams(Map<String, String> testData){
		requestJson = testData.get("RequestData");
		if (requestJson != null && requestJson.length() > 2) {
			requestJson = util.readFileAsString("requests/" + requestJson + ".json");
		}
		urlParams = testData.get("URLParams");
		if (urlParams != null && urlParams.length() > 2) {
			urlParamsJson = util.readFileAsString("params/" + urlParams + ".json");
		} else {
			urlParamsJson = "{}";
		}
		formParams = testData.get("FormParams");
		if (formParams != null && formParams.length() > 2) {
			formParamsJson = util.readFileAsString("params/" + formParams + ".json");
		} else {
			formParamsJson = "{}";
		}
		
		headers = testData.get("RequestHeaders");
		if (headers != null && headers.length() > 2) {
			headersJson = util.readFileAsString("headers/" + headers + ".json");
		} 
		
		
	}
	public void doHTTPPost(WebResource webResource, Map<String, String> testData ) throws JSONException{
		if (requestJson != null && requestJson.length() > 2) {
			actualJson = executor.doHttpPost(webResource,testData, ClientResponse.class,
					new JSONObject(requestJson).toString(), test);
		} else if (formParamsJson !=null && formParamsJson.length() > 2){
			actualJson = executor.doHttpPostWithFomrParams(webResource, testData, ClientResponse.class,
					formParamsJson, test);
		} else {
			actualJson = executor.doHttpPost(webResource, testData, ClientResponse.class, test);
		}
	}

}
