package com.astrsomn.starter.runtime.langchain.exception;


public class ContentModerationException extends AstroModelException {

    public ContentModerationException() {
        super(ErrorCode.CONTENT_MODERATION_BLOCKED, "Input contains prohibited content");
    }

    public ContentModerationException(String detail) {
        super(ErrorCode.CONTENT_MODERATION_BLOCKED, detail);
    }
}
