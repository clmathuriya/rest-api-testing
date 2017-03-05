package com.newton.executor;

import java.io.IOException;

import org.apache.commons.lang3.time.StopWatch;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.testng.Assert;
import org.testng.Reporter;

import com.newton.reporter.MyReporter;
import com.newton.utils.MyJsonComparator;
import com.newton.utils.Util;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

public class Executioner {
	private WebDriver driver;
	private Util util;
	private WebDriverWait wait;
	private long startTime;
	private long duration;
	public StopWatch stopWatch;

	public MyReporter reporter;

	public Executioner() {
		util = Util.getInstance();
		reporter = MyReporter.getInstance(util.getReportPath());
		stopWatch = reporter.getStopWatch();
	}

	public MyReporter getReporter() {
		return reporter;
	}

	private void addStep(long start, long duration, String step, String status, String screenShot, ExtentTest test) {

		if (status.toLowerCase().equals("pass")) {

			if (screenShot.length() > 4) {
				reporter.log(LogStatus.PASS, step + " </td><td class='step-details'> <a target='_blank' href='"
						+ screenShot + "' > response </a></td>", test);
			} else {
				reporter.log(LogStatus.PASS, step + " </td><td class='step-details'>" + screenShot + "</td>", test);
			}
		} else {

			if (screenShot.length() > 4) {
				reporter.log(LogStatus.FAIL, step + " </td> <td class='step-details'><a target='_blank' href='"
						+ screenShot + "' > response </a></td>", test);
			} else {
				reporter.log(LogStatus.FAIL, step + " </td> <td class='step-details'> " + screenShot + " </td>", test);
			}

		}
	}

	public String doHttpGet(Builder builder, Class<ClientResponse> c, ExtentTest test) {
		String screenshot = "NA";

		ClientResponse clientResponse = null;
		String output = "";
		try {

			startTime = stopWatch.getTime();
			clientResponse = builder.accept("application/json").get(c);
			output = clientResponse.getEntity(String.class);

			screenshot = util.saveResponse(output);

			addStep(startTime, stopWatch.getTime() - startTime, "Method : GET <br> URL: " + clientResponse.getLocation()
					+ " <br> <br> Response Code:" + clientResponse.getStatus(), "Pass", screenshot, test);

			return output;
		} catch (AssertionError exception) {
			addStep(startTime, stopWatch.getTime() - startTime, "HTTP GET \n URL: " + clientResponse.getLocation()
					+ " \n Response Code:" + clientResponse.getStatus(), "Failed", screenshot, test);

		} catch (IOException e) {

			e.printStackTrace();

		} finally {
			return output;
		}

	}

	public String doHttpGet(WebResource builder, Class<ClientResponse> c, ExtentTest test) {
		String screenshot = "NA";

		ClientResponse clientResponse = null;
		String output = "";
		try {

			startTime = stopWatch.getTime();
			clientResponse = builder.accept("application/json").get(c);
			output = clientResponse.getEntity(String.class);

			screenshot = util.saveResponse(output);

			addStep(startTime, stopWatch.getTime() - startTime, "Method : GET <br> URL: " + builder.getURI()
					+ " <br> <br> Response Code:" + clientResponse.getStatus(), "Pass", screenshot, test);

			return output;
		} catch (AssertionError exception) {
			addStep(startTime, stopWatch.getTime() - startTime,
					"HTTP GET \n URL: " + builder.getURI() + " \n Response Code:" + clientResponse.getStatus(),
					"Failed", screenshot, test);

		} catch (IOException e) {

			e.printStackTrace();

		} finally {
			return output;
		}

	}

	public String doHttpPost(WebResource webResource, Class<ClientResponse> c, String requestData, ExtentTest test) {
		String screenshot = "NA";

		ClientResponse clientResponse = null;
		String output = "";
		try {

			startTime = stopWatch.getTime();
			clientResponse = webResource.type("application/json").post(c, requestData);
			output = clientResponse.getEntity(String.class);

			// System.out.println(output);

			screenshot = util.saveResponse(output);

			addStep(startTime, stopWatch.getTime() - startTime, "Method : POST <br> URL: " + webResource.getURI()
					+ " <br> <br> Request Data : " + requestData + " <br>Response Code:" + clientResponse.getStatus(),
					"Pass", screenshot, test);

			return output;
		} catch (AssertionError exeption) {
			addStep(startTime, stopWatch.getTime() - startTime,
					"HTTP GET \n URL: " + webResource.getURI() + " \n Response Code:" + clientResponse.getStatus(),
					"Failed", screenshot, test);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();//

		} finally {
			return output;
		}

	}

	public String doHttpPost(WebResource webResource, Class<ClientResponse> c, ExtentTest test) {
		String screenshot = "NA";

		ClientResponse clientResponse = null;
		String output = "";
		try {

			startTime = stopWatch.getTime();
			clientResponse = webResource.type("application/json").post(c);
			output = clientResponse.getEntity(String.class);

			// System.out.println(output);

			screenshot = util.saveResponse(output);

			addStep(startTime, stopWatch.getTime() - startTime, "Method : POST <br> URL: " + webResource.getURI()
					+ " <br>Response Code:" + clientResponse.getStatus(), "Pass", screenshot, test);

			return output;
		} catch (AssertionError exeption) {
			addStep(startTime, stopWatch.getTime() - startTime,
					"HTTP GET \n URL: " + webResource.getURI() + " \n Response Code:" + clientResponse.getStatus(),
					"Failed", screenshot, test);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();//

		} finally {
			return output;
		}

	}

	public Executioner verifyJsonEquals(String expected, String actual, String message, boolean flag, ExtentTest test) {

		String screenshot = "NA";
		try {

			startTime = stopWatch.getTime();
			// JSONAssert.assertEquals(expected, actual, flag);
			JSONCompareResult result = JSONCompare.compareJSON(expected, actual,
					new MyJsonComparator(JSONCompareMode.STRICT));
			if (!result.passed())
				throw new AssertionError(result.getMessage());
			addStep(startTime, stopWatch.getTime() - startTime, message, "Pass", screenshot, test);
			Reporter.log("Staus: PASS, Expected: " + expected + ", Actual : " + actual, true);
			return this;
		} catch (AssertionError | org.json.JSONException exception) {
			addStep(startTime, stopWatch.getTime() - startTime, message + " <br> " + exception.getMessage(), "Failed",
					screenshot, test);
			Assert.fail(exception.getMessage());

			return this;

		}

	}

	public Executioner verifyEquals(Object expected, Object actual, String message, ExtentTest test) {

		String screenshot = "NA";
		try {

			startTime = stopWatch.getTime();
			Assert.assertEquals(expected, actual);
			addStep(startTime, stopWatch.getTime() - startTime, message, "Pass", screenshot, test);
			Reporter.log("Staus: PASS, Expected: " + expected + ", Actual : " + actual, true);
			return this;
		} catch (AssertionError | org.json.JSONException exception) {
			addStep(startTime, stopWatch.getTime() - startTime, message + " <br> " + exception.getMessage(), "Failed",
					screenshot, test);
			// Assert.fail(exception.getMessage());

			return this;

		}

	}

}
