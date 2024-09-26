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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SpringConfig.class, WebConfig.class, SecurityConfig.class})
class TrainerControllerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(TrainerControllerTest.class);

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext wac;

  private String trainerUsername;
  private String trainerPassword;
  private MockHttpSession session;

  @BeforeEach
  public void setup() throws Exception {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    ObjectMapper objectMapper = new ObjectMapper();

    String trainingTypeJson = "{ \"trainingTypeName\": \"Yoga\" }";

    mockMvc.perform(post("/api/training-type/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(trainingTypeJson))
        .andExpect(status().isOk())
        .andExpect(content().string("Training type created successfully."));


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

    String trainerRegistrationResponse = trainerRegistrationResult.getResponse().getContentAsString();
    JsonNode trainerJsonNode = objectMapper.readTree(trainerRegistrationResponse);
    trainerUsername = trainerJsonNode.get("username").asText();
    trainerPassword = trainerJsonNode.get("password").asText();

    LOGGER.info("Registered trainer with username: {}", trainerUsername);
    LOGGER.info("Registered trainer with password: {}", trainerPassword);

    MvcResult loginResult = mockMvc.perform(get("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\": \"" + trainerUsername + "\", \"password\": \"" + trainerPassword + "\"}"))
        .andExpect(status().isOk())
        .andExpect(content().string("Login successful"))
        .andReturn();

    session = (MockHttpSession) loginResult.getRequest().getSession(false);
  }

  @Test
  public void testRegisterTrainer() throws Exception {
    String trainerJson = "{"
        + "\"firstName\": \"AnotherTrainer\","
        + "\"lastName\": \"LastName\","
        + "\"trainingTypeId\": " + 1
        + "}";

    mockMvc.perform(post("/api/trainer/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(trainerJson))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.username", notNullValue()))
        .andExpect(jsonPath("$.password", notNullValue()));
  }

  @Test
  public void testGetAllTrainers() throws Exception {
    mockMvc.perform(get("/api/trainer/all")
            .session(session))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].username", is(trainerUsername)))
        .andExpect(jsonPath("$[0].trainingTypeId", is(1)));
  }

  @Test
  public void testActivationOfTrainer() throws Exception {
    mockMvc.perform(patch("/api/trainer/activate")
            .session(session)
            .param("username", trainerUsername)
            .param("isActive", "true"))
        .andExpect(status().isOk())
        .andExpect(content().string("Trainer activated"));

    mockMvc.perform(get("/api/trainer/trainer-profile")
            .session(session)
            .param("userName", trainerUsername))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active", is(true)));
  }

  @Test
  public void testDeActivationOfTrainer() throws Exception {
    mockMvc.perform(patch("/api/trainer/activate")
            .session(session)
            .param("username", trainerUsername)
            .param("isActive", "true"))
        .andExpect(status().isOk())
        .andExpect(content().string("Trainer activated"));

    mockMvc.perform(patch("/api/trainer/de-activate")
            .session(session)
            .param("username", trainerUsername)
            .param("isActive", "false"))
        .andExpect(status().isOk())
        .andExpect(content().string("Trainer deactivated"));

    mockMvc.perform(get("/api/trainer/trainer-profile")
            .session(session)
            .param("userName", trainerUsername))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active", is(false)));
  }

  @Test
  public void testRetrieveTrainerByUserName() throws Exception {
    mockMvc.perform(get("/api/trainer/trainer-profile")
            .session(session)
            .param("userName", trainerUsername))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName", is("TrainerFirstName")))
        .andExpect(jsonPath("$.lastName", is("TrainerLastName")))
        .andExpect(jsonPath("$.specialization", is("Yoga")))
        .andExpect(jsonPath("$.active", is(false)));
  }
}
