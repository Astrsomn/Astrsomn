package com.astrsomn.api.vector.dto.vecfolder;

import com.astrsomn.api.vector.entity.AiVecFolderEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AiVecFolderResponseDTO extends AiVecFolderEntity {


    private Long childCount;

    
    private Long docCount;
}
