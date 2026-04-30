package com.astrsomn.core.common.dto.systemmessage;

import lombok.Data;

/**
 * 编程写入 {@link com.astrsomn.core.common.entity.SystemMessageEntity} 时使用的入参
 *（通常由应用模块中的 Recorder 等封装为 insert，业务避免直接调 Mapper）。
 */
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

    /**
     * 可显式指定环境；为空则在插入时由元数据填充（请求上下文或实例配置）解析
     */
    private String envCode;
}
