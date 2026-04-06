package org.astrsomn.core.common.dto.vecstore;

import lombok.Data;
import org.astrsomn.core.common.entity.AiVecStoreEntity;

import java.io.Serializable;

@Data
public class AiVecStoreQueryRequestDTO extends AiVecStoreEntity implements Serializable {

    private Long sourceId;
    private String collectionName;
    private String modelKey;
}
