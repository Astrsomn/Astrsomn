package com.astrsomn.server.service.impl;

import com.astrsomn.api.workflow.domain.dto.runtime.AstFlowTestRunRequestDTO;
import com.astrsomn.api.workflow.domain.dto.runtime.AstFlowTestRunResponseDTO;
import com.astrsomn.api.workflow.runtime.spi.AstFlowRuntimeEngine;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.server.service.workflow.AiWorkflowTestRunServiceImpl;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

@Ignore("Test skipped")
public class AiWorkflowTestRunServiceImplTest {

    @Test
    public void shouldReturnFailWhenWorkflowIdentityMissing() {
        AstFlowRuntimeEngine engine = Mockito.mock(AstFlowRuntimeEngine.class);
        AiWorkflowTestRunServiceImpl service = new AiWorkflowTestRunServiceImpl(engine);

        AstFlowTestRunRequestDTO request = new AstFlowTestRunRequestDTO();
        BaseResponse<AstFlowTestRunResponseDTO> response = service.testRun(request);

        Assert.assertFalse(response.isSuccess());
        Assert.assertEquals("id or workflowKey is required", response.getMessage());
    }

    @Test
    public void shouldReturnSuccessWhenEngineReturnsResult() {
        AstFlowRuntimeEngine engine = Mockito.mock(AstFlowRuntimeEngine.class);
        AstFlowTestRunResponseDTO runtimeResponse = new AstFlowTestRunResponseDTO();
        runtimeResponse.setStatus("COMPLETED");
        Mockito.when(engine.testRun(Mockito.any())).thenReturn(runtimeResponse);

        AiWorkflowTestRunServiceImpl service = new AiWorkflowTestRunServiceImpl(engine);

        AstFlowTestRunRequestDTO request = new AstFlowTestRunRequestDTO();
        request.setWorkflowKey("demo");
        BaseResponse<AstFlowTestRunResponseDTO> response = service.testRun(request);

        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals("COMPLETED", response.getData().getStatus());
    }
}
