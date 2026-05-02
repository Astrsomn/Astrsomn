package com.astrsomn.api.runtime.common.dto.systemmessage;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import com.astrsomn.api.runtime.common.entity.SystemMessageEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 推给前端的轻量 DTO，与表结构同字段、便于在 SSE / WebSocket 中直传
 *（持久化与推送解耦，表数据由应用或 starter 中的封装类写入）。
 */
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
