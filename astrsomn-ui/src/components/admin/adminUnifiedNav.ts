import type { AdminModuleNavGroup } from '@/components/admin/adminModuleTypes';
import {
  AppstoreAddOutlined,
  ApiOutlined,
  BranchesOutlined,
  CloudServerOutlined,
  CodeOutlined,
  ControlOutlined,
  DatabaseOutlined,
  FileTextOutlined,
  GlobalOutlined,
  KeyOutlined,
  MessageOutlined,
  RobotOutlined,
  SafetyCertificateOutlined,
  SecurityScanOutlined,
  SettingOutlined,
  TeamOutlined,
  ToolOutlined,
  UserOutlined
} from '@ant-design/icons-vue';

const baseAi = '/admin/ai-config';
const baseSafety = '/admin/ai-safety';
const baseWorkflow = '/admin/ai-workflow';
const baseSystem = '/admin/system';

/**
 * 管理侧栏：三层分类（组 → 子项），与 /admin/ai-config、/admin/ai-safety、/admin/system 下子路由一一对应。
 */
export const adminUnifiedNav: AdminModuleNavGroup[] = [
  {
    key: 'ai-config',
    label: 'AI 配置',
    icon: RobotOutlined,
    children: [
      { to: `${baseAi}/agents`, label: '智能体', icon: TeamOutlined },
      { to: `${baseAi}/ai-instance`, label: '推理配置', icon: CloudServerOutlined },
      { to: `${baseAi}/models`, label: '模型接入', icon: DatabaseOutlined },
      { to: `${baseAi}/ai-account`, label: 'AI 账号', icon: KeyOutlined },
      { to: `${baseAi}/mcp`, label: 'MCP', icon: ApiOutlined },
      { to: `${baseAi}/tools`, label: 'Tools', icon: ToolOutlined },
      { to: `${baseAi}/prompts`, label: '提示词', icon: FileTextOutlined },
      { to: `${baseAi}/conversations`, label: '对话管理', icon: MessageOutlined }
    ]
  },
  {
    key: 'ai-safety',
    label: '安全与治理',
    icon: SecurityScanOutlined,
    children: [
      { to: `${baseSafety}/templates`, label: 'FTL 模板', icon: CodeOutlined },
      { to: `${baseSafety}/security`, label: '敏感词', icon: SafetyCertificateOutlined },
      { to: `${baseSafety}/tracing`, label: '链路追踪', icon: BranchesOutlined }
    ]
  },
  {
    key: 'ai-workflow',
    label: '工作流',
    icon: BranchesOutlined,
    children: [
      { to: `${baseWorkflow}/definitions`, label: '流程定义', icon: AppstoreAddOutlined },
      { to: `${baseWorkflow}/deployments`, label: '流程发布', icon: CloudServerOutlined },
      { to: `${baseWorkflow}/instances`, label: '流程实例', icon: DatabaseOutlined },
      { to: `${baseWorkflow}/node-configs`, label: '节点配置', icon: ControlOutlined },
      { to: `${baseWorkflow}/node-history`, label: '节点历史', icon: FileTextOutlined },
      { to: `${baseWorkflow}/human-tasks`, label: '人工任务', icon: TeamOutlined },
      { to: `${baseWorkflow}/publish`, label: '发布接口', icon: CloudServerOutlined },
      { to: `${baseWorkflow}/test-run`, label: '测试运行', icon: ApiOutlined },
      { to: `${baseWorkflow}/biz-idempotent`, label: '业务幂等', icon: KeyOutlined },
      { to: `${baseWorkflow}/instance-events`, label: '实例事件', icon: FileTextOutlined },
      { to: `${baseWorkflow}/msg-outbox`, label: '消息 Outbox', icon: MessageOutlined },
      { to: `${baseWorkflow}/timer-jobs`, label: '定时任务', icon: ControlOutlined }
    ]
  },
  {
    key: 'system',
    label: '系统管理',
    icon: SettingOutlined,
    children: [
      { to: `${baseSystem}/users`, label: '用户管理', icon: UserOutlined },
      { to: `${baseSystem}/env`, label: '环境管理', icon: GlobalOutlined },
      { to: `${baseSystem}/config`, label: '系统配置', icon: ControlOutlined },
      { to: `${baseSystem}/messages`, label: '系统消息', icon: MessageOutlined },
      { to: `${baseSystem}/extensions`, label: '系统扩展', icon: AppstoreAddOutlined }
    ]
  }
];
