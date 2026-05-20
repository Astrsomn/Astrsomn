package com.astrsomn.server.service.vector.document.parser;

import com.astrsomn.common.document.parser.DocumentParser;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.IOException;
import java.io.InputStream;
import java.util.StringJoiner;

public class DocxDocumentParser implements DocumentParser {

    @Override
    public boolean supports(String filename) {
        return filename != null && filename.toLowerCase().endsWith(".docx");
    }

    @Override
    public String parse(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            StringJoiner joiner = new StringJoiner("\n");
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                if (text != null && !text.isBlank()) {
                    joiner.add(text);
                }
            }
            return joiner.toString();
        }
    }
}
