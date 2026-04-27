package com.astrsomn.starter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.core.common.dto.chat.message.AiChatMessageQueryRequestDTO;
import com.astrsomn.core.common.dto.chat.message.AiChatMessageResponseDTO;
import com.astrsomn.core.common.dto.chat.message.AiChatUsageDTO;
import com.astrsomn.core.common.entity.AiChatMessageEntity;

import java.util.List;

@Mapper
public interface AiChatMessageMapper extends BaseMapper<AiChatMessageEntity> {

    IPage<AiChatMessageResponseDTO> queryPage(IPage<AiChatMessageResponseDTO> page, @Param("req") AiChatMessageQueryRequestDTO param);

    int getMaxMessageOrder(@Param("messageKey") String messageKey);

    List<AiChatUsageDTO> selectTodayUsage(@Param("envCode") String envCode);

    IPage<AiChatMessageResponseDTO> queryGroups(IPage<AiChatMessageResponseDTO> page, @Param("req") AiChatMessageQueryRequestDTO param);

    List<AiChatMessageResponseDTO> recoverByMemoryKey(@Param("memoryKey") String memoryKey);
}
