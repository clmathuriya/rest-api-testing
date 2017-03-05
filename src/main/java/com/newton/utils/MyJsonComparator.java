package com.newton.utils;

import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.comparator.DefaultComparator;
import org.skyscreamer.jsonassert.comparator.JSONCompareUtil;

public class MyJsonComparator extends DefaultComparator {
	JSONCompareMode mode;

	public MyJsonComparator(JSONCompareMode mode) {
		super(mode);
		this.mode = mode;

	}

	@Override
	protected void checkJsonObjectKeysExpectedInActual(String prefix, JSONObject expected, JSONObject actual,
			JSONCompareResult result) throws JSONException {
		Set<String> expectedKeys = JSONCompareUtil.getKeys(expected);
		for (String key : expectedKeys) {
			Object expectedValue = expected.get(key);
			if (actual.has(key)) {
				Object actualValue = actual.get(key);
				if (Config.vars.get("IgnoreFields") != null && Config.vars.get("IgnoreFields").contains(key)) {
					customCompareValues(JSONCompareUtil.qualify(prefix, key), expectedValue, actualValue, result);
				} else {

					compareValues(JSONCompareUtil.qualify(prefix, key), expectedValue, actualValue, result);
				}
			} else {
				result.missing(prefix, key);
			}
		}
		// super.checkJsonObjectKeysExpectedInActual(prefix, expected, actual,
		// result);
	}

	public void customCompareValues(String prefix, Object expectedValue, Object actualValue, JSONCompareResult result) {
		if ((expectedValue instanceof Number)) {
			if (actualValue == null || actualValue.equals("") || actualValue.equals(" ")
					|| !(actualValue instanceof Number))
				result.fail(prefix, expectedValue, actualValue);
		} else if (!expectedValue.getClass().isAssignableFrom(actualValue.getClass())) {

			result.fail(prefix, expectedValue, actualValue);
		}
	}

}
