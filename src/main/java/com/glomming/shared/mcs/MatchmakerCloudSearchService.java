package com.glomming.shared.mcs;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Main entry point for the matchmaker-cloudsearch-service.
 *
 * @author mahesh.subramanian
 */
public class MatchmakerCloudSearchService extends SpringBootServletInitializer implements ApplicationContextAware {

  @SuppressWarnings({"unused"})
  private ApplicationContext applicationContext;


  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }


  public static void main(String[] args) {

    System.setProperty("endpoints.autoconfig.enabled", "true");
    System.setProperty("endpoints.beans.enabled", "true");
    System.setProperty("endpoints.configprops.enabled", "true");
    System.setProperty("endpoints.dump.enabled", "true");
    System.setProperty("endpoints.env.enabled", "true");
    System.setProperty("endpoints.health.enabled", "true");
    System.setProperty("endpoints.info.enabled", "true");
    System.setProperty("endpoints.metrics.enabled", "true");
    System.setProperty("endpoints.mappings.enabled", "true");
    System.setProperty("endpoints.shutdown.enabled", "true");
    System.setProperty("endpoints.trace.enabled", "true");
    System.setProperty("endpoints.health.sensitive", "false");

    SpringApplication application = new SpringApplication(MatchmakerCloudSearchConfiguration.class, MatchmakerCloudSearchService.class);
    application.setShowBanner(false);
//    application.run();
    application.run("--debug");

  }


//  @Autowired
//  private SpringSwaggerConfig swaggerConfig;
//
//  @Bean
//  public SwaggerSpringMvcPlugin groupOnePlugin() {
//    return new SwaggerSpringMvcPlugin(swaggerConfig);
////            .directModelSubstitute(LocalDate.class, String.class)
////            .directModelSubstitute(LocalDateTime.class, String.class);
//  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(MatchmakerCloudSearchConfiguration.class, MatchmakerCloudSearchService.class);
  }
}
