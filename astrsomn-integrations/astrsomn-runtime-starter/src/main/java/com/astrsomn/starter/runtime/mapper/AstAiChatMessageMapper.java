package com.astrsomn.starter.runtime.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.api.runtime.common.dto.account.AiAccountUsageStatsDTO;
import com.astrsomn.api.runtime.common.dto.chat.message.AiChatMessageQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.chat.message.AiChatMessageResponseDTO;
import com.astrsomn.api.runtime.common.dto.chat.message.AiChatUsageDTO;
import com.astrsomn.api.runtime.common.entity.AiChatMessageEntity;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface AstAiChatMessageMapper extends BaseMapper<AiChatMessageEntity> {

    @Select("   SELECT COALESCE(MAX(MESSAGE_ORDER), -1) FROM `AI_CHAT_MESSAGE`\n" +
            "        WHERE DELETED = 0 AND MEMORY_KEY = #{messageKey}")
    int getMaxMessageOrder(String memoryKey, String envKey);

    @Select("SELECT COALESCE(MAX(TURN_NO), 0) FROM `AI_CHAT_MESSAGE`\n" +
            "        WHERE DELETED = 0 AND MEMORY_KEY = #{messageKey}")
    int getMaxTurnNo(String memoryKey, String envKey);

    @Select("  SELECT\n" +
            "            MODEL_KEY AS modelKey,\n" +
            "            SUM(TOTAL_TOKENS) AS total\n" +
            "        FROM\n" +
            "            `AI_CHAT_MESSAGE`\n" +
            "        WHERE\n" +
            "            CREATE_TIME >= CURDATE()\n" +
            "            AND DELETED = 0\n" +
            "            AND ENV_CODE = #{envCode}\n" +
            "        GROUP BY\n" +
            "            MODEL_KEY")
    List<AiChatUsageDTO> selectTodayUsage(String env);
}
