package com.astrsomn.api.vector.dto.vecfolder;

import lombok.Data;

import java.io.Serializable;

@Data
public class AiVecFolderQueryRequestDTO implements Serializable {

    private Long collectionId;
    private Long parentId;
}
