package com.glomming.shared.mcs.test;

import com.amazonaws.services.cloudsearchdomain.model.SearchResult;
import com.amazonaws.util.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glomming.shared.mcs.MatchmakerCloudSearchConfiguration;
import com.glomming.shared.mcs.controller.MatchmakerCloudSearchServiceController;
import com.glomming.shared.mcs.dto.MCSQuery;
import com.glomming.shared.mcs.dto.Matchmaker;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {MatchmakerCloudSearchConfiguration.class, MockServletContext.class})
@WebAppConfiguration
@IntegrationTest
public class MatchmakerCloudSearchRestServiceTest extends AbstractIntegrationTest {

  private static final Logger logger = LoggerFactory.getLogger(MatchmakerCloudSearchRestServiceTest.class);

  @Autowired
  protected MatchmakerCloudSearchServiceController mcsServiceController;

  @Override
  public Object getController() {
    return mcsServiceController;
  }

  static private ObjectMapper objectMapper = new ObjectMapper();

  @Before
  public void setUp() throws Exception {
    mvc = MockMvcBuilders.standaloneSetup(getController()).build();
  }

  @After
  public void tearDown() throws Exception {
  }


  @Test
  public void testUpsert() throws Exception {
    List<Matchmaker> documentsToInsert = CloudSearchServiceTest.createRandomMatchmaker(1);
    String jsonDocuments = objectMapper.writeValueAsString(documentsToInsert);
    MockHttpServletRequestBuilder upsertDocumentsRequest = MockMvcRequestBuilders.post("/mcs/v1/" + MatchmakerCloudSearchServiceController.restResourceName);
    setJsonContentString(upsertDocumentsRequest, jsonDocuments);
    ResultActions resultActions = mvc.perform(upsertDocumentsRequest).andExpect(status().isOk());
    String response = getResponseString(resultActions);
    JSONObject jsonObject = new JSONObject(response);
    Assert.assertTrue(jsonObject.getString("status").contentEquals("success"));
    Assert.assertEquals(jsonObject.getInt("adds"), 1);
  }

  @Test
  public void testSearch() throws Exception {

    MCSQuery query = new MCSQuery();
    query.facets = CloudSearchServiceTest.facetThree;
    query.queryString = CloudSearchServiceTest.queryFour;
    String queryString = objectMapper.writeValueAsString(query);
    MockHttpServletRequestBuilder searchDocumentRequest = MockMvcRequestBuilders.get("/mcs/v1/" + MatchmakerCloudSearchServiceController.restResourceName);
    searchDocumentRequest.param("query", queryString);
    ResultActions resultActions = mvc.perform(searchDocumentRequest).andExpect(status().isOk());
    String response = getResponseString(resultActions);
    SearchResult searchResult = objectMapper.readValue(response, SearchResult.class);
    Assert.assertTrue(searchResult.getHits().getFound() > 0);
    Assert.assertEquals(searchResult.getFacets().size(), 2);
    Assert.assertTrue(searchResult.getHits().getFound().longValue() > 0);
  }

}
