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
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SpringConfig.class, WebConfig.class, SecurityConfig.class})
class TrainingTypeControllerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(TrainingTypeControllerTest.class);

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext wac;

  private String username;
  private String password;
  private MockHttpSession session;

  @BeforeEach
  public void setup() throws Exception {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

    String registrationJson = """
        {
            "firstName": "John",
            "lastName": "Doe",
            "dateOfBirth": "1990-01-01",
            "address": "123 Main Street"
        }
        """;

    // Register a trainee to obtain a user session
    MvcResult registrationResult = mockMvc.perform(post("/api/trainee/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(registrationJson))
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

    MvcResult loginResult = mockMvc.perform(get("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}"))
        .andExpect(status().isOk())
        .andExpect(content().string("Login successful"))
        .andReturn();

    session = (MockHttpSession) loginResult.getRequest().getSession(false);
  }

  @Test
  public void testRegisterTrainingType() throws Exception {
    String trainingTypeJson = "{ \"trainingTypeName\": \"Yoga\" }";

    mockMvc.perform(post("/api/training-type/create")
            .session(session)
            .contentType(MediaType.APPLICATION_JSON)
            .content(trainingTypeJson))
        .andExpect(status().isOk())
        .andExpect(content().string("Training type created successfully."));
  }

  @Test
  public void testGetAllTrainingTypes() throws Exception {
    String trainingTypeJson = "{ \"trainingTypeName\": \"Yoga\" }";

    mockMvc.perform(post("/api/training-type/create")
            .session(session)
            .contentType(MediaType.APPLICATION_JSON)
            .content(trainingTypeJson))
        .andExpect(status().isOk());

    mockMvc.perform(get("/api/training-type/all")
            .session(session))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].trainingTypeName", is("Yoga")));
  }

  @Test
  public void testGetTrainingTypeById() throws Exception {
    String trainingTypeJson = "{ \"trainingTypeName\": \"Yoga\" }";

    mockMvc.perform(post("/api/training-type/create")
            .session(session)
            .contentType(MediaType.APPLICATION_JSON)
            .content(trainingTypeJson))
        .andExpect(status().isOk());

    MvcResult getAllResult = mockMvc.perform(get("/api/training-type/all")
            .session(session))
        .andExpect(status().isOk())
        .andReturn();

    String getAllResponse = getAllResult.getResponse().getContentAsString();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode arrayNode = objectMapper.readTree(getAllResponse);
    Long id = arrayNode.get(0).get("id").asLong();

    mockMvc.perform(get("/api/training-type/" + id)
            .session(session))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.trainingTypeName", is("Yoga")))
        .andExpect(jsonPath("$.id", is(id.intValue())));
  }
}
