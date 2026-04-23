package com.astrsomn.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.core.common.dto.conversation.AiConversationQueryRequestDTO;
import com.astrsomn.core.common.dto.conversation.AiConversationResponseDTO;
import com.astrsomn.core.common.dto.conversation.AiConversationUsageDTO;
import com.astrsomn.core.common.entity.AiConversationEntity;

import java.util.List;

@Mapper
public interface AiConversationMapper extends BaseMapper<AiConversationEntity> {

    IPage<AiConversationResponseDTO> queryPage(IPage<AiConversationResponseDTO> page, @Param("req") AiConversationQueryRequestDTO param);

    int getMaxMessageOrder(@Param("messageKey") String messageKey);

    List<AiConversationUsageDTO> selectTodayUsage(@Param("envCode") String envCode);

    IPage<AiConversationResponseDTO> queryGroups(IPage<AiConversationResponseDTO> page, @Param("req") AiConversationQueryRequestDTO param);

    List<AiConversationResponseDTO> recoverByMemoryKey(@Param("memoryKey") String memoryKey);
}
