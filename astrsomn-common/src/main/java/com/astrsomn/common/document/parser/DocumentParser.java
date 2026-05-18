package com.astrsomn.common.document.parser;

import java.io.IOException;
import java.io.InputStream;


public interface DocumentParser {


    boolean supports(String filename);


    String parse(InputStream inputStream) throws IOException;
}
