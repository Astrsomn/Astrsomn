package org.astrsomn.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.astrsomn.core.common.dto.instance.AiInstanceQueryRequestDTO;
import org.astrsomn.core.common.dto.instance.AiInstanceResponseDTO;
import org.astrsomn.core.common.entity.AiInstanceEntity;

@Mapper
public interface AiInstanceMapper extends BaseMapper<AiInstanceEntity> {

    IPage<AiInstanceResponseDTO> queryPage(IPage<AiInstanceResponseDTO> page,
                                           @Param("req") AiInstanceQueryRequestDTO param);

    AiInstanceResponseDTO selectDetailDtoById(@Param("id") Long id);
}
