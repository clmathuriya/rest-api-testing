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
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
public class ConnectToParentTests extends BaseTest {
	/**
	 * 
	 * @param testData
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws JSONException
	 */
	@Test(dataProvider = "loginDataProvider", alwaysRun = true)
	@Parameters("testData")
	public void loginTest(Map<String, String> testData) throws JsonGenerationException, JsonMappingException, IOException, JSONException {
		defaultRestCall(testData);
		// verify success message
		executor.verifyEquals(!response.get("errCode").toString().equals("-1"),true, "verify success message", test);
	}
	/**
	 * 
	 * @param testData
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws JSONException
	 */
	@Test(dataProvider = "studentPhotoDataProvider", dependsOnMethods = "loginTest", alwaysRun = true)
	@Parameters("testData")
	public void getStudentPhotoTest(Map<String, String> testData) throws JsonGenerationException, JsonMappingException, IOException, JSONException {
		defaultRestCall(testData);
		// verify success message
		executor.verifyEquals(!response.get("errCode").toString().equals("-1"),true, "verify success message", test);
	}
	@DataProvider (name = "loginDataProvider")
	public Object[][] loginDataProvider() {
		return new ExcelReader().getUserDataFromExcel("testData.xlsx", "login");
	}
	@DataProvider (name = "studentPhotoDataProvider")
	public Object[][] studentPhotoDataProvider() {
		return new ExcelReader().getUserDataFromExcel("testData.xlsx", "studentPhoto");
	}
}