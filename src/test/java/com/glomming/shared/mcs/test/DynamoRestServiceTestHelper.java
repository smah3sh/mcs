package com.glomming.shared.mcs.test;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Helper class with convenience methods.
 */
public class DynamoRestServiceTestHelper extends TestHelper {

  static final Random generator = new Random();

  public static String createAttributes(Integer numAttributes) throws JSONException {
    // Create between 1 and 10 ATTRIBUTES
    int random = generator.nextInt(10) + 1;
    JSONObject jsonObject = new JSONObject();
    if (numAttributes == null)
      numAttributes = new Integer(random);
    for (int i = 0; i < numAttributes; i++) {
      jsonObject.put("key_" + i, UUID.randomUUID().toString());
    }
    return jsonObject.toString();
  }

  public static String createAttributeWithValue(String key, Object value) throws JSONException {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put(key, value);
    return jsonObject.toString();
  }


  public static String createNamedAttribute(String attribute) throws JSONException {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put(attribute, UUID.randomUUID().toString());
    return jsonObject.toString();
  }

  public static String createNamedAttributeAndValue(String attribute, String value) throws JSONException {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put(attribute, value);
    return jsonObject.toString();
  }

  public static String updateStringAttributes(List<String> attributes) throws JSONException {
    JSONObject jsonObject = new JSONObject();
    for (String attribute : attributes) {
      jsonObject.put(attribute, UUID.randomUUID().toString());
    }
    return jsonObject.toString();
  }

  /**
   * Check if all the keys and the values in the request are present in the response. The response might contain additional data which is ignored.
   *
   * @param requestAttributes
   * @param responseAttributes
   * @return
   * @throws Exception
   */
  public static boolean checkIfAttributesAreEqual(String requestAttributes, String responseAttributes) throws Exception {
    JSONObject requestJson = new JSONObject(requestAttributes);
    JSONObject responseJson = new JSONObject(responseAttributes);

    Iterator iterator = requestJson.keys();
    while (iterator.hasNext()) {
      String requestAttribute = iterator.next().toString();
      if (!responseJson.has(requestAttribute) || !requestJson.get(requestAttribute).equals(responseJson.get(requestAttribute)))
        return false;
    }
    return true;
  }


  public static boolean checkIfAttributesContainKeyAndValue(String attributes, String key, String value) throws Exception {
    JSONObject jsonObject = new JSONObject(attributes);
    if (!jsonObject.has(key) || !jsonObject.get(key).equals(value))
      return false;
    return true;
  }

  public static boolean checkIfAttributesContainKey(String attributes, String key) throws Exception {
    JSONObject jsonObject = new JSONObject(attributes);
    if (!jsonObject.has(key))
      return false;
    return true;
  }

}

