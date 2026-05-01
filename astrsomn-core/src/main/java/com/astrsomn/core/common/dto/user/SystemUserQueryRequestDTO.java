package com.astrsomn.core.common.dto.user;

import lombok.Data;
import com.astrsomn.core.common.entity.SystemUserEntity;

import java.io.Serializable;

@Data
public class SystemUserQueryRequestDTO extends SystemUserEntity implements Serializable {

    private String username;
    private String adminFlag;
    private String email;

    /**
     * 按业务系统状态筛选：ONLINE | OFFLINE | MAINTENANCE（需在 Mapper/表字段就绪后生效；未接库前可忽略）
     */
    private String systemStatus;

}
