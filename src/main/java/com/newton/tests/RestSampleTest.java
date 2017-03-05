package com.newton.tests;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.newton.utils.Config;
import com.newton.utils.ExcelReader;
import com.relevantcodes.extentreports.ExtentTest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RestSampleTest extends BaseTest {
	String urlParamsJson;
	String requestJson;

	@Test(dataProvider = "PostSearchDataProvider")
	@Parameters("testData")
	public void restTest(Map<String, String> testData)
			throws JsonGenerationException, JsonMappingException, IOException, JSONException {
		Config.vars = testData;
		requestJson = testData.get("RequestData");

		test = extentReporter.startTest(testData.get("TestName"));

		if (requestJson != null && requestJson.length() > 2) {
			requestJson = util.readFileAsString("requests/" + requestJson + ".json");
		}

		urlParams = testData.get("URLParams");
		if (urlParams != null && urlParams.length() > 2) {
			urlParamsJson = util.readFileAsString("params/" + urlParams + ".json");
		} else {
			urlParamsJson = "{}";
		}
		String actualJson = "";

		WebResource webResource = client.resource(baseURL + testData.get("APIEndPoint"));

		webResource = util.setUrlParams(webResource, urlParamsJson);
		

		switch (testData.get("HTTPMethod").toUpperCase()) {

		case "GET": {
			actualJson = executor.doHttpGet(webResource, ClientResponse.class, test);
			break;
		}
		case "POST": {
			if (requestJson != null && requestJson.length() > 2) {
				actualJson = executor.doHttpPost(webResource, ClientResponse.class,
						new JSONObject(requestJson).toString(), test);
			} else {
				actualJson = executor.doHttpPost(webResource, ClientResponse.class, test);
			}

		}

		}

		JSONObject response = new JSONObject(actualJson);
		// verify success message
		
		executor.verifyEquals(response!=null,true, "verify success message", test);

	}

	@DataProvider(name = "PostSearchDataProvider"/* , parallel = true */)
	public Object[][] postSearchData() {
		return new ExcelReader().getUserDataFromExcel("testData.xlsx", "tests");
	}

}
