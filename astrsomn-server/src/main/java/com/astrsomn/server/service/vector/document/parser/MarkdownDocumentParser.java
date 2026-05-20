package com.astrsomn.server.service.vector.document.parser;

import com.astrsomn.common.document.parser.DocumentParser;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MarkdownDocumentParser implements DocumentParser {

    private final Parser parser = Parser.builder().build();
    private final TextContentRenderer renderer = TextContentRenderer.builder().build();

    @Override
    public boolean supports(String filename) {
        if (filename == null) return false;
        String lower = filename.toLowerCase();
        return lower.endsWith(".md") || lower.endsWith(".markdown");
    }

    @Override
    public String parse(InputStream inputStream) throws IOException {
        String markdown = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }
}
