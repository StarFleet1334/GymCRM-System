package com.demo.folder.controller;

import com.demo.folder.config.SecurityConfig;
import com.demo.folder.config.SpringConfig;
import com.demo.folder.config.WebConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SpringConfig.class, WebConfig.class, SecurityConfig.class})
class LoginControllerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoginControllerTest.class);

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext wac;

  private String username;
  private String password;
  private MockHttpSession session;

  @BeforeEach
  public void setup() throws Exception {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

    String jsonBody = """
        {
            "firstName": "John",
            "lastName": "Doe",
            "dateOfBirth": "1990-01-01",
            "address": "Tbilisi Tamarashvili Street"
        }
        """;

    MvcResult registrationResult = mockMvc.perform(post("/api/trainee/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonBody))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    String registrationResponse = registrationResult.getResponse().getContentAsString();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(registrationResponse);
    username = jsonNode.get("username").asText();
    password = jsonNode.get("password").asText();

    LOGGER.info("Registered trainee with username: {}", username);
    LOGGER.info("Registered trainee with password: {}", password);
  }

  @Test
  public void testAuthentication() throws Exception {
    // Perform Login
    MvcResult loginResult = mockMvc.perform(get("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Login successful")))
        .andReturn();

    session = (MockHttpSession) loginResult.getRequest().getSession(false);
  }

  @Test
  public void testChangePassword() throws Exception {
    MvcResult loginResult = mockMvc.perform(get("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Login successful")))
        .andReturn();

    session = (MockHttpSession) loginResult.getRequest().getSession(false);

    String newPassword = "newPassword123";

    String changePasswordJson = "{"
        + "\"username\": \"" + username + "\","
        + "\"password\": \"" + password + "\","
        + "\"newPassword\": \"" + newPassword + "\""
        + "}";

    mockMvc.perform(put("/api/login/change-login")
            .session(session)
            .contentType(MediaType.APPLICATION_JSON)
            .content(changePasswordJson))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("User Password got successfully updated.")));

    mockMvc.perform(get("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\": \"" + username + "\", \"password\": \"" + newPassword + "\"}"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Login successful")));
  }

}
