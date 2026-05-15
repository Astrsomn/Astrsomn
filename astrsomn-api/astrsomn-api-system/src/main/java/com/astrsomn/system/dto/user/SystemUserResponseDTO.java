package com.astrsomn.system.dto.user;

import com.astrsomn.api.runtime.common.entity.SystemUserEntity;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class SystemUserResponseDTO extends SystemUserEntity {


    private String systemDisplayName;


    private String systemStatus;


    private Long todayApiCalls;


    private BigDecimal errorRate;


    private String lastAccessTime;
}