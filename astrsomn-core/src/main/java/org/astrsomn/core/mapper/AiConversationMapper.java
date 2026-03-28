package org.astrsomn.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.astrsomn.core.common.dto.conversation.AiConversationQueryRequestDTO;
import org.astrsomn.core.common.dto.conversation.AiConversationResponseDTO;
import org.astrsomn.core.common.entity.AiConversationEntity;

import java.util.List;
import java.util.Map;

@Mapper
public interface AiConversationMapper extends BaseMapper<AiConversationEntity> {

    IPage<AiConversationResponseDTO> queryPage(IPage<AiConversationResponseDTO> page, @Param("req") AiConversationQueryRequestDTO param);

    int getMaxMessageOrder(@Param("messageKey") String messageKey);

    @Select("SELECT MODEL_KEY, SUM(CONSUME_TOKENS) FROM AI_CONVERSATION WHERE CREATE_TIME >= TODAY GROUP BY MODEL_KEY")
    List<Map<String, Object>> selectTodayUsage();
}
