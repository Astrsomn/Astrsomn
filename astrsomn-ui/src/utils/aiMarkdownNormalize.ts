/**
 * 修复模型输出里常见的 Markdown 问题，让 fenced code、标题能被正确解析。
 * 典型问题：### 与正文无空格、标题行与 ``` 粘连在同一行、围栏前缺少换行。
 */
export function normalizeAiMarkdown(raw: string): string {
  if (!raw) return ''
  let s = raw.replace(/\r\n/g, '\n')

  // CommonMark：ATX 标题的 # 后须有空格，否则 ###2.2 不会解析为标题
  s = s.replace(/^(\s*)(#{1,6})([^\s#])/gm, '$1$2 $3')

  // 同一行：正文末尾直接跟 ```lang（须带语言名，避免误伤单独的闭合 ```）
  s = s.replace(/([^\n])(```\s*[a-zA-Z][\w#+-]*)/g, '$1\n$2')

  // 围栏闭合行后若紧跟下一节标题或围栏且缺空行，保证至少一个换行（减少误吞）
  s = s.replace(/(```)\n?(#{1,6}[^\n])/g, '$1\n\n$2')

  return s
}
