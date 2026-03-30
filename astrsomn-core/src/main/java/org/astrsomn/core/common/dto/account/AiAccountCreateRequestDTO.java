package org.astrsomn.core.common.dto.account;

import lombok.Data;
import org.astrsomn.core.common.entity.AiAccountEntity;

import java.io.Serializable;

@Data
public class AiAccountCreateRequestDTO extends AiAccountEntity implements Serializable {

    private static final long serialVersionUID = 1L;
}
