package org.astrsomn.core.common.dto.vecdoc;

import lombok.Data;
import org.astrsomn.core.common.entity.AiVecDocEntity;

import java.io.Serializable;

@Data
public class AiVecDocQueryRequestDTO extends AiVecDocEntity implements Serializable {

    private Long collectionId;
    private String docIdInStore;
    private String syncStatus;
}
