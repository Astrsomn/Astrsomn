package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.astrsomn.core.common.base.BaseEntity;

/**
 * AI MCP 配置实体（服务名称、类型 SSE/STDIO/STEAMABLE、SSE 地址、请求头等）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("AI_MCP_CONFIG")
public class AiMcpEntity extends BaseEntity<Long> {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 服务名称 */
    @TableField("server_name")
    private String serverName;

    /** 描述 */
    private String description;

    /** 类型：SSE / STEAMABLE / STDIO */
    private String type;

    /** SSE 地址（类型为 SSE 时必填） */
    @TableField("sse_address")
    private String sseAddress;

    /** 请求头配置（如 JSON 字符串） */
    @TableField("request_header_config")
    private String requestHeaderConfig;

    /** 是否启用：1-是 0-否 */
    private Integer enabled;

    /** * 【新增字段】STDIO 运行命令 (例如: "npx", "python", "node")
     */
    @TableField("command")
    private String command;

    /** * 【新增字段】运行参数 (例如: "-y", "@modelcontextprotocol/server-everything")
     * 建议存储为 JSON 数组字符串或以空格分隔
     */
    @TableField("args")
    private String args;

    /** * 【新增字段】环境变量 (例如: {"API_KEY": "xxx"})
     * 建议存储为 JSON 字符串
     */
    @TableField("env_vars")
    private String envVars;
}
