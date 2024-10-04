package com.demo.folder.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

  @Autowired
  private MockMvc mockMvc;

  private MockHttpSession session;

  @BeforeEach
  public void setup() throws Exception {
    this.session = new MockHttpSession();
    String registrationJson = """
        {
            "firstName": "John",
            "lastName": "Doe",
            "dateOfBirth": "1990-01-01",
            "address": "123 Main Street"
        }
        """;

    MvcResult registrationResult = mockMvc.perform(post("/api/trainees")
            .contentType(MediaType.APPLICATION_JSON)
            .content(registrationJson))
        .andExpect(status().isCreated())
        .andReturn();

    String registrationResponse = registrationResult.getResponse().getContentAsString();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(registrationResponse);
    String username = jsonNode.get("username").asText();
    String password = jsonNode.get("password").asText();

    MvcResult loginResult = mockMvc.perform(post("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .param("username", username)
            .param("password", password))
        .andExpect(status().isOk())
        .andExpect(content().string("Login successful"))
        .andReturn();

    session = (MockHttpSession) loginResult.getRequest().getSession(false);
  }

  @Test
  public void testGetAllTrainingTypes() throws Exception {
    String trainingTypeJson = "{ \"trainingTypeName\": \"Yoga\" }";

    mockMvc.perform(post("/api/training-type")
            .session(session)
            .contentType(MediaType.APPLICATION_JSON)
            .content(trainingTypeJson))
        .andExpect(status().isOk());

    mockMvc.perform(get("/api/training-type")
            .session(session))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
            "$[0].trainingTypeName", is("Yoga")));
  }

  @Test
  public void testGetTrainingTypeById() throws Exception {
    String trainingTypeJson = "{ \"trainingTypeName\": \"Yoga\" }";

    mockMvc.perform(post("/api/training-type")
            .session(session)
            .contentType(MediaType.APPLICATION_JSON)
            .content(trainingTypeJson))
        .andExpect(status().isOk());

    MvcResult getAllResult = mockMvc.perform(get("/api/training-type")
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
        .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
            "$.trainingTypeName", is("Yoga")))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id",
                is(id.intValue())));
  }
}
