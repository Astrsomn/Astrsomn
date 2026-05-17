package com.astrsomn.server.mapper;

import com.astrsomn.api.runtime.common.dto.instance.AiInstanceQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.instance.AiInstanceResponseDTO;
import com.astrsomn.api.runtime.common.entity.AiInstanceEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiInstanceMapper extends BaseMapper<AiInstanceEntity> {

    IPage<AiInstanceResponseDTO> queryPage(IPage<AiInstanceResponseDTO> page,
                                           @Param("req") AiInstanceQueryRequestDTO param);

    AiInstanceResponseDTO selectDetailDtoById(@Param("id") Long id);

    List<AiInstanceResponseDTO> selectByBizKeys(@Param("bizKeys") List<String> bizKeys);
}
