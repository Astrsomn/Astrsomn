package com.astrsomn.api.runtime.common.dto.account;

import lombok.Data;
import com.astrsomn.api.runtime.common.entity.AiAccountEntity;

import java.io.Serializable;

@Data
public class AiAccountQueryRequestDTO extends AiAccountEntity implements Serializable {

    private static final long serialVersionUID = 1L;
}