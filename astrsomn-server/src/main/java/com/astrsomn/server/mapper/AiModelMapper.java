package com.astrsomn.server.mapper;

import com.astrsomn.api.runtime.common.dto.model.AiModelQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.model.AiModelResponseDTO;
import com.astrsomn.api.runtime.common.entity.AiModelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface AiModelMapper extends BaseMapper<AiModelEntity> {

    IPage<AiModelResponseDTO> queryPage(IPage<AiModelResponseDTO> page,
                                        @Param("req") AiModelQueryRequestDTO param);

    AiModelResponseDTO selectModelWithReferenceStatus(@Param("id") Long id);

    /**
     * Batch query existing model keys for a given extensionCode + envCode.
     * Returns the set of MODEL_KEY values that already exist (not deleted).
     */
    Set<String> selectExistingModelKeys(@Param("extensionCode") String extensionCode,
                                        @Param("envCode") String envCode);
}
