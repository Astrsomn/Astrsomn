package com.astrsomn.server.mapper;

import com.astrsomn.api.vector.dto.vecfolder.AiVecFolderQueryRequestDTO;
import com.astrsomn.api.vector.dto.vecfolder.AiVecFolderResponseDTO;
import com.astrsomn.api.vector.entity.AiVecFolderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiVecFolderMapper extends BaseMapper<AiVecFolderEntity> {

    List<AiVecFolderResponseDTO> queryList(@Param("req") AiVecFolderQueryRequestDTO param);
}
