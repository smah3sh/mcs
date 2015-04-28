package com.glomming.shared.mcs.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.cloudsearchdomain.AmazonCloudSearchDomainClient;
import com.amazonaws.services.cloudsearchdomain.model.*;
import com.amazonaws.util.json.JSONException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.glomming.shared.mcs.dto.MCSQuery;
import com.glomming.shared.mcs.dto.Matchmaker;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CloudSearchService {


  @Value("${cloudsearch.domain.document.endpoint:doc-<fill in endpoint here>.cloudsearch.amazonaws.com}")
  private String documentEndPoint;  // For posting data and searching

  @Value("${cloudsearch.domain.search.endpoint:search-<fill in endpoint here>.cloudsearch.amazonaws.com}")
  private String searchEndPoint;  // For searching only

  @Value("${cloudsearch.connection.accesskey:<aws-cloudsearch-access-key>}")
  private String accessKey;

  @Value("${cloudsearch.connection.secretkey:<aws-cloudsearch-secret-key>}")
  private String secretKey;

  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CloudSearchService.class);

  private AmazonCloudSearchDomainClient domain = null;


  @PostConstruct
  public void initialize() {
    domain = new AmazonCloudSearchDomainClient(new AWSCredentials() {
      @Override
      public String getAWSAccessKeyId() {
        return accessKey;
      }

      @Override
      public String getAWSSecretKey() {
        return secretKey;
      }
    });
    domain.setEndpoint(documentEndPoint);
  }

  /**
   * Insert or replace a list of documents
   * @param documents
   * @return
   * @throws JSONException
   * @throws IOException
   */
  public UploadDocumentsResult upsert(List<Matchmaker> documents) throws JSONException, IOException {

    UploadDocumentsRequest uploadDocumentsRequest = new UploadDocumentsRequest();
    InputStream inputStream = null;
    UploadDocumentsResult result = null;

    try {
      ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
      String jsonDocuments = ow.writeValueAsString(documents);

      inputStream = new ByteArrayInputStream(jsonDocuments.getBytes(StandardCharsets.UTF_8));
      uploadDocumentsRequest
          .withDocuments(inputStream)
          .withContentLength((long) jsonDocuments.length())
          .withContentType(ContentType.Applicationjson);
      result = domain.uploadDocuments(uploadDocumentsRequest);
    } catch (Exception e) {
      logger.error("", e);
    } finally {
      inputStream.close();
    }
    return result;
  }


  /**
   * Search using structured queryString parser
   *
   * @param query
   * @return
   * @throws JSONException
   */
  public SearchResult structuredSearch(String query) {
    SearchRequest request = new SearchRequest()
        .withQueryParser("structured")
        .withQuery(query);
    request.setCursor("initial");   // Set cursor value to initial to get first cursor
    request.setSize(20L);
    SearchResult result = domain.search(request);
    return result;
  }

  /**
   * Search with facets
   * @param query
   * @return
   */
  public SearchResult structuredSearch(MCSQuery query) {
    SearchRequest request = new SearchRequest()
        .withQueryParser("structured")
        .withQuery(query.queryString);
    request.setCursor(query.cursor);   // Set cursor value to initial to get first cursor
    request.setSize(query.numRecordsToReturn);
    if (StringUtils.isNotBlank(query.facets))
      request.setFacet(query.facets);
    return domain.search(request);
  }

}
