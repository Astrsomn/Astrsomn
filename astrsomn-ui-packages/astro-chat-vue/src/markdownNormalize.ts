/**
 * 修复模型输出里常见的 Markdown 问题，让 fenced code、标题能被正确解析。
 */
export function normalizeAiMarkdown(raw: string): string {
    if (!raw) return ''
    let s = raw.replace(/\r\n/g, '\n')

    s = s.replace(/^(\s*)(#{1,6})([^\s#])/gm, '$1$2 $3')

    s = s.replace(/([^\n])(```\s*[a-zA-Z][\w#+-]*)/g, '$1\n$2')

    s = s.replace(/(```)\n?(#{1,6}[^\n])/g, '$1\n\n$2')

    return s
}
