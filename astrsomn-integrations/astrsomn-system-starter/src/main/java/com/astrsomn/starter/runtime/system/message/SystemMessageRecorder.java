package com.astrsomn.starter.runtime.system.message;

import com.astrsomn.system.constant.SystemMessageEnum;
import com.astrsomn.system.dto.systemmessage.SystemMessageRecordCommand;
import com.astrsomn.system.entity.SystemMessageEntity;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.system.mapper.AstSystemMessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;


@Component
@RequiredArgsConstructor
public class SystemMessageRecorder {

    private final AstSystemMessageMapper systemMessageMapper;

    private static SystemMessageEntity toEntity(SystemMessageRecordCommand c) {
        SystemMessageEntity e = new SystemMessageEntity();
        BeanUtils.copyProperties(c, e, "id", "deleted");
        if (StringUtils.isBlank(e.getMessageType())) {
            e.setMessageType(SystemMessageEnum.MessageTypeEnum.OTHER.getCode());
        }
        if (StringUtils.isBlank(e.getMessageLevel())) {
            e.setMessageLevel(SystemMessageEnum.MessageLevelEnum.INFO.getCode());
        }
        if (StringUtils.isBlank(e.getReadStatus())) {
            e.setReadStatus(SystemMessageEnum.ReadStatusEnum.UNREAD.getCode());
        }
        e.setDeleted(Boolean.FALSE);
        return e;
    }


    @Transactional(rollbackFor = Exception.class)
    public SystemMessageEntity record(SystemMessageRecordCommand command) {
        Objects.requireNonNull(command, "command");
        SystemMessageEntity e = toEntity(command);
        int n = systemMessageMapper.insert(e);
        if (n < 1) {
            throw new IllegalStateException("System message insert failed");
        }
        if (e.getId() == null) {
            throw new IllegalStateException("System message id missing after insert");
        }
        SystemMessageEntity row = systemMessageMapper.selectById(e.getId());
        return row != null ? row : e;
    }
}
