package org.astrsomn.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.astrsomn.core.common.entity.AiConversationEntity;

@Mapper
public interface AiConversationMapper extends BaseMapper<AiConversationEntity> {

    /**
     * 通过messageId获取系统中最后一条记录，方便排序
     * @param messageId 同一对话中的消息ID
     * @return  返回order排序
     */
    int getMaxMessageOrder(@Param("messageId") String messageId);



}
