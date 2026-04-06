package org.astrsomn.starter.langchain.tool.image;


import dev.langchain4j.model.image.ImageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.mapper.AiAccountMapper;
import org.astrsomn.core.mapper.AiModelMapper;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.astrsomn.starter.langchain.factory.AstroModelFactory;
import org.springframework.stereotype.Component;



@Slf4j
@Component
@RequiredArgsConstructor
public class AiImageModelFactory {

    private final AiModelMapper aiModelMapper;
    private final AiAccountMapper aiAccountMapper;
    private final AstrsomnProperties astrsomnProperties;
    private final AstroModelFactory aastroModelFactory;

    public <T> ImageModel getImageModel(AstroChatParam<T> param) {
        return aastroModelFactory.createModel(param, ImageModel.class);
    }





}
