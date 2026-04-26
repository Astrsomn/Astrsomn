package com.astrsomn.server.service.impl;

import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.workflow.core.domain.dto.runtime.AstFlowTestRunRequestDTO;
import com.astrsomn.workflow.core.domain.dto.runtime.AstFlowTestRunResponseDTO;
import com.astrsomn.workflow.core.runtime.spi.AstFlowRuntimeEngine;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

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
