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
class TrainingControllerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(TrainingControllerTest.class);

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext wac;

  private String traineeUsername;
  private String traineePassword;
  private String trainerUsername;
  private String trainerPassword;
  private MockHttpSession session;

  @BeforeEach
  public void setup() throws Exception {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    ObjectMapper objectMapper = new ObjectMapper();

    String trainingTypeJson = "{ \"trainingTypeName\": \"Yoga\" }";

    MvcResult trainingTypeResult = mockMvc.perform(post("/api/training-type/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(trainingTypeJson))
        .andExpect(status().isOk())
        .andExpect(content().string("Training type created successfully."))
        .andReturn();

    String traineeJson = """
        {
            "firstName": "TraineeFirstName",
            "lastName": "TraineeLastName",
            "dateOfBirth": "1995-05-15",
            "address": "123 Trainee Street"
        }
        """;

    MvcResult traineeRegistrationResult = mockMvc.perform(post("/api/trainee/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(traineeJson))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    String traineeRegistrationResponse = traineeRegistrationResult.getResponse()
        .getContentAsString();
    JsonNode traineeJsonNode = objectMapper.readTree(traineeRegistrationResponse);
    traineeUsername = traineeJsonNode.get("username").asText();
    traineePassword = traineeJsonNode.get("password").asText();

    LOGGER.info("Registered trainee with username: {}", traineeUsername);
    LOGGER.info("Registered trainee with password: {}", traineePassword);

    String trainerJson = "{"
        + "\"firstName\": \"TrainerFirstName\","
        + "\"lastName\": \"TrainerLastName\","
        + "\"trainingTypeId\": " + 1
        + "}";

    MvcResult trainerRegistrationResult = mockMvc.perform(post("/api/trainer/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(trainerJson))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    String trainerRegistrationResponse = trainerRegistrationResult.getResponse()
        .getContentAsString();
    JsonNode trainerJsonNode = objectMapper.readTree(trainerRegistrationResponse);
    trainerUsername = trainerJsonNode.get("username").asText();
    trainerPassword = trainerJsonNode.get("password").asText();

    LOGGER.info("Registered trainer with username: {}", trainerUsername);
    LOGGER.info("Registered trainer with password: {}", trainerPassword);

    MvcResult loginResult = mockMvc.perform(get("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\": \"" + traineeUsername + "\", \"password\": \"" + traineePassword
                + "\"}"))
        .andExpect(status().isOk())
        .andExpect(content().string("Login successful"))
        .andReturn();

    session = (MockHttpSession) loginResult.getRequest().getSession(false);
  }

  @Test
  public void testRegisterTraining() throws Exception {
    String trainingDateStr = "2024-01-01T10:00:00.000Z";
    String trainingJson = "{"
        + "\"traineeUserName\": \"" + traineeUsername + "\","
        + "\"trainerUserName\": \"" + trainerUsername + "\","
        + "\"trainingName\": \"Morning Yoga Session\","
        + "\"trainingDate\": \"" + trainingDateStr + "\","
        + "\"duration\": 60"
        + "}";

    mockMvc.perform(post("/api/training/create")
            .session(session)
            .contentType(MediaType.APPLICATION_JSON)
            .content(trainingJson))
        .andExpect(status().isOk())
        .andExpect(content().string("Training created successfully"));
  }

  @Test
  public void testGetTrainings() throws Exception {
    String trainingDateStr = "2024-01-01T10:00:00.000Z";
    String trainingJson = "{"
        + "\"traineeUserName\": \"" + traineeUsername + "\","
        + "\"trainerUserName\": \"" + trainerUsername + "\","
        + "\"trainingName\": \"Morning Yoga Session\","
        + "\"trainingDate\": \"" + trainingDateStr + "\","
        + "\"duration\": 60"
        + "}";

    mockMvc.perform(post("/api/training/create")
            .session(session)
            .contentType(MediaType.APPLICATION_JSON)
            .content(trainingJson))
        .andExpect(status().isOk());

    mockMvc.perform(get("/api/training/all")
            .session(session))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].trainingName", is("Morning Yoga Session")))
        .andExpect(jsonPath("$[0].traineeUserName", is(traineeUsername)))
        .andExpect(jsonPath("$[0].trainerUserName", is(trainerUsername)));
  }
}
