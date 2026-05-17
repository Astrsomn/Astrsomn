package com.astrsomn.server.mapper;

import com.astrsomn.api.runtime.common.dto.chat.session.AiChatSessionQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.chat.session.AiChatSessionResponseDTO;
import com.astrsomn.api.runtime.common.entity.AiChatSessionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AiChatSessionMapper extends BaseMapper<AiChatSessionEntity> {

    IPage<AiChatSessionResponseDTO> queryPage(
            IPage<AiChatSessionResponseDTO> page, @Param("req") AiChatSessionQueryRequestDTO param);
}
