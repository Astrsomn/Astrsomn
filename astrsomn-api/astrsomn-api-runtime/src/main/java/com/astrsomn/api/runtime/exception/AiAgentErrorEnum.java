package com.astrsomn.api.runtime.exception;

import com.astrsomn.common.base.IError;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum AiAgentErrorEnum implements IError {

    AGENT_NOT_FOUND(20001, "Agent not found"),


    AGENT_CREATE_FAILED(20002, "Agent creation failed"),


    AGENT_UPDATE_FAILED(20003, "Agent update failed"),


    AGENT_DELETE_FAILED(20004, "Agent deletion failed"),


    AGENT_PARAM_ERROR(20005, "Agent parameter error"),


    AGENT_PERMISSION_DENIED(20006, "Agent permission denied");

    private final int code;
    private final String message;
}