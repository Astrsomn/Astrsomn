package com.astrsomn.api.runtime.common.entity;

import com.astrsomn.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("AI_INSTANCE")
public class AiInstanceEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField("INSTANCE_KEY")
    private String instanceKey;

    @TableField("AGENT_KEY")
    private String agentKey;

    @TableField("INSTANCE_NAME")
    private String instanceName;

    @TableField("MODEL_KEY")
    private String modelKey;


    @TableField(exist = false)
    private String modelType;

    @TableField("MAX_TOKENS")
    private Integer maxTokens;


    @TableField("TEMPERATURE")
    private Double temperature;


    @TableField("PRESENCE_PENALTY")
    private Double presencePenalty;


    @TableField("FREQUENCY_PENALTY")
    private Double frequencyPenalty;


    @TableField("STOP_SEQUENCES")
    private String stopSequences;


    @TableField("SEED")
    private Integer seed;


    @TableField("TOP_P")
    private Double topP;


    @TableField("TOP_K")
    private Integer topK;


    @TableField("STYLE")
    private String style;


    @TableField("SIZE")
    private String size;


    @TableField("DIMENSIONS")
    private Integer dimensions;


    @TableField("STATUS")
    private String status;


    @TableField("IS_DEFAULT")
    private String isDefault;


    @TableField("ACCOUNT_KEY")
    private String accountKey;


    @TableField(value = "MODEL_ROUTE_JSON", exist = false)
    private String modelRouteJson;

    @TableField("ROUTE_STRATEGY")
    private String routeStrategy;

    @TableField("ROUTE_WEIGHT")
    private Integer routeWeight;

    @TableField("RESILIENCE_ENABLED")
    private String resilienceEnabled;

    @TableField("RESILIENCE_INSTANCE_NAME")
    private String resilienceInstanceName;

    @TableField("CIRCUIT_BREAKER_ENABLED")
    private String circuitBreakerEnabled;

    @TableField("CIRCUIT_BREAKER_FAILURE_RATE_THRESHOLD")
    private Integer circuitBreakerFailureRateThreshold;

    @TableField("CIRCUIT_BREAKER_WAIT_DURATION")
    private Long circuitBreakerWaitDuration;

    @TableField("RETRY_ENABLED")
    private String retryEnabled;

    @TableField("RETRY_MAX_ATTEMPTS")
    private Integer retryMaxAttempts;

    @TableField("RETRY_WAIT_DURATION")
    private Long retryWaitDuration;

    @TableField("TIMEOUT_ENABLED")
    private String timeoutEnabled;

    @TableField("TIMEOUT_DURATION")
    private Long timeoutDuration;

    @TableField("FALLBACK_ENABLED")
    private String fallbackEnabled;

    @TableField("FALLBACK_INSTANCE_KEY")
    private String fallbackInstanceKey;

}