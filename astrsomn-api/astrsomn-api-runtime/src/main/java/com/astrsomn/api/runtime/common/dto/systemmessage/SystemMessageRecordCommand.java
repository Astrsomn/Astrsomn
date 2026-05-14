package com.astrsomn.api.runtime.common.dto.systemmessage;

import lombok.Data;


@Data
public class SystemMessageRecordCommand {

    private String messageType;
    private String messageLevel;
    private String readStatus;
    private String title;
    private String content;
    private String refType;
    private Long refId;
    private String refKey;
    private String source;
    private String errorCode;


    private String envCode;
}