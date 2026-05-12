package com.astrsomn.starter.runtime.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.api.runtime.common.dto.vecstore.AiVecStoreQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.vecstore.AiVecStoreResponseDTO;
import com.astrsomn.api.runtime.common.dto.vecstore.AiVecStoreStatsResponseDTO;
import com.astrsomn.api.runtime.common.entity.AiVecStoreEntity;

@Mapper
public interface AstAiVecStoreMapper extends BaseMapper<AiVecStoreEntity> {

}
