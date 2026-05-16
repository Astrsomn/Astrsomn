package com.astrsomn.server.service.document.parser;

import com.astrsomn.common.document.parser.DocumentParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DocumentParserRegistry {

    private static final List<DocumentParser> PARSERS = new ArrayList<>();

    static {
        PARSERS.add(new TxtDocumentParser());
        PARSERS.add(new PdfDocumentParser());
        PARSERS.add(new DocxDocumentParser());
        PARSERS.add(new MarkdownDocumentParser());
    }

    public static DocumentParser getParser(String filename) {
        for (DocumentParser parser : PARSERS) {
            if (parser.supports(filename)) {
                return parser;
            }
        }
        return null;
    }

    public static String parseFile(String filename, InputStream inputStream) throws IOException {
        DocumentParser parser = getParser(filename);
        if (parser == null) {
            throw new IOException("不支持的文件格式: " + filename);
        }
        return parser.parse(inputStream);
    }

    public static boolean isSupported(String filename) {
        return getParser(filename) != null;
    }
}
