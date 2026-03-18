package org.astrsomn.core.common.langchain.buildParam;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 模型推理参数配置
 */
@Data
@Accessors(chain = true)
public class ModelInferenceConfig {

    /**
     * 温度 (0.0 - 2.0)
     */
    private Double temperature;

    /**
     * 核采样概率 (0.0 - 1.0)
     */
    private Double topP;

    /**
     * 最大生成 Token 数
     */
    private Integer maxTokens;

    /**
     * 随机种子 (用于复现结果)
     */
    private Integer seed;

    /**
     * 存在惩罚 (降低重复话题)
     */
    private Double presencePenalty;

    /**
     * 频率惩罚 (降低重复词汇)
     */
    private Double frequencyPenalty;

}
