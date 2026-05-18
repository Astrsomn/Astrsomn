package com.astrsomn.starter.workflow.config;

import com.astrsomn.api.runtime.common.mybatis.AstrsomnMybatisContributor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;


@Component
public class WorkflowMybatisContributor implements AstrsomnMybatisContributor {

    @Override
    public Collection<String> getMapperScanPackages() {
        return List.of("com.astrsomn.starter.workflow.mapper");
    }

    @Override
    public Collection<String> getTypeAliasesPackages() {
        return List.of("com.astrsomn.api.workflow.domain.entity");
    }

    @Override
    public Collection<String> getTenantTables() {
        return List.of(
                "ast_flow_biz_idempotent",
                "ast_flow_definition",
                "ast_flow_deployment",
                "ast_flow_human_task",
                "ast_flow_instance",
                "ast_flow_instance_event",
                "ast_flow_msg_outbox",
                "ast_flow_node_config",
                "ast_flow_node_history",
                "ast_flow_timer_job"
        );
    }
}
