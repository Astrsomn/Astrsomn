import type {WorkflowNodeDefinition, WorkflowNodeType} from './types'

export const nodeDefinitionMap: Record<WorkflowNodeType, WorkflowNodeDefinition> = {
    start: {
        type: 'start',
        category: 'control',
        title: '开始',
        description: '流程唯一入口，定义全局输入变量。',
        inputs: [],
        outputs: [{handleId: 'query', valueType: 'String'}, {handleId: 'files', valueType: 'Array'}],
        configSchema: [
            {key: 'queryKey', label: '默认输入变量名', component: 'input', defaultValue: 'query'},
            {key: 'allowFile', label: '允许文件上传', component: 'switch', defaultValue: true}
        ]
    },
    end: {
        type: 'end',
        category: 'control',
        title: '结束',
        description: '流程出口，返回最终响应。',
        inputs: [{handleId: 'answer', valueType: 'String'}, {handleId: 'artifacts', valueType: 'Array'}],
        outputs: [{handleId: 'result', valueType: 'Object'}],
        configSchema: [{
            key: 'responseMode',
            label: '响应模式',
            component: 'select',
            defaultValue: 'json',
            options: [{label: 'JSON', value: 'json'}, {label: '文本', value: 'text'}]
        }]
    },
    parallel: {
        type: 'parallel',
        category: 'logic',
        title: '并行 (Parallel)',
        description: '同时触发多个下游分支并在后续汇聚。',
        inputs: [{handleId: 'input', valueType: 'Any'}],
        outputs: [{handleId: 'branchA', valueType: 'Any'}, {handleId: 'branchB', valueType: 'Any'}],
        configSchema: [{key: 'branchCount', label: '分支数量', component: 'number', defaultValue: 2}]
    },
    llm: {
        type: 'llm',
        category: 'ai',
        title: 'LLM',
        description: '调用大语言模型进行生成。',
        inputs: [{handleId: 'system_prompt', valueType: 'String'}, {
            handleId: 'user_prompt',
            valueType: 'String'
        }, {handleId: 'context_data', valueType: 'Object'}],
        outputs: [{handleId: 'text', valueType: 'String'}, {handleId: 'usage', valueType: 'Object'}],
        configSchema: [
            {
                key: 'model',
                label: '模型',
                component: 'select',
                defaultValue: 'gpt-4o-mini',
                options: [{label: 'GPT-4o-mini', value: 'gpt-4o-mini'}, {label: 'Claude 3.5', value: 'claude-3.5'}]
            },
            {key: 'temperature', label: '温度', component: 'number', defaultValue: 0.7},
            {key: 'maxTokens', label: '最大 Token', component: 'number', defaultValue: 1024}
        ]
    },
    retrieval: {
        type: 'retrieval',
        category: 'ai',
        title: '知识库 (Retrieval)',
        description: '向量数据库检索背景知识（RAG）。',
        inputs: [{handleId: 'query', valueType: 'String'}],
        outputs: [{handleId: 'chunks', valueType: 'Array'}, {handleId: 'references', valueType: 'Array'}],
        configSchema: [
            {key: 'knowledgeBaseId', label: '知识库 ID', component: 'input', required: true},
            {key: 'topK', label: 'Top K', component: 'number', defaultValue: 5},
            {key: 'scoreThreshold', label: '相似度阈值', component: 'number', defaultValue: 0.5}
        ]
    },
    'if-else': {
        type: 'if-else',
        category: 'logic',
        title: '条件分支 (If/Else)',
        description: '根据表达式结果决定执行路径。',
        inputs: [{handleId: 'target_variable', valueType: 'Any'}],
        outputs: [{handleId: 'true', valueType: 'Boolean'}, {handleId: 'false', valueType: 'Boolean'}],
        configSchema: [
            {
                key: 'expression',
                label: '条件表达式',
                component: 'textarea',
                required: true,
                defaultValue: 'context.score >= 0.8'
            },
            {key: 'trueLabel', label: 'True 分支标签', component: 'input', defaultValue: '通过'},
            {key: 'falseLabel', label: 'False 分支标签', component: 'input', defaultValue: '不通过'}
        ]
    },
    'intent-classifier': {
        type: 'intent-classifier',
        category: 'logic',
        title: '问题分类器',
        description: '使用 LLM 对意图进行分类并路由。',
        inputs: [{handleId: 'query', valueType: 'String'}],
        outputs: [{handleId: 'classA', valueType: 'String'}, {handleId: 'classB', valueType: 'String'}],
        configSchema: [
            {key: 'labels', label: '分类标签(逗号分隔)', component: 'input', defaultValue: '问候,售后,投诉'},
            {key: 'prompt', label: '分类提示词', component: 'textarea', defaultValue: '请将用户意图分类到给定标签之一。'}
        ]
    },
    merge: {
        type: 'merge',
        category: 'logic',
        title: '变量聚合 (Merge)',
        description: '汇聚多个分支变量形成统一输出。',
        inputs: [{handleId: 'left', valueType: 'Any'}, {handleId: 'right', valueType: 'Any'}],
        outputs: [{handleId: 'result', valueType: 'String'}],
        configSchema: [{
            key: 'strategy',
            label: '聚合策略',
            component: 'select',
            defaultValue: 'concat',
            options: [{label: '拼接', value: 'concat'}, {label: 'JSON 合并', value: 'json-merge'}]
        }]
    },
    http: {
        type: 'http',
        category: 'tool',
        title: 'HTTP 请求',
        description: '调用外部 API 接口。',
        inputs: [{handleId: 'params', valueType: 'Object'}, {handleId: 'headers', valueType: 'Object'}],
        outputs: [{handleId: 'response_body', valueType: 'Object'}, {handleId: 'status_code', valueType: 'Number'}],
        configSchema: [
            {key: 'url', label: 'URL', component: 'input', required: true},
            {
                key: 'method',
                label: 'Method',
                component: 'select',
                defaultValue: 'GET',
                options: [{label: 'GET', value: 'GET'}, {label: 'POST', value: 'POST'}, {label: 'PUT', value: 'PUT'}]
            }
        ]
    },
    code: {
        type: 'code',
        category: 'tool',
        title: '代码执行',
        description: '运行 Python/JS 代码片段。',
        inputs: [{handleId: 'inputs', valueType: 'Object'}],
        outputs: [{handleId: 'output', valueType: 'Any'}],
        configSchema: [
            {
                key: 'language',
                label: '语言',
                component: 'select',
                defaultValue: 'python',
                options: [{label: 'Python', value: 'python'}, {label: 'JavaScript', value: 'javascript'}]
            },
            {key: 'script', label: '脚本', component: 'textarea', defaultValue: 'return inputs'}
        ]
    },
    tools: {
        type: 'tools',
        category: 'tool',
        title: '工具 (Tools)',
        description: '调用预定义工具或插件能力。',
        inputs: [{handleId: 'tool_input', valueType: 'Object'}],
        outputs: [{handleId: 'tool_output', valueType: 'Object'}],
        configSchema: [
            {key: 'toolName', label: '工具名称', component: 'input', required: true, defaultValue: 'web-search'},
            {key: 'timeoutMs', label: '超时(ms)', component: 'number', defaultValue: 8000}
        ]
    }
}

