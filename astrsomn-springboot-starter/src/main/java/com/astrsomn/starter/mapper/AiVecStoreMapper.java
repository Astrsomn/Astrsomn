package com.astrsomn.starter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.core.common.dto.vecstore.AiVecStoreQueryRequestDTO;
import com.astrsomn.core.common.dto.vecstore.AiVecStoreResponseDTO;
import com.astrsomn.core.common.dto.vecstore.AiVecStoreStatsResponseDTO;
import com.astrsomn.core.common.entity.AiVecStoreEntity;

@Mapper
public interface AiVecStoreMapper extends BaseMapper<AiVecStoreEntity> {

    IPage<AiVecStoreResponseDTO> queryPage(IPage<AiVecStoreResponseDTO> page, @Param("req") AiVecStoreQueryRequestDTO param);

    AiVecStoreResponseDTO selectDetailDtoById(@Param("id") Long id);

    AiVecStoreStatsResponseDTO selectStoreStats(@Param("storeId") Long storeId, @Param("envCode") String envCode);
}
