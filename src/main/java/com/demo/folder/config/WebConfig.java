package com.demo.folder.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
@ComponentScan(basePackages = {"com.demo.folder.controller"})
public class WebConfig implements WebMvcConfigurer {
  // You don't need to add resource handlers manually for Swagger UI or OpenAPI
}
