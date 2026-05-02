package com.astrsomn.starter.runtime.message;

import com.astrsomn.api.runtime.common.constant.SystemMessageEnum;
import com.astrsomn.api.runtime.common.dto.systemmessage.SystemMessageRecordCommand;
import com.astrsomn.api.runtime.common.entity.SystemMessageEntity;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.mapper.SystemMessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 仅负责将系统消息落库，供各模块（插件、定时任务、编排层）在<strong>不依赖 app 服务层</strong> 的情况下写入
 * {@code SYS_MESSAGE}。推送给前端的场景应在业务应用（如 astrsomn-server）中
 * 通过「事件 + SSE」等方式组合，本类不依赖 Web 层。
 * <p>
 * 与 app 中 HTTP 暴露的 system-message 服务可并存，内部/插件类优先本入口以减少依赖。
 */
@Component
@RequiredArgsConstructor
public class SystemMessageRecorder {

    private final SystemMessageMapper systemMessageMapper;

    /**
     * 写入并返回表行；插入失败时抛出 {@link IllegalStateException}。
     */
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
}
