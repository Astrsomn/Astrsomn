package com.astrsomn.system.dto.user;

import com.astrsomn.system.entity.SystemUserEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class SystemUserQueryRequestDTO extends SystemUserEntity implements Serializable {

    private String username;
    private String adminFlag;
    private String email;


    private String systemStatus;

}