package com.glomming.shared.mcs.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class AbstractIntegrationTest {
  @Rule
  public ExpectedException exception = ExpectedException.none();

  protected MockMvc mvc;

  @Autowired
  protected ObjectMapper jsonObjectMapper;

  protected static final String APPID = "com.glomming.app";

  public Object getController() {
    return null;
  }

  public void setUp() throws Exception {
    mvc = MockMvcBuilders.standaloneSetup(getController()).build();
  }

  public void setJsonContentString(MockHttpServletRequestBuilder request, String json) {
    request.content(json);
    request.contentType(MediaType.APPLICATION_JSON);
    request.accept(MediaType.APPLICATION_JSON);
  }

  public String getResponseString(ResultActions actions)
      throws Exception {
    MvcResult mvcResult = actions.andReturn();
    String response = mvcResult.getResponse().getContentAsString();
    return response;
  }

  public String getResponseHeader(ResultActions resultActions, String header) throws Exception {
    MvcResult mvcResult = resultActions.andReturn();
    return mvcResult.getResponse().getHeader(header);
  }
}
