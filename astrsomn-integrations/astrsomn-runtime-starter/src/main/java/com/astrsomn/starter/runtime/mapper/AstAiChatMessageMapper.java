package com.astrsomn.starter.runtime.mapper;

import com.astrsomn.api.runtime.common.dto.chat.message.AiChatUsageDTO;
import com.astrsomn.api.runtime.common.entity.AiChatMessageEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AstAiChatMessageMapper extends BaseMapper<AiChatMessageEntity> {

    @Select("SELECT COALESCE(MAX(MESSAGE_ORDER), -1) FROM AI_CHAT_MESSAGE WHERE DELETED = 0 AND MEMORY_KEY = #{memoryKey}")
    int getMaxMessageOrder(@Param("memoryKey") String memoryKey, @Param("envCode") String envCode);

    @Select("SELECT COALESCE(MAX(TURN_NO), 0) FROM AI_CHAT_MESSAGE WHERE DELETED = 0 AND MEMORY_KEY = #{memoryKey}")
    int getMaxTurnNo(@Param("memoryKey") String memoryKey, @Param("envCode") String envCode);

    @Select("SELECT MODEL_KEY AS modelKey, SUM(TOTAL_TOKENS) AS total FROM AI_CHAT_MESSAGE WHERE CREATE_TIME >= CURDATE() AND DELETED = 0 AND ENV_CODE = #{env} GROUP BY MODEL_KEY")
    List<AiChatUsageDTO> selectTodayUsage(@Param("env") String env);
}
