package com.demo.folder.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({
    org.springdoc.core.configuration.SpringDocConfiguration.class,
    org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration.class,
    org.springdoc.webmvc.ui.SwaggerConfig.class,
    org.springdoc.core.properties.SwaggerUiConfigProperties.class,
    org.springdoc.core.properties.SwaggerUiOAuthProperties.class,
    org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.class
})

public class OpenApiConfig {

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("public-apis")
        .pathsToMatch("/api/**")
        .build();
  }

}
