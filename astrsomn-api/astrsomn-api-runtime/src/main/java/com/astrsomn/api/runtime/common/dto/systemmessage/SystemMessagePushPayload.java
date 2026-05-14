package com.astrsomn.api.runtime.common.dto.systemmessage;

import com.astrsomn.api.runtime.common.entity.SystemMessageEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
public class SystemMessagePushPayload implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String messageType;
    private String messageLevel;
    private String readStatus;
    private String title;
    private String content;
    private String refType;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long refId;

    private String refKey;
    private String source;
    private String errorCode;
    private String envCode;

    private LocalDateTime createTime;

    public static SystemMessagePushPayload fromEntity(SystemMessageEntity e) {
        if (e == null) {
            return null;
        }
        SystemMessagePushPayload p = new SystemMessagePushPayload();
        p.setId(e.getId());
        p.setMessageType(e.getMessageType());
        p.setMessageLevel(e.getMessageLevel());
        p.setReadStatus(e.getReadStatus());
        p.setTitle(e.getTitle());
        p.setContent(e.getContent());
        p.setRefType(e.getRefType());
        p.setRefId(e.getRefId());
        p.setRefKey(e.getRefKey());
        p.setSource(e.getSource());
        p.setErrorCode(e.getErrorCode());
        p.setEnvCode(e.getEnvCode());
        p.setCreateTime(e.getCreateTime());
        return p;
    }
}