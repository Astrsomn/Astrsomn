package org.astrsomn.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.extension.ExtensionMarketplaceItemDTO;
import org.astrsomn.server.service.ExtensionMarketplaceCatalogSource;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * 插件市场目录：调用外部 API 获取插件列表。
 */
@Component
@RequiredArgsConstructor
public class CompositeExtensionMarketplaceCatalogSource implements ExtensionMarketplaceCatalogSource {

    private final ApplicationContext applicationContext;
    private final RestTemplate restTemplate;

    @Override
    public PageResponse<ExtensionMarketplaceItemDTO> listCatalog(int pageNum, int pageSize, Optional<String> typeFilter) {
        // 构建请求参数
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("pageNum", pageNum);
        requestBody.put("pageSize", pageSize);
        
        Map<String, Object> data = new HashMap<>();
        typeFilter.ifPresent(t -> data.put("type", t));
        requestBody.put("data", data);
        
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        // 调用外部 API
        String url = "https://www.astrsomn.com/api/plugins/queryPage";
        try {
            return restTemplate.exchange(url, HttpMethod.POST, entity, 
                    new org.springframework.core.ParameterizedTypeReference<PageResponse<ExtensionMarketplaceItemDTO>>() {})
                    .getBody();
        } catch (Exception e) {
            // 如果外部 API 调用失败，返回空分页结果
            return PageResponse.empty();
        }
    }
}
