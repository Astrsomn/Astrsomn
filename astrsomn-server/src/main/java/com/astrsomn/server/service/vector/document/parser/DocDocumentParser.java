package com.astrsomn.server.service.vector.document.parser;

import com.astrsomn.common.document.parser.DocumentParser;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

import java.io.IOException;
import java.io.InputStream;

public class DocDocumentParser implements DocumentParser {

    @Override
    public boolean supports(String filename) {
        if (filename == null) return false;
        String lower = filename.toLowerCase();
        return lower.endsWith(".doc") && !lower.endsWith(".docx");
    }

    @Override
    public String parse(InputStream inputStream) throws IOException {
        try (HWPFDocument document = new HWPFDocument(inputStream)) {
            WordExtractor extractor = new WordExtractor(document);
            return extractor.getText();
        }
    }
}
