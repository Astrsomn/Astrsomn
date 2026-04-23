package com.astrsomn.core.common.langchain.buildParam.setting;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 模型推理参数配置
 */
@Data
@Accessors(chain = true)
public class ChatSetting {

    /**
     * 温度 (0.0 - 2.0)
     */
    private Double temperature;

    /**
     * 核采样概率 (0.0 - 1.0)
     * 不固定候选词的数量，而是按概率从高到低排序，累加概率，直到累积和达到阈值 P (0 < P ≤ 1)。只在这个动态集合中进行采样。
     */
    private Double topP;

    /**
     * Top-K 采样
     * 在采样之前，只保留概率最高的 K 个词，将其他所有词的概率设为 0，然后重新归一化剩下的概率分布进行采样。
     */
    private Integer topK;
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
