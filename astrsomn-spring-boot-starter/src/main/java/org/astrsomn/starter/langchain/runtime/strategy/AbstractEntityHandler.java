package org.astrsomn.starter.langchain.runtime.strategy;

import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.common.langchain.buildParam.setting.ChatSetting;

import java.util.Objects;

/**
 * 实体查询与降级抽象处理器
 *
 * 设计模式：模板方法模式
 * 职责: 定义处理流程，具体的查询和降级实现由子类完成
 */
@Slf4j
public abstract class AbstractEntityHandler<T> {


    /**
     * 核心业务执行入口
     * 子类调用此方法触发完整的“查询-降级”流程
     */
    public T execute(AstroChatParam param) {
        try {
            // 1. 执行查询
            T result = doQuery(param);

            // 2. 判空检查
            if (Objects.isNull(result)) {
                log.warn("查询结果为空，准备执行降级。类型: {}", getEntityName());
                return doFallback();
            }

            return result;

        } catch (Exception e) {
            // 3. 异常捕获 -> 触发降级
            log.error("查询过程发生异常，执行降级逻辑。类型: {}, 错误: {}", getEntityName(), e.getMessage(), e);
            return doFallback();
        }
    }

    /**
     * 【抽象方法】具体的查询逻辑 (Callback)
     * 子类需在此实现 Mapper.selectOne 等操作
     */
    protected abstract T doQuery(AstroChatParam chatParam);

    /**
     * 【抽象方法】具体的降级逻辑 (Fallback)
     * 子类需在此实现返回默认对象等操作
     */
    protected abstract T doFallback();

    /**
     * 【可选】获取实体名称用于日志
     */
    protected String getEntityName() {
        return this.getClass().getSimpleName();
    }

    public ChatSetting getParam() {
        return null;
    }
}
