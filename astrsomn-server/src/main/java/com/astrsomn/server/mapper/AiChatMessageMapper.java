package com.astrsomn.server.mapper;

import com.astrsomn.api.runtime.common.dto.account.AiAccountUsageStatsDTO;
import com.astrsomn.api.runtime.common.dto.chat.message.AiChatMessageQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.chat.message.AiChatMessageResponseDTO;
import com.astrsomn.api.runtime.common.dto.chat.message.AiChatUsageDTO;
import com.astrsomn.api.runtime.common.entity.AiChatMessageEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiChatMessageMapper extends BaseMapper<AiChatMessageEntity> {

    IPage<AiChatMessageResponseDTO> queryPage(IPage<AiChatMessageResponseDTO> page, @Param("req") AiChatMessageQueryRequestDTO param);

    int getMaxMessageOrder(@Param("messageKey") String messageKey, @Param("envCode") String envCode);

    int getMaxTurnNo(@Param("messageKey") String messageKey, @Param("envCode") String envCode);

    List<AiChatUsageDTO> selectTodayUsage(@Param("envCode") String envCode);

    List<AiAccountUsageStatsDTO> selectUsageByAccountKeys(@Param("accountKeys") List<String> accountKeys);


    List<AiChatMessageResponseDTO> recoverByMemoryKey(@Param("memoryKey") String memoryKey);
}
