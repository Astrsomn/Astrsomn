package com.astrsomn.starter.mapper;

import com.astrsomn.core.common.dto.chat.session.AiChatSessionQueryRequestDTO;
import com.astrsomn.core.common.dto.chat.session.AiChatSessionResponseDTO;
import com.astrsomn.core.common.entity.AiChatSessionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AiChatSessionMapper extends BaseMapper<AiChatSessionEntity> {

    IPage<AiChatSessionResponseDTO> queryPage(
            IPage<AiChatSessionResponseDTO> page, @Param("req") AiChatSessionQueryRequestDTO param);
}
