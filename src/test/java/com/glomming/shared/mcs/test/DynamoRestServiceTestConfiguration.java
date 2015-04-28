package com.glomming.shared.mcs.test;


import com.glomming.shared.mcs.MatchmakerCloudSearchConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {MatchmakerCloudSearchConfiguration.class})
public class DynamoRestServiceTestConfiguration {
}
