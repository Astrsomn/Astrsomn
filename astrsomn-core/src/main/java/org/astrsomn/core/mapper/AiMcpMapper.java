package org.astrsomn.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.astrsomn.core.common.entity.AiMcpEntity;

@Mapper
public interface AiMcpMapper extends BaseMapper<AiMcpEntity> {
}
