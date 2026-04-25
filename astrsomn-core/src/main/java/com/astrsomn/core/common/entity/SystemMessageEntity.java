package com.astrsomn.core.common.entity;

import com.astrsomn.commn.base.BaseEntity;
import com.astrsomn.core.common.constant.SystemMessageEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统消息/站内通知：插件安装、上线通知、调用失败等均落本表，类型与级别见
 * {@link com.astrsomn.core.common.constant.SystemMessageEnum}。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("SYS_MESSAGE")
public class SystemMessageEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 消息业务类型，存 {@link SystemMessageEnum.MessageTypeEnum#code}
     */
    @TableField("MESSAGE_TYPE")
    private String messageType;

    /**
     * 展示级别，存 {@link SystemMessageEnum.MessageLevelEnum#code}
     */
    @TableField("MESSAGE_LEVEL")
    private String messageLevel;

    /**
     * 已读/未读，存 {@link SystemMessageEnum.ReadStatusEnum#code}
     */
    @TableField("READ_STATUS")
    private String readStatus;

    /**
     * 标题/摘要（列表展示）
     */
    @TableField("TITLE")
    private String title;

    /**
     * 正文：纯文本、JSON（扩展字段、请求/响应片段、堆栈等）
     */
    @TableField("CONTENT")
    private String content;

    /**
     * 关联业务类型，存 {@link SystemMessageEnum.RefTypeEnum#code}
     */
    @TableField("REF_TYPE")
    private String refType;

    /**
     * 关联主键（如 AI_INSTANCE.ID、SYS_EXTENSION.ID 等，按 REF_TYPE 解释）
     */
    @TableField("REF_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long refId;

    /**
     * 业务唯一键（如 extensionKey、traceId 等，便于无 REF_ID 或跨表关联）
     */
    @TableField("REF_KEY")
    private String refKey;

    /**
     * 来源：插件 artifact/模块名、服务名、SYSTEM 等
     */
    @TableField("SOURCE")
    private String source;

    /**
     * 错误码/第三方状态码（调用失败等场景）
     */
    @TableField("ERROR_CODE")
    private String errorCode;
}
