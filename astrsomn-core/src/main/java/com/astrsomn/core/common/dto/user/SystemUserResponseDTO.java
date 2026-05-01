package com.astrsomn.core.common.dto.user;

import lombok.Data;
import com.astrsomn.core.common.entity.SystemUserEntity;

import java.math.BigDecimal;

/**
 * 列表/详情返回：在 {@link SystemUserEntity} 基础上补充「业务系统」大屏展示字段（与表字段解耦，可后续由 Service/统计任务填充）。
 */
@Data
public class SystemUserResponseDTO extends SystemUserEntity {

    /**
     * 业务系统展示名（缺省可由前端回退为 username）
     */
    private String systemDisplayName;

    /**
     * 业务系统运行状态：ONLINE | OFFLINE | MAINTENANCE（可与逻辑删除、监控状态对齐，具体由业务层赋值）
     */
    private String systemStatus;

    /**
     * 今日 API 调用量等业务指标（占位，后续对接统计）
     */
    private Long todayApiCalls;

    /**
     * 错误率，0–100 或与前端约定的小数（占位）
     */
    private BigDecimal errorRate;

    /**
     * 最后访问时间展示文案或 ISO 时间（占位，可与审计日志对齐）
     */
    private String lastAccessTime;
}
