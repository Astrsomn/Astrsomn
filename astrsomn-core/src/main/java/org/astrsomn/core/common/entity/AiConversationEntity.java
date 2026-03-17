package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.astrsomn.core.common.base.BaseEntity;


@Data
@EqualsAndHashCode(callSuper = false)
@TableName("AI_CONVERSATION")
public class AiConversationEntity extends BaseEntity<Long> {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * UUID 前端生成
     */
    @TableField("memory_id")
    private String memoryId;

    /**
     * ENUM('user', 'assistant') 消息发送者角色
     */
    private String role;

    /**
     * 消息内容（用户输入或模型回复）
     */
    private String content;

    /**
     * 在当前 messageId 中的消息序号（从 0 开始）
     */
    @TableField("message_order")
    private Integer messageOrder;

    /**
     * 消耗token数
     */
    @TableField("consume_tokens")
    private Integer consumeTokens;

    /**
     * 模型ID
     */
    @TableField("model_id")
    private Long modelId;

    /**
     * 关联的智能体/提示词ID
     */
    @TableField("prompt_id")
    private Long promptId;
}
