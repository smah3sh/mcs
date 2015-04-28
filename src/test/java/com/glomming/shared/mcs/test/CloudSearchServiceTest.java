package com.glomming.shared.mcs.test;

import com.amazonaws.services.cloudsearchdomain.model.SearchResult;
import com.amazonaws.services.cloudsearchdomain.model.UploadDocumentsResult;
import com.amazonaws.services.cloudsearchv2.AmazonCloudSearchClient;
import com.glomming.shared.mcs.controller.MatchmakerCloudSearchServiceController;
import com.glomming.shared.mcs.dto.MCSQuery;
import com.glomming.shared.mcs.dto.MatchFields;
import com.glomming.shared.mcs.dto.Matchmaker;
import com.glomming.shared.mcs.service.CloudSearchService;
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

import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {DynamoRestServiceTestConfiguration.class, MockServletContext.class, AmazonCloudSearchClient.class})
@WebAppConfiguration
@IntegrationTest
public class CloudSearchServiceTest {

  private static final Logger logger = LoggerFactory.getLogger(MatchmakerCloudSearchServiceController.class);

  @Autowired
  public CloudSearchService cloudSearchService;

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  // Query based on weapon
  static final String queryOne = "(or weapons:'GUN' weapons:'CANNON' weapons:'DRONE')";
  // Query based on weapon and last_login date
  static final String queryTwo = "(and " +
      "(or weapons:'GUN' weapons:'CANNON' weapons:'DRONE')" +
      "(and last_login:['2013-05-25T00:00:00Z','2014-10-25T00:00:00Z']))";
  // Query based on weapon, last_login and points
  static final String queryThree = "(and " +
      "(or weapons:'GUN' weapons:'CANNON' weapons:'DRONE')" +
      "(and last_login:['2013-05-25T00:00:00Z','2014-10-25T00:00:00Z'])" +
      "(and points:[100, 200]))";
  // Query based on weapon, last_login, points and elo_rating
  static final String queryFour = "(and " +
      "(or weapons:'GUN' weapons:'CANNON' weapons:'DRONE')" +
      "(and last_login:['2013-05-25T00:00:00Z','2014-10-25T00:00:00Z'])" +
      "(and points:[100, 200])" +
      "(and elo_rating:[1000, 2000]))";

  static final String facetOne = "{\n" +
      "    \"weapons\": {\n" +
      "        \"sort\": \"count\",\n" +
      "        \"size\": 5\n" +
      "    }\n" +
      "}";

  // "points":{"buckets":["[0,100]","[101,200]","[201,300]","[301,400]","[401,}"]}
  static final String facetTwo = "{\n" +
      "    \"points\": {\n" +
      "        \"buckets\": [\n" +
      "            \"[0,100]\",\n" +
      "            \"[101,200]\",\n" +
      "            \"[201,300]\",\n" +
      "            \"[301,400]\",\n" +
      "            \"[401,}\"\n" +      // No upper limit for this bucket
      "        ]\n" +
      "    }\n" +
      "}";

  //  {"weapons":{"sort":"count","size":5},"points":{"buckets":["[0,100]","[101,200]","[201,300]","[301,400]","[401,}"]}}
  static final String facetThree =
      "{\n" +
      "    \"weapons\": {\n" +
      "        \"sort\": \"count\",\n" +
      "        \"size\": 5\n" +
      "    },\n" +
      "    \"points\": {\n" +
      "        \"buckets\": [\n" +
      "            \"[0,100]\",\n" +
      "            \"[101,200]\",\n" +
      "            \"[201,300]\",\n" +
      "            \"[301,400]\",\n" +
      "            \"[401,}\"\n" +
      "        ]\n" +
      "    }\n" +
      "}";

  String [] queryStrings = {queryOne, queryTwo, queryThree, queryFour};
  String [] facetStrings = {facetOne, facetTwo, facetThree};

  @Test
  public void testSearch() throws Exception {
    long numResults = 10;
    for (String queryString : queryStrings) {
      for (String facet : facetStrings) {
        MCSQuery query = new MCSQuery();
        query.queryString = queryString;
        query.facets = facet;
        query.numRecordsToReturn = numResults;
        SearchResult result = cloudSearchService.structuredSearch(query);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.getHits().getFound() > 0);
        logger.info("RESULT:::::: " + result.toString());
      }
    }
  }

  @Test
  public void testPaginatedSearch() throws Exception {
    long numResults = 52;

    MCSQuery query = new MCSQuery();
    query.queryString = queryFour;
    query.facets = facetThree;
    query.numRecordsToReturn = numResults;
    SearchResult result = null;
    long lastStartIndex = -1;
    long count = 0;
    do {
      result = cloudSearchService.structuredSearch(query);
      Assert.assertNotNull(result);
      Assert.assertTrue(result.getHits().getFound() >= 0);
      count += result.getHits().getHit().size();
      Assert.assertEquals(result.getFacets().size(), 2);
      Assert.assertTrue(result.getHits().getStart() > lastStartIndex);
      lastStartIndex = result.getHits().getStart();
      query.cursor = result.getHits().getCursor();
    } while (result.getHits().getHit().size() > 0);
    // Assuming the results do not change when this test is executed, the next line must be true
    Assert.assertEquals(result.getHits().getFound().longValue(), count);
  }


  @Test
  public void testUpsert() throws Exception {
    List<Matchmaker> matchmakerList = createRandomMatchmaker(1);
    UploadDocumentsResult result = cloudSearchService.upsert(matchmakerList);
    logger.info(result.toString());
  }

  public static Random generator = new Random();

  public static List<Matchmaker> createRandomMatchmaker(int count) {

    List<Matchmaker> listMatchmaker = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      Matchmaker matchmaker = new Matchmaker();
      matchmaker.fields.elo_rating = (float)((double)generator.nextInt(100000)/ 100);
      matchmaker.fields.randomizer = generator.nextInt(100) + 1;
      matchmaker.fields.points = generator.nextInt(1000) + 1;
      matchmaker.fields.last_login = MatchFields.dateFormatter.format(new Date()) + "Z";
      List<String> weapons = new ArrayList<>();
      weapons.add("BOMBER");
      matchmaker.fields.weapons = weapons;
      matchmaker.id  = (UUID.randomUUID().toString());
      matchmaker.type = "add";
      listMatchmaker.add(matchmaker);
    }
    return listMatchmaker;
  }

}
