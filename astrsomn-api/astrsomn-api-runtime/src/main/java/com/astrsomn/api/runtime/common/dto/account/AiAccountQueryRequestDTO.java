package com.astrsomn.api.runtime.common.dto.account;

import com.astrsomn.api.runtime.common.entity.AiAccountEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class AiAccountQueryRequestDTO extends AiAccountEntity implements Serializable {

    private static final long serialVersionUID = 1L;
}