package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.astrsomn.core.common.base.BaseEntity;

/**
 * AI模型配置实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("AI_MODEL_CONFIG")
public class AiModelEntity extends BaseEntity<Long> {
    /**
     * 模型ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 模型名称
     */
    private String modelName;
    
    /**
     * 模型类型
     */
    private String modelType;

    /**
     * 供应商
     */
    private String provider;
    
    /**
     * APIKey
     */
    private String apiKey;

    /**
     * API密钥
     */
    private String apiSecret;

    /**
     * API地址
     */
    private String apiUrl;
    /**
     * 模型参数
     */
    private String modelParams;
    
    /**
     * 状态（启用-禁用）
     */
    private String status;

    /**
     * 响应限制 0 - 8192
     */
    private Integer responseLimit;

    /**
     * 随机性
     */
    private Integer randomIndex;


    /**
     * 顶层概率
     */
    private Integer topVariance;
    
    /**
     * 是否默认模型
     */
    private Integer isDefault;
    
    /**
     * 能力分类（JSON格式存储）
     */
    private String capabilities;
}