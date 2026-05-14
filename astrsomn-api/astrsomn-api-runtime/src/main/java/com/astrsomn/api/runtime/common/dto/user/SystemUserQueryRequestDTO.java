package com.astrsomn.api.runtime.common.dto.user;

import lombok.Data;
import com.astrsomn.api.runtime.common.entity.SystemUserEntity;

import java.io.Serializable;

@Data
public class SystemUserQueryRequestDTO extends SystemUserEntity implements Serializable {

    private String username;
    private String adminFlag;
    private String email;

    
    private String systemStatus;

}