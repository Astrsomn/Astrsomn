package com.astrsomn.api.workflow.domain.constant;

import com.astrsomn.common.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

public interface AstWorkflowEnum {

    @Getter
    @AllArgsConstructor
    enum NodeTypeEnum implements BaseEnum {
        START("START", "开始节点"),
        LLM("LLM", "LLM节点"),
        TOOL("TOOL", "工具节点"),
        CONDITION("CONDITION", "条件节点"),
        HUMAN("HUMAN", "人工节点"),
        END("END", "结束节点");

        private final String code;
        private final String desc;
    }

    @Getter
    @AllArgsConstructor
    enum ExecutionStatusEnum implements BaseEnum {
        RUNNING("RUNNING", "运行中"),
        SUSPENDED("SUSPENDED", "挂起"),
        COMPLETED("COMPLETED", "已完成"),
        FAILED("FAILED", "失败");

        private final String code;
        private final String desc;
    }

    @Getter
    @AllArgsConstructor
    enum HumanTaskStatusEnum implements BaseEnum {
        PENDING("PENDING", "待处理"),
        APPROVED("APPROVED", "已通过"),
        REJECTED("REJECTED", "已拒绝");

        private final String code;
        private final String desc;
    }
}
