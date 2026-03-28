package org.astrsomn.starter.langchain.quota;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SensitiveWordProvider {
    // 根节点：存储所有敏感词的树状结构
    private final Map<Character, Object> nodes = new ConcurrentHashMap<>();
    private static final String IS_END = "isEnd";

    /**
     * 初始化/刷新词库
     */
    public void refreshWords(List<String> words) {
        nodes.clear();
        for (String word : words) {
            Map<Character, Object> current = nodes;
            for (char c : word.toCharArray()) {
                current = (Map<Character, Object>) current.computeIfAbsent(c, k -> new ConcurrentHashMap<>());
            }
            current.put((char) 0, true); // 标记结尾
        }
        log.info("====> [Astrsomn] 敏感词库加载完成，共 {} 条", words.size());
    }

    /**
     * 过滤敏感词，替换为 *
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) return text;
        StringBuilder result = new StringBuilder(text);
        for (int i = 0; i < result.length(); i++) {
            int length = checkSensitiveWord(result.toString(), i);
            if (length > 0) {
                for (int j = 0; j < length; j++) {
                    result.setCharAt(i + j, '*');
                }
                i = i + length - 1;
            }
        }
        return result.toString();
    }

    /**
     * 检查是否存在敏感词，并返回长度
     */
    public int checkSensitiveWord(String text, int beginIndex) {
        Map<Character, Object> current = nodes;
        int length = 0;
        int matchLength = 0;
        for (int i = beginIndex; i < text.length(); i++) {
            char c = text.charAt(i);
            current = (Map<Character, Object>) current.get(c);
            if (current == null) break;
            length++;
            if (current.containsKey((char) 0)) {
                matchLength = length; // 找到一个词，记录当前长度
            }
        }
        return matchLength;
    }
}
