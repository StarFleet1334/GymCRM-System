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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SpringConfig.class, WebConfig.class, SecurityConfig.class})
class TraineeControllerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(TraineeControllerTest.class);

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
  public void testGetAllTrainee() throws Exception {
    mockMvc.perform(get("/api/trainee/all")
            .session(session))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(containsString(username)));
  }

  @Test
  public void testActivationOfTrainee() throws Exception {
    mockMvc.perform(patch("/api/trainee/activate")
            .session(session)
            .param("username", username)
            .param("isActive", "true"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Trainee activated")));

    // Verify Trainee is active
    mockMvc.perform(get("/api/trainee/profile")
            .session(session)
            .param("username", username))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.active", is(true)));
  }

  @Test
  public void testDeActivationOfTrainee() throws Exception {
    mockMvc.perform(patch("/api/trainee/de-activate")
            .session(session)
            .param("username", username)
            .param("isActive", "false"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Trainee deactivated")));

    mockMvc.perform(get("/api/trainee/profile")
            .session(session)
            .param("username", username))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.active", is(false)));
  }

  @Test
  public void testDeletionOfTrainee() throws Exception {
    mockMvc.perform(delete("/api/trainee/delete")
            .session(session)
            .param("username", username))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Trainee deleted")));
  }

  @Test
  public void testRetrieveTraineeByUserName() throws Exception {
    mockMvc.perform(get("/api/trainee/profile")
            .session(session)
            .param("username", username))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.firstName", equalTo("John")))
        .andExpect(jsonPath("$.lastName", equalTo("Doe")))
        .andExpect(jsonPath("$.address", equalTo("Tbilisi Tamarashvili Street")));
  }
}
