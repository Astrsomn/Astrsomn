package com.astrsomn.common.document.parser;

import java.io.IOException;
import java.io.InputStream;

/**
 * 文档解析器接口，将不同格式的文件解析为纯文本
 */
public interface DocumentParser {

    /**
     * 是否能解析该文件
     */
    boolean supports(String filename);

    /**
     * 将文件流解析为纯文本
     */
    String parse(InputStream inputStream) throws IOException;
}
