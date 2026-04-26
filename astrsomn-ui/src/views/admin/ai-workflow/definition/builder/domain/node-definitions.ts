import type { WorkflowNodeDefinition, WorkflowNodeType } from './types'

export const nodeDefinitionMap: Record<WorkflowNodeType, WorkflowNodeDefinition> = {
  start: {
    type: 'start',
    category: 'control',
    title: '开始',
    description: '流程唯一入口，定义全局输入变量。',
    inputs: [],
    outputs: [{ handleId: 'query', valueType: 'String' }, { handleId: 'files', valueType: 'Array' }],
    configSchema: [
      { key: 'queryKey', label: '默认输入变量名', component: 'input', defaultValue: 'query' },
      { key: 'allowFile', label: '允许文件上传', component: 'switch', defaultValue: true }
    ]
  },
  end: {
    type: 'end',
    category: 'control',
    title: '结束',
    description: '流程出口，返回最终响应。',
    inputs: [{ handleId: 'answer', valueType: 'String' }, { handleId: 'artifacts', valueType: 'Array' }],
    outputs: [{ handleId: 'result', valueType: 'Object' }],
    configSchema: [{ key: 'responseMode', label: '响应模式', component: 'select', defaultValue: 'json', options: [{ label: 'JSON', value: 'json' }, { label: '文本', value: 'text' }] }]
  },
  parallel: {
    type: 'parallel',
    category: 'control',
    title: '并行/汇聚',
    description: '触发多个分支并等待汇聚。',
    inputs: [{ handleId: 'input', valueType: 'Any' }],
    outputs: [{ handleId: 'branchA', valueType: 'Any' }, { handleId: 'branchB', valueType: 'Any' }],
    configSchema: [{ key: 'branchCount', label: '分支数量', component: 'number', defaultValue: 2 }]
  },
  llm: {
    type: 'llm',
    category: 'ai',
    title: 'LLM',
    description: '调用大语言模型进行生成。',
    inputs: [{ handleId: 'system_prompt', valueType: 'String' }, { handleId: 'user_prompt', valueType: 'String' }, { handleId: 'context_data', valueType: 'Object' }],
    outputs: [{ handleId: 'text', valueType: 'String' }, { handleId: 'usage', valueType: 'Object' }],
    configSchema: [
      { key: 'model', label: '模型', component: 'select', defaultValue: 'gpt-4o-mini', options: [{ label: 'GPT-4o-mini', value: 'gpt-4o-mini' }, { label: 'Claude 3.5', value: 'claude-3.5' }] },
      { key: 'temperature', label: '温度', component: 'number', defaultValue: 0.7 },
      { key: 'maxTokens', label: '最大 Token', component: 'number', defaultValue: 1024 }
    ]
  },
  knowledge: {
    type: 'knowledge',
    category: 'ai',
    title: '知识库检索',
    description: 'RAG 检索相关上下文片段。',
    inputs: [{ handleId: 'query', valueType: 'String' }],
    outputs: [{ handleId: 'chunks', valueType: 'Array' }, { handleId: 'references', valueType: 'Array' }],
    configSchema: [
      { key: 'knowledgeBaseId', label: '知识库 ID', component: 'input', required: true },
      { key: 'topK', label: 'Top K', component: 'number', defaultValue: 5 },
      { key: 'scoreThreshold', label: '相似度阈值', component: 'number', defaultValue: 0.5 }
    ]
  },
  vision: {
    type: 'vision',
    category: 'ai',
    title: '多模态识别',
    description: '图像识别与结构化提取。',
    inputs: [{ handleId: 'image', valueType: 'Any' }, { handleId: 'prompt', valueType: 'String' }],
    outputs: [{ handleId: 'description', valueType: 'String' }, { handleId: 'structured_data', valueType: 'Object' }],
    configSchema: [{ key: 'visionModel', label: '视觉模型', component: 'input', defaultValue: 'gpt-4o-mini' }]
  },
  condition: {
    type: 'condition',
    category: 'logic',
    title: '条件分支',
    description: '根据表达式进行路由。',
    inputs: [{ handleId: 'target_variable', valueType: 'Any' }],
    outputs: [{ handleId: 'true', valueType: 'Boolean' }, { handleId: 'false', valueType: 'Boolean' }],
    configSchema: [
      { key: 'expression', label: '条件表达式', component: 'textarea', required: true, defaultValue: 'context.score >= 0.8' },
      { key: 'trueLabel', label: 'True 分支标签', component: 'input', defaultValue: '通过' },
      { key: 'falseLabel', label: 'False 分支标签', component: 'input', defaultValue: '不通过' }
    ]
  },
  iterator: {
    type: 'iterator',
    category: 'logic',
    title: '迭代器',
    description: '对数组进行循环处理。',
    inputs: [{ handleId: 'list', valueType: 'Array' }],
    outputs: [{ handleId: 'item', valueType: 'Any' }, { handleId: 'index', valueType: 'Number' }],
    configSchema: [{ key: 'maxLoop', label: '最大循环次数', component: 'number', defaultValue: 20 }]
  },
  template: {
    type: 'template',
    category: 'logic',
    title: '变量聚合',
    description: '将多个变量模板拼接为结果文本。',
    inputs: [{ handleId: 'variables', valueType: 'Object' }],
    outputs: [{ handleId: 'result', valueType: 'String' }],
    configSchema: [{ key: 'template', label: '模板内容', component: 'textarea', required: true, defaultValue: '根据{{var1}}处理{{var2}}' }]
  },
  http: {
    type: 'http',
    category: 'tool',
    title: 'HTTP 请求',
    description: '调用外部 API 接口。',
    inputs: [{ handleId: 'params', valueType: 'Object' }, { handleId: 'headers', valueType: 'Object' }],
    outputs: [{ handleId: 'response_body', valueType: 'Object' }, { handleId: 'status_code', valueType: 'Number' }],
    configSchema: [
      { key: 'url', label: 'URL', component: 'input', required: true },
      { key: 'method', label: 'Method', component: 'select', defaultValue: 'GET', options: [{ label: 'GET', value: 'GET' }, { label: 'POST', value: 'POST' }, { label: 'PUT', value: 'PUT' }] }
    ]
  },
  code: {
    type: 'code',
    category: 'tool',
    title: '代码执行',
    description: '运行 Python/JS 代码片段。',
    inputs: [{ handleId: 'inputs', valueType: 'Object' }],
    outputs: [{ handleId: 'output', valueType: 'Any' }],
    configSchema: [
      { key: 'language', label: '语言', component: 'select', defaultValue: 'python', options: [{ label: 'Python', value: 'python' }, { label: 'JavaScript', value: 'javascript' }] },
      { key: 'script', label: '脚本', component: 'textarea', defaultValue: 'return inputs' }
    ]
  },
  search: {
    type: 'search',
    category: 'tool',
    title: '搜索',
    description: '联网检索实时信息。',
    inputs: [{ handleId: 'query', valueType: 'String' }],
    outputs: [{ handleId: 'search_results', valueType: 'Array' }],
    configSchema: [{ key: 'provider', label: '搜索提供方', component: 'select', defaultValue: 'bing', options: [{ label: 'Bing', value: 'bing' }, { label: 'Google', value: 'google' }] }]
  },
  'human-audit': {
    type: 'human-audit',
    category: 'interaction',
    title: '人工审核',
    description: '流程挂起等待人工确认。',
    inputs: [{ handleId: 'wait_for_review', valueType: 'Any' }],
    outputs: [{ handleId: 'is_approved', valueType: 'Boolean' }, { handleId: 'comment', valueType: 'String' }],
    configSchema: [{ key: 'timeoutAction', label: '超时动作', component: 'select', defaultValue: 'reject', options: [{ label: '驳回', value: 'reject' }, { label: '继续', value: 'continue' }] }]
  },
  'input-form': {
    type: 'input-form',
    category: 'interaction',
    title: '输入增强',
    description: '中间态表单采集补充信息。',
    inputs: [{ handleId: 'fields', valueType: 'Array' }],
    outputs: [{ handleId: 'form_data', valueType: 'Object' }],
    configSchema: [{ key: 'formSchema', label: '表单 Schema', component: 'textarea', defaultValue: '[{\"key\":\"email\",\"type\":\"input\"}]' }]
  }
}

