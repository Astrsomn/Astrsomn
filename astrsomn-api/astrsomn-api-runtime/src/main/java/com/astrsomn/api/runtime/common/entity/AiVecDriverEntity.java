package com.astrsomn.api.runtime.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 仅作为 VecDriver SPI 元数据载体保留，不再映射数据库表。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiVecDriverEntity {

    private Long id;

    private String driverName;

    private String provider;

    private String params;

    private String status;
}
