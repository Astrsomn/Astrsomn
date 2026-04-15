package org.astrsomn.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.astrsomn.core.common.dto.model.AiModelQueryRequestDTO;
import org.astrsomn.core.common.dto.model.AiModelResponseDTO;
import org.astrsomn.core.common.entity.AiModelEntity;

@Mapper
public interface AiModelMapper extends BaseMapper<AiModelEntity> {

    IPage<AiModelResponseDTO> queryPage(IPage<AiModelResponseDTO> page,
                                        @Param("req") AiModelQueryRequestDTO param);

    AiModelResponseDTO selectModelWithReferenceStatus(@Param("id") Long id);
}
