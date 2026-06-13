package com.astrsomn.api.vector.dto.vecdoc;

import com.astrsomn.api.vector.entity.AiVecDocEntity;
import lombok.Data;

@Data
public class AiVecDocResponseDTO extends AiVecDocEntity {

    /** Whether the original file name was auto-renamed to avoid a duplicate */
    private Boolean renamed;
}
