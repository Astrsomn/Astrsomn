package com.astrsomn.server.mapper;

import com.astrsomn.api.runtime.common.dto.account.AiAccountQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.account.AiAccountResponseDTO;
import com.astrsomn.api.runtime.common.entity.AiAccountEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AiAccountMapper extends BaseMapper<AiAccountEntity> {

    IPage<AiAccountResponseDTO> queryPage(IPage<AiAccountResponseDTO> page,
                                          @Param("req") AiAccountQueryRequestDTO param);
}
