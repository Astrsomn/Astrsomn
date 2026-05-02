package com.astrsomn.api.runtime.common.langchain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.common.base.BaseEnum;

public interface ChatStreamEnum {

    @Getter
    @AllArgsConstructor
    enum AstroChatRole implements BaseEnum {
        USER("user", "用户"),
        ASSISTANT("assistant", "AI回答"),
        SYSTEM("system", "系统"),
        THOUGHT("thought", "思考"); // 专门用于思考过程

        private String code;

        private String desc;
    }



    @Getter
    @AllArgsConstructor
    enum AstroEventType implements BaseEnum {

        TEXT("text", "文本", ""),
        THOUGHT("thought", "思考", ""),
        HTML("html", "html代码", ""),
        ERROR("error", "错误", ""),
        IMAGE("image", "图像", ""),
        DONE("done", "完成", "");
        private String code;

        private String desc;

        private String prefix;
    }

}
