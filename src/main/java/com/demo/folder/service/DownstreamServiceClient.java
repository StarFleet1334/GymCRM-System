package com.demo.folder.service;

import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DownstreamServiceClient {


  private final RestTemplate restTemplate;

  public DownstreamServiceClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public String callService() {
    HttpHeaders headers = new HttpHeaders();
    String transactionId = MDC.get("transactionId");
    headers.set("Transaction-ID", transactionId);
    return restTemplate.getForObject("http://downstream-service/api", String.class, headers);
  }
}
