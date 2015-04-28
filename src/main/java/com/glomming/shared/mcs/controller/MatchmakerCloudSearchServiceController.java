package com.glomming.shared.mcs.controller;

import com.amazonaws.services.cloudsearchdomain.model.SearchResult;
import com.amazonaws.services.cloudsearchdomain.model.UploadDocumentsResult;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glomming.shared.mcs.ServiceName;
import com.glomming.shared.mcs.dto.MCSQuery;
import com.glomming.shared.mcs.dto.Matchmaker;
import com.glomming.shared.mcs.service.CloudSearchService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@ManagedResource(description = "REST Matchmaking service backed by AWS CloudSearch")
@RestController
@EnableAutoConfiguration
@RequestMapping(ServiceName.SERVICE_PATH)
public class MatchmakerCloudSearchServiceController {

  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MatchmakerCloudSearchServiceController.class);
  public static final String restResourceName = "matchmaker";
  private static ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private CloudSearchService cloudSearchService;

  @Autowired
  public void setStringAttributeService(CloudSearchService cloudSearchService) {
    this.cloudSearchService = cloudSearchService;
  }


  @RequestMapping(value = "/" + restResourceName , method = RequestMethod.POST, produces = "application/json")
  @ResponseStatus(value = HttpStatus.OK)
  public @ResponseBody String upsert(@RequestBody List<Matchmaker> jsonDocuments)
      throws Exception {
    UploadDocumentsResult result = null;
    String response = null;
    try {
      result = cloudSearchService.upsert(jsonDocuments);
      response = objectMapper.writeValueAsString(result);
    } catch (Exception e) {
      logger.error("", e);
    }
    return response;
  }


  @RequestMapping(value = "/" + restResourceName, method = RequestMethod.GET, produces = "application/json")
  @ResponseStatus(value = HttpStatus.OK)
  public @ResponseBody String search(
      @RequestParam (value = "query", required = true) String query)
      throws Exception {

    String response = null;
    SearchResult documents = null;

    try {
      MCSQuery mcsQuery = objectMapper.readValue(query, MCSQuery.class);
      documents = cloudSearchService.structuredSearch(mcsQuery);
      response = objectMapper.writeValueAsString(documents);
    } catch (Exception e) {
      logger.error("", e);
    }
    return response;
  }

}
 