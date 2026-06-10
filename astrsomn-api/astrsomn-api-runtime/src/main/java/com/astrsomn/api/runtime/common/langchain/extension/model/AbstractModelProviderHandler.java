package com.astrsomn.api.runtime.common.langchain.extension.model;

import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.common.UnknowModelException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;

/**
 * Base class for model provider handlers.
 * <p>
 * Provides shared utility methods and a template dispatch pattern that
 * subclasses can use or override as needed.
 * <ul>
 *   <li>{@link #resolveModelKey(AstroChatParam)} — resolves model name from param</li>
 *   <li>{@link #validateParams(Class, AstroChatParam)} — validates common null constraints</li>
 *   <li>{@link #dispatchModel(Class, AstroChatParam)} — dispatches to getChatModel / getStreamModel / getEmbeddingModel</li>
 * </ul>
 * </p>
 */
public abstract class AbstractModelProviderHandler implements ModelProviderHandler {

    /**
     * Resolve the effective model key from chat parameters.
     * Falls back to modelName from ModelSetting if modelKey is not set.
     */
    protected static String resolveModelKey(AstroChatParam<?> param) {
        String modelKey = param.getModelKey();
        if ((modelKey == null || modelKey.isBlank()) && param.getModelSetting() != null) {
            modelKey = param.getModelSetting().getModelName();
        }
        return modelKey;
    }

    /**
     * Validate that model class, param, and its model setting are all non-null.
     *
     * @throws IllegalArgumentException if any required parameter is null
     */
    protected static void validateParams(Class<?> modelClass, AstroChatParam<?> param) {
        if (modelClass == null || param == null || param.getModelSetting() == null) {
            throw new IllegalArgumentException("Model class and parameters must not be null");
        }
    }

    /**
     * Template dispatch method for creating a model instance.
     * <p>
     * Routes to {@link #getChatModel(AstroChatParam)}, {@link #getStreamModel(AstroChatParam)},
     * or {@link #getEmbeddingModel(AstroChatParam)} based on the requested modelClass type.
     * </p>
     *
     * @param modelClass the expected model interface (ChatModel, StreamingChatModel, or EmbeddingModel)
     * @param param      the chat parameters containing settings, API key, base URL
     * @param <T>        the model type
     * @return a model instance of the requested type
     * @throws UnknowModelException if the model type is not supported or the cast fails
     */
    protected <T> T dispatchModel(Class<T> modelClass, AstroChatParam<?> param) {
        validateParams(modelClass, param);

        Object model;

        if (StreamingChatModel.class.isAssignableFrom(modelClass)) {
            model = getStreamModel(param);
        } else if (ChatModel.class.isAssignableFrom(modelClass)) {
            model = getChatModel(param);
        } else if (EmbeddingModel.class.isAssignableFrom(modelClass)) {
            model = getEmbeddingModel(param);
        } else {
            throw new UnknowModelException(
                    "Failed to initialize: " + modelClass.getName()
                            + " is not supported by " + getProvider().getDesc() + " provider.");
        }

        try {
            return modelClass.cast(model);
        } catch (ClassCastException e) {
            throw new UnknowModelException(
                    "Model instance created but is not compatible with " + modelClass.getName());
        }
    }

    // --- Hook methods for subclasses ---

    /**
     * Serialize an object to JSON string. Used by handlers for capabilities/params.
     */
    protected static String toJson(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            return "[]";
        }
    }

    /**
     * Create a non-streaming chat model. Override if the provider supports chat.
     */
    protected ChatModel getChatModel(AstroChatParam<?> param) {
        throw new UnsupportedOperationException(
                "Chat model not supported by " + getProvider().getDesc() + " provider.");
    }

    /**
     * Create a streaming chat model. Override if the provider supports streaming.
     */
    protected StreamingChatModel getStreamModel(AstroChatParam<?> param) {
        throw new UnsupportedOperationException(
                "Streaming chat model not supported by " + getProvider().getDesc() + " provider.");
    }

    /**
     * Create an embedding model. Override if the provider supports embeddings.
     */
    protected EmbeddingModel getEmbeddingModel(AstroChatParam<?> param) {
        throw new UnsupportedOperationException(
                "Embedding model not supported by " + getProvider().getDesc() + " provider.");
    }

}
