package com.astrsomn.api.vector.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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