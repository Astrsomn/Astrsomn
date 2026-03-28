package org.astrsomn.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.astrsomn.core.common.dto.sensitiveword.AiSensitiveWordQueryRequestDTO;
import org.astrsomn.core.common.dto.sensitiveword.AiSensitiveWordResponseDTO;
import org.astrsomn.core.common.entity.AiSensitiveWordEntity;

@Mapper
public interface AiSensitiveWordMapper extends BaseMapper<AiSensitiveWordEntity> {

    IPage<AiSensitiveWordResponseDTO> queryPage(IPage<AiSensitiveWordResponseDTO> page,
                                                @Param("req") AiSensitiveWordQueryRequestDTO param);
}
