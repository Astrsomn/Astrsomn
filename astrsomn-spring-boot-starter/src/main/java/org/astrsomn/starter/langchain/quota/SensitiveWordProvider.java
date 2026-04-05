package org.astrsomn.starter.langchain.quota;

import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SensitiveWordProvider {

    private final Map<Character, Object> nodes = new ConcurrentHashMap<>();
    private static final String IS_END = "isEnd";


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
                matchLength = length;
            }
        }
        return matchLength;
    }
}
