import {computed} from 'vue';
import {usePageTranslation} from '@/locales/pages.ts';
import type {AdminModuleNavGroup} from '@/views/admin/layout/utils/adminModuleTypes.ts';
import {
    ApiOutlined,
    AppstoreAddOutlined,
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

export function useAdminUnifiedNav() {
    const t = usePageTranslation('common');
    return computed<AdminModuleNavGroup[]>(() => [
        {
            key: 'ai-config',
            label: t.value.nav.aiConfig,
            icon: RobotOutlined,
            children: [
                {to: `${baseAi}/agents`, label: t.value.nav.agents, icon: TeamOutlined},
                {to: `${baseAi}/ai-instance`, label: t.value.nav.aiInstance, icon: CloudServerOutlined},
                {to: `${baseAi}/models`, label: t.value.nav.models, icon: DatabaseOutlined},
                {to: `${baseAi}/ai-account`, label: t.value.nav.aiAccount, icon: KeyOutlined},
                {to: `${baseAi}/mcp`, label: t.value.nav.mcp, icon: ApiOutlined},
                {to: `${baseAi}/tools`, label: t.value.nav.tools, icon: ToolOutlined},
                {to: `${baseAi}/prompts`, label: t.value.nav.prompts, icon: FileTextOutlined},
                {to: `${baseAi}/conversations`, label: t.value.nav.conversations, icon: MessageOutlined}
            ]
        },
        {
            key: 'ai-safety',
            label: t.value.nav.aiSafety,
            icon: SecurityScanOutlined,
            children: [
                {to: `${baseSafety}/templates`, label: t.value.nav.templates, icon: CodeOutlined},
                {to: `${baseSafety}/security`, label: t.value.nav.security, icon: SafetyCertificateOutlined},
                {to: `${baseSafety}/tracing`, label: t.value.nav.tracing, icon: BranchesOutlined}
            ]
        },
        {
            key: 'ai-workflow',
            label: t.value.nav.aiWorkflow,
            icon: BranchesOutlined,
            children: [
                {to: `${baseWorkflow}/definitions`, label: t.value.nav.definitions, icon: AppstoreAddOutlined},
                {to: `${baseWorkflow}/deployments`, label: t.value.nav.deployments, icon: CloudServerOutlined},
                {to: `${baseWorkflow}/instances`, label: t.value.nav.instances, icon: DatabaseOutlined},
                {to: `${baseWorkflow}/node-configs`, label: t.value.nav.nodeConfigs, icon: ControlOutlined},
                {to: `${baseWorkflow}/node-history`, label: t.value.nav.nodeHistory, icon: FileTextOutlined},
                {to: `${baseWorkflow}/human-tasks`, label: t.value.nav.humanTasks, icon: TeamOutlined},
                {to: `${baseWorkflow}/publish`, label: t.value.nav.publish, icon: CloudServerOutlined},
                {to: `${baseWorkflow}/test-run`, label: t.value.nav.testRun, icon: ApiOutlined},
                {to: `${baseWorkflow}/biz-idempotent`, label: t.value.nav.bizIdempotent, icon: KeyOutlined},
                {to: `${baseWorkflow}/instance-events`, label: t.value.nav.instanceEvents, icon: FileTextOutlined},
                {to: `${baseWorkflow}/msg-outbox`, label: t.value.nav.msgOutbox, icon: MessageOutlined},
                {to: `${baseWorkflow}/timer-jobs`, label: t.value.nav.timerJobs, icon: ControlOutlined}
            ]
        },
        {
            key: 'system',
            label: t.value.nav.system,
            icon: SettingOutlined,
            children: [
                {to: `${baseSystem}/users`, label: t.value.nav.users, icon: UserOutlined},
                {to: `${baseSystem}/env`, label: t.value.nav.env, icon: GlobalOutlined},
                {to: `${baseSystem}/config`, label: t.value.nav.config, icon: ControlOutlined},
                {to: `${baseSystem}/messages`, label: t.value.nav.messages, icon: MessageOutlined},
                {to: `${baseSystem}/extensions`, label: t.value.nav.extensions, icon: AppstoreAddOutlined}
            ]
        }
    ]);
}
