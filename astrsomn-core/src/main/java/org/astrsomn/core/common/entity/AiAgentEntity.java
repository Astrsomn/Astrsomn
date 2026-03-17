package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.astrsomn.core.common.base.BaseEntity;


@Data
@TableName("AI_AGENT")
public class AiAgentEntity extends BaseEntity<Long> {

    /**
     * Agent名称
     */
    private String name;

    /**
     * Agent描述
     */
    private String description;

    /**
     * 默认模型
     */
    private Long modelId;

    /**
     * 提示词ID
     */
    private String promptUuid;

    /**
     * 运行参数
     *  { "temperature": 0.7, "max_tokens": 2048, ... }
     */
    private String configParams;

    /**
     * 状态（启用/禁用）
     */
    private String status;


    /**
     * 关联知识库列表
     */
    private String knowledgeBaseIds;

    /**
     * 支持工具列表
     */
    private String toolIds;

    /**
     * MCP列表
     */
    private String mcpIds;

    /**
     * 记忆模式
     */
    private String memoryMode;

    /**
     * 记忆轮数
     */
    private String memoryWindowSize;

    /**
     * 最大Token数
     */
    private Integer maxToken;

    /**
     * 温度值
     */
    private Double temperature;

    /**
     * 存在惩罚
     */
    private Double presencePenalty;

    /**
     * 频率惩罚
     */
    private Double frequencyPenalty;

    /**
     * 停止序列
     */
    private String stopSequences;

    /**
     * 随机种子
     */
    private Integer seed;

    /**
     * 流式输出
     */
    private boolean enableStream;

    /**
     * 全类名
     */
    private String interfaceClass;
}
