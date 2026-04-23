package com.astrsomn.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.core.common.dto.account.AiAccountQueryRequestDTO;
import com.astrsomn.core.common.dto.account.AiAccountResponseDTO;
import com.astrsomn.core.common.entity.AiAccountEntity;

@Mapper
public interface AiAccountMapper extends BaseMapper<AiAccountEntity> {

    IPage<AiAccountResponseDTO> queryPage(IPage<AiAccountResponseDTO> page,
                                          @Param("req") AiAccountQueryRequestDTO param);
}
