package com.manticore.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тестирует REST-контроллер для пайплайнов
 *
 * @author Linempy
 * @since 24.07.2026
 */
@DisplayName("Тестирование PipelineController")
@SpringBootTest
@AutoConfigureMockMvc
class PipelineControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullPipelineFlow_worksThroughRestApi() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/pipelines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"demo"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("demo")))
                .andReturn();

        JsonNode pipeline = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String pipelineId = pipeline.get("id").asText();

        mockMvc.perform(post("/pipelines/{pipelineId}/nodes", pipelineId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nodeId":"input","name":"Input"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/pipelines/{pipelineId}/nodes", pipelineId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nodeId":"output","name":"Output"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/pipelines/{pipelineId}/edges", pipelineId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"from":"input","to":"output"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dependencies", hasSize(1)));

        mockMvc.perform(get("/pipelines/{pipelineId}", pipelineId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nodes", hasSize(2)))
                .andExpect(jsonPath("$.nodes[0].id", is("input")))
                .andExpect(jsonPath("$.nodes[0].name", is("Input")))
                .andExpect(jsonPath("$.nodes[1].id", is("output")))
                .andExpect(jsonPath("$.nodes[1].name", is("Output")));

        mockMvc.perform(get("/pipelines/{pipelineId}/execution-order", pipelineId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.order[0]", is("input")))
                .andExpect(jsonPath("$.order[1]", is("output")));
    }

    @Test
    void createPipeline_rejectsBlankName() throws Exception {
        mockMvc.perform(post("/pipelines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\" \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("name: must not be blank")));
    }

    @Test
    void getPipeline_returnsNotFoundForUnknownId() throws Exception {
        mockMvc.perform(get("/pipelines/{pipelineId}", "a39a9b84-9b1b-4da4-8908-6c27e8f6ab4f"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }
}
