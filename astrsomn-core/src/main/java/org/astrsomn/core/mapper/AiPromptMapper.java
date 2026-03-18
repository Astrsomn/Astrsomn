package org.astrsomn.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.astrsomn.core.common.entity.AiPromptEntity;

@Mapper
public interface AiPromptMapper extends BaseMapper<AiPromptEntity> {
    String getByUUID(@Param("promptUuid") String promptUuid);
}
