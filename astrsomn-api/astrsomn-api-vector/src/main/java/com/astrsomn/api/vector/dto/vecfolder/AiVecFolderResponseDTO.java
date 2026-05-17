package com.astrsomn.api.vector.dto.vecfolder;

import com.astrsomn.api.vector.entity.AiVecFolderEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AiVecFolderResponseDTO extends AiVecFolderEntity {

    /** 子文件夹数量 */
    private Long childCount;

    /** 关联文档数量 */
    private Long docCount;
}
