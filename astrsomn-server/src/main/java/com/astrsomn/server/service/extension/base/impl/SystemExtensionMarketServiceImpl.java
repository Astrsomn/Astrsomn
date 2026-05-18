package com.astrsomn.server.service.extension.base.impl;

import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.service.extension.base.SystemExtensionMarketService;
import com.astrsomn.server.service.extension.base.SystemExtensionService;
import com.astrsomn.system.dto.extension.ExtensionMarketplaceItemDTO;
import com.astrsomn.system.entity.SystemExtensionEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class SystemExtensionMarketServiceImpl implements SystemExtensionMarketService {

    private final ApplicationContext applicationContext;
    private final RestTemplate restTemplate;
    private final SystemExtensionService systemExtensionService;

    // 返回一个链接
    private final String DOWNLOAD_URL = "https://www.astrsomn.com/api/plugins/{pluginId}/download/{version}";

    private final String MARKETPLACE_URL = "https://www.astrsomn.com/api/plugins/queryPage";

    @Override
    public PageResponse<ExtensionMarketplaceItemDTO> listCatalog(int pageNo, int pageSize, Optional<String> typeFilter) {
        // 构建请求参数
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("pageNo", pageNo);
        requestBody.put("pageSize", pageSize);

        Map<String, Object> param = new HashMap<>();
        typeFilter.ifPresent(t -> param.put("type", t));
        requestBody.put("param", param);

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);


        try {
            PageResponse<ExtensionMarketplaceItemDTO> resp = restTemplate.exchange(MARKETPLACE_URL, HttpMethod.POST, entity,
                            new org.springframework.core.ParameterizedTypeReference<PageResponse<ExtensionMarketplaceItemDTO>>() {
                            })
                    .getBody();
            if (resp == null || resp.getList() == null || resp.getList().isEmpty()) {
                return resp != null ? resp : PageResponse.empty();
            }
            markInstalledExtensions(resp.getList());
            return resp;
        } catch (Exception e) {
            // 如果外部 API 调用失败，返回空分页结果
            return PageResponse.empty();
        }
    }

    private void markInstalledExtensions(List<ExtensionMarketplaceItemDTO> items) {
        Set<String> keys = items.stream()
                .map(ExtensionMarketplaceItemDTO::getExtensionKey)
                .filter(k -> k != null && !k.isEmpty())
                .collect(Collectors.toSet());
        if (keys.isEmpty()) return;

        List<SystemExtensionEntity> installed = systemExtensionService.list(
                new LambdaQueryWrapper<SystemExtensionEntity>()
                        .in(SystemExtensionEntity::getExtensionKey, keys)
                        .eq(SystemExtensionEntity::getDeleted, 0)
        );
        Set<String> installedKeys = installed.stream()
                .map(SystemExtensionEntity::getExtensionKey)
                .collect(Collectors.toSet());

        for (ExtensionMarketplaceItemDTO item : items) {
            if (item.getExtensionKey() != null && installedKeys.contains(item.getExtensionKey())) {
                item.setInstalled(true);
            }
        }
    }

    @Override
    public BaseResponse<String> installExtension(String pluginId, String version) {
        try {
            // 构建下载 URL
            String url = DOWNLOAD_URL.replace("{pluginId}", pluginId).replace("{version}", version);

            // 下载 jar 包
            byte[] jarBytes = restTemplate.getForObject(url, byte[].class);
            if (jarBytes == null || jarBytes.length == 0) {
                return BaseResponse.fail("下载插件失败，文件为空", null);
            }

            // 创建 MultipartFile 实现类
            MultipartFile multipartFile = new MultipartFile() {
                @Override
                public String getName() {
                    return pluginId + "-" + version + ".jar";
                }

                @Override
                public String getOriginalFilename() {
                    return pluginId + "-" + version + ".jar";
                }

                @Override
                public String getContentType() {
                    return "application/java-archive";
                }

                @Override
                public boolean isEmpty() {
                    return jarBytes.length == 0;
                }

                @Override
                public long getSize() {
                    return jarBytes.length;
                }

                @Override
                public byte[] getBytes() throws IOException {
                    return jarBytes;
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return new ByteArrayInputStream(jarBytes);
                }

                @Override
                public void transferTo(File dest) throws IOException, IllegalStateException {
                    try (FileOutputStream fos = new FileOutputStream(dest)) {
                        fos.write(jarBytes);
                    }
                }
            };

            // 调用 uploadJar 方法安装插件
            // 注意：这里的参数需要根据实际情况设置，暂时使用默认值
            return systemExtensionService.uploadJar(
                    multipartFile
            );
        } catch (Exception e) {
            return BaseResponse.fail("安装插件失败：" + e.getMessage(), null);
        }
    }
}
