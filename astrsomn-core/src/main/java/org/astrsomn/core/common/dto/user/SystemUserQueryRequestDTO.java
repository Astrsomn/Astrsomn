package org.astrsomn.core.common.dto.user;

import lombok.Data;
import org.astrsomn.core.common.entity.SystemUserEntity;

import java.io.Serializable;

@Data
public class SystemUserQueryRequestDTO extends SystemUserEntity implements Serializable {

    private String username;
    private String adminFlag;
    private String email;

}
