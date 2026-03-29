package org.astrsomn.core.common.langchain.buildParam.setting;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ImageSetting {


    private String style;


    private String size;


}
