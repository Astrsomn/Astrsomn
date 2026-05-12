package com.astrsomn.starter.runtime.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.api.runtime.common.dto.account.AiAccountQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.account.AiAccountResponseDTO;
import com.astrsomn.api.runtime.common.entity.AiAccountEntity;

@Mapper
public interface AstAiAccountMapper extends BaseMapper<AiAccountEntity> {


}
