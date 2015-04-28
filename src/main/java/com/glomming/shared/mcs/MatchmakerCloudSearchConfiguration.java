package com.glomming.shared.mcs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.glomming.shared.mcs.service.CloudSearchService;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackageClasses = {MatchmakerCloudSearchConfiguration.class, MatchmakerCloudSearchService.class})
@Import({CloudSearchService.class})
public class MatchmakerCloudSearchConfiguration extends WebMvcConfigurerAdapter implements ApplicationContextAware, EmbeddedServletContainerCustomizer {

  public ApplicationContext applicationContext;

  @PostConstruct
  public void initialize() throws Exception {
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
  }

  @Override
  public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    configurer.enable("sas");
  }

  @Bean
  public InternalResourceViewResolver jspViewResolver() {
    InternalResourceViewResolver bean = new InternalResourceViewResolver();
    bean.setPrefix("/WEB-INF/views/");
    bean.setSuffix(".jsp");
    return bean;
  }

  @Bean(name = "multipartResolver")
  public CommonsMultipartResolver getMultipartResolver() {
    return new CommonsMultipartResolver();
  }

  @Bean(name = "messageSource")
  public ReloadableResourceBundleMessageSource getMessageSource() {
    ReloadableResourceBundleMessageSource resource = new ReloadableResourceBundleMessageSource();
    resource.setBasename("classpath:messages");
    resource.setDefaultEncoding("UTF-8");
    return resource;
  }

  @Bean
  public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
    return new Jackson2ObjectMapperBuilder();
  }

  @Bean
  public View defaultView() {
    MappingJackson2JsonView bean = new MappingJackson2JsonView();
    bean.setObjectMapper(jsonObjectMapper());
    return bean;
  }

  @Bean
  public ViewResolver viewResolver() {
    return new ViewResolver() {
      public View resolveViewName(String viewName, Locale locale) throws Exception {
        return new MappingJackson2JsonView();
      }
    };
  }

  @Bean
  public HttpMessageConverter<?> messageConverter() {
    return new MappingJackson2HttpMessageConverter(jsonObjectMapper());
  }

  @Bean
  public HttpMessageConverter<?> stringMessageConverter() {
    return new StringHttpMessageConverter();
  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(new ByteArrayHttpMessageConverter());
    converters.add(stringMessageConverter());
    converters.add(new SourceHttpMessageConverter<>());
    converters.add(new AllEncompassingFormHttpMessageConverter());
    converters.add(messageConverter());
  }

  @Bean
  public String applicationAddress() {
    return ServiceName.SERVICE_NAME + "." + UUID.randomUUID().toString().replace("-", "");
  }

  @Bean
  public String applicationVersion() {
    return MatchmakerCloudSearchService.class.getPackage().getImplementationVersion();
  }

  @Bean(name = "jsonObjectMapper", autowire = Autowire.BY_NAME)
  public ObjectMapper jsonObjectMapper() {
    ObjectMapper bean = new ObjectMapper(new JsonFactory());
    bean.disableDefaultTyping();
    bean.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    bean.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
    bean.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);

    bean.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    bean.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

    //bean.registerSubtypes(LookupResult.class);
    return bean;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  public void customize(ConfigurableEmbeddedServletContainer container) {
  }

  /**
   * This class is excluded when building a war file and included only when running this under Jetty.
   *
   * @param bindPort
   * @param maxThreads
   * @param minThreads
   * @param idleTimeout
   * @return
   */
  @ConditionalOnClass(name = "org.eclipse.jetty.webapp.Configuration")
  @Bean
  public JettyEmbeddedServletContainerFactory jettyEmbeddedServletContainerFactory(@Value("${http.bindPort:8025}") final String bindPort,
                                                                                   @Value("${jetty.threadPool.maxThreads:200}") final String maxThreads,
                                                                                   @Value("${jetty.threadPool.minThreads:8}") final String minThreads,
                                                                                   @Value("${jetty.threadPool.idleTimeout:60000}") final String idleTimeout) {

    final JettyEmbeddedServletContainerFactory factory = new JettyEmbeddedServletContainerFactory(Integer.valueOf(bindPort));
    factory.addServerCustomizers(new JettyServerCustomizer() {
      @Override
      public void customize(final Server server) {
        // Tweak the connection pool used by Jetty to handle incoming HTTP connections
        final QueuedThreadPool threadPool = server.getBean(QueuedThreadPool.class);
        threadPool.setMaxThreads(Integer.valueOf(maxThreads));
        threadPool.setMinThreads(Integer.valueOf(minThreads));
        threadPool.setIdleTimeout(Integer.valueOf(idleTimeout));

      }
    });
    factory.addServerCustomizers(new JettyServerCustomizer() {
      @Override
      public void customize(final Server server) {
        // Expose Jetty managed beans to the JMX platform server provided by Spring
        final MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
        server.addBean(mbContainer);
      }
    });
    factory.setContextPath("");
    return factory;
  }

}
