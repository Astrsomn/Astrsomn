package org.astrsomn.core.common.langchain.buildParam;

import lombok.Data;
import org.astrsomn.core.common.langchain.AstrsomnChatAssistant;

import java.util.List;
@Data
@Deprecated
public class AiChatBuildParam {

    /**
     * 信息
     */
    private String message;

    /**
     * 模型ID
     */
    private Long modelId;

    /**
     * 记忆ID - 前端生成
     */
    private String memoryId;

    /**
     * 前置prompt
     */
    @Deprecated
    private Long promptId;

    /**
     * 提示词UUID
     */
    private String promptUuid;

    /**
     * 系统消息
     */
    private String systemMessage;


    /**
     * 最大记忆数
     */
    private Integer maxMessages = 10;
    /**
     * 开启联网检索
     */
    private boolean enableNetwork;

    /**
     * 开启深度思考
     */
    private boolean enableDeepSeek;


    /**
     * 开启流式输出 / 完整输出
     */
    private boolean enableStream = false;

    /**
     * 温度
     */
    private Double temperature;

    /**
     * 随机性
     */
    public Double getTopP;
    /**
     * 最大Token数
     */
    private Integer maxToken;
    /**
     * 随机种子
     */
    private Integer seed;

    /**
     * 存在惩罚
     */
    private Double presencePenalty;

    /**
     * 频率惩罚
     */
    private Double frequencyPenalty;

    /**
     * 可用工具列表
     */
    private List<Long> toolIdList;
    /**
     * 可用MCP列表
     */
    private List<Long> mcpIdList;

    /**
     * RAG ID
     */
    private List<Long> ragIdList;

    /**
     * 维度
     */
    private Integer vectorSize;
    /**
     * 指定构建类型
     */
    private Class clazz = AstrsomnChatAssistant.class;

}
