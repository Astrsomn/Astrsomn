package com.astrsomn.server.api;

import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.server.service.AiWorkflowTestRunService;
import com.astrsomn.workflow.core.domain.dto.runtime.AstFlowTestRunRequestDTO;
import com.astrsomn.workflow.core.domain.dto.runtime.AstFlowTestRunResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AiWorkflowTestRunControllerTest {

    @Test
    public void shouldReturnSuccessForTestRunEndpoint() throws Exception {
        AiWorkflowTestRunService service = Mockito.mock(AiWorkflowTestRunService.class);
        AstFlowTestRunResponseDTO data = new AstFlowTestRunResponseDTO();
        data.setStatus("COMPLETED");
        Mockito.when(service.testRun(Mockito.any())).thenReturn(BaseResponse.success(data));

        AiWorkflowTestRunController controller = new AiWorkflowTestRunController(service);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        ObjectMapper objectMapper = new ObjectMapper();

        AstFlowTestRunRequestDTO request = new AstFlowTestRunRequestDTO();
        request.setWorkflowKey("demo");

        MvcResult result = mockMvc.perform(post("/v1/astro/ai-workflow/test-run")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        Assert.assertTrue(json.contains("\"success\":true"));
        Assert.assertTrue(json.contains("\"status\":\"COMPLETED\""));
    }
}
