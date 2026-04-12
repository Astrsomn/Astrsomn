package org.astrsomn.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.astrsomn.core.common.dto.vecstore.AiVecStoreQueryRequestDTO;
import org.astrsomn.core.common.dto.vecstore.AiVecStoreResponseDTO;
import org.astrsomn.core.common.entity.AiVecStoreEntity;

@Mapper
public interface AiVecStoreMapper extends BaseMapper<AiVecStoreEntity> {

    IPage<AiVecStoreResponseDTO> queryPage(IPage<AiVecStoreResponseDTO> page, @Param("req") AiVecStoreQueryRequestDTO param);

    AiVecStoreResponseDTO selectDetailDtoById(@Param("id") Long id);
}
