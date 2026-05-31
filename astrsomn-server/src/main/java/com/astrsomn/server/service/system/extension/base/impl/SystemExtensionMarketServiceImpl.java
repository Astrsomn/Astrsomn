package com.astrsomn.server.service.system.extension.base.impl;

import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.common.utils.CollectionUtils;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.service.system.extension.base.SystemExtensionMarketService;
import com.astrsomn.server.service.system.extension.base.SystemExtensionService;
import com.astrsomn.system.dto.extension.ExtensionMarketplaceItemDTO;
import com.astrsomn.system.dto.extension.ExtensionMarketplaceVersionDTO;
import com.astrsomn.system.entity.SystemExtensionEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class SystemExtensionMarketServiceImpl implements SystemExtensionMarketService {

    private final ApplicationContext applicationContext;
    private final RestTemplate restTemplate;
    private final SystemExtensionService systemExtensionService;

    // 返回一个链接
    private final String VERSION_DETAIL_URL = "https://www.astrsomn.com/api/plugins/{pluginId}/versions/{version}";

    private final String MARKETPLACE_URL = "https://www.astrsomn.com/api/plugins/queryPage";

    private final String VERSIONS_URL = "https://www.astrsomn.com/api/plugins/{pluginId}/versions/queryPage";

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
            if (Objects.isNull(resp) || CollectionUtils.isEmpty(resp.getList())) {
                return resp != null ? resp : PageResponse.empty();
            }
            markInstalledExtensions(resp.getList());
            fillInstalledVersionInfo(resp.getList());
            return resp;
        } catch (Exception e) {
            // 如果外部 API 调用失败，返回空分页结果
            return PageResponse.empty();
        }
    }

    private void markInstalledExtensions(List<ExtensionMarketplaceItemDTO> items) {
        Set<String> pluginIds = items.stream()
                .map(ExtensionMarketplaceItemDTO::getPluginId)
                .filter(k -> Objects.nonNull(k) && !k.isEmpty())
                .collect(Collectors.toSet());
        if (pluginIds.isEmpty()) return;

        // 拉取所有未删除的已安装扩展（数量不大，全量拉取做内存匹配）
        List<SystemExtensionEntity> allInstalled = systemExtensionService.list(
                new LambdaQueryWrapper<SystemExtensionEntity>()
                        .eq(SystemExtensionEntity::getDeleted, 0)
        );

        // 匹配方式 1：extensionKey 精确匹配 marketplace pluginId
        Set<String> matchedByKey = allInstalled.stream()
                .map(SystemExtensionEntity::getExtensionKey)
                .filter(Objects::nonNull)
                .filter(pluginIds::contains)
                .collect(Collectors.toSet());

        // 匹配方式 2：jarName 前缀匹配（marketplace 安装的 jarName 为 {pluginId}-{version}.jar）
        Map<String, String> jarNamePluginIdMap = new HashMap<>();
        for (SystemExtensionEntity ext : allInstalled) {
            String jarName = ext.getJarName();
            if (jarName == null || jarName.isEmpty()) continue;
            for (String pid : pluginIds) {
                if (jarName.startsWith(pid)) {
                    jarNamePluginIdMap.put(pid, ext.getExtensionKey());
                    break;
                }
            }
        }

        for (ExtensionMarketplaceItemDTO item : items) {
            String pid = item.getPluginId();
            if (Objects.isNull(pid)) continue;
            if (matchedByKey.contains(pid) || jarNamePluginIdMap.containsKey(pid)) {
                item.setInstalled(true);
            }
        }
    }

    private void fillInstalledVersionInfo(List<ExtensionMarketplaceItemDTO> items) {
        Set<String> pluginIds = items.stream()
                .map(ExtensionMarketplaceItemDTO::getPluginId)
                .filter(k -> Objects.nonNull(k) && !k.isEmpty())
                .collect(Collectors.toSet());
        if (pluginIds.isEmpty()) return;

        List<SystemExtensionEntity> allInstalled = systemExtensionService.list(
                new LambdaQueryWrapper<SystemExtensionEntity>()
                        .eq(SystemExtensionEntity::getDeleted, 0)
        );

        // 建立 pluginId → installedVersion 的映射（支持两种匹配方式）
        Map<String, String> installedVersionMap = new HashMap<>();

        for (SystemExtensionEntity ext : allInstalled) {
            String extKey = ext.getExtensionKey();
            String extVer = StringUtils.trimToNull(ext.getVersion());
            String jarName = ext.getJarName();
            if (extKey == null || extVer == null) continue;

            // 匹配方式 1：extensionKey 直接命中
            if (pluginIds.contains(extKey)) {
                installedVersionMap.put(extKey, extVer);
            }

            // 匹配方式 2：jarName 前缀匹配
            if (jarName != null && !jarName.isEmpty()) {
                for (String pid : pluginIds) {
                    if (jarName.startsWith(pid) && !installedVersionMap.containsKey(pid)) {
                        installedVersionMap.put(pid, extVer);
                        break;
                    }
                }
            }
        }

        for (ExtensionMarketplaceItemDTO item : items) {
            String pid = item.getPluginId();
            if (Objects.isNull(pid)) continue;

            String installedVer = installedVersionMap.get(pid);
            if (installedVer != null) {
                item.setInstalledVersion(installedVer);
            }
        }
    }

    @Override
    public PageResponse<ExtensionMarketplaceVersionDTO> listVersions(String pluginId, int pageNo, int pageSize) {
        String url = VERSIONS_URL.replace("{pluginId}", pluginId);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("pageNo", pageNo);
        requestBody.put("pageSize", pageSize);
        requestBody.put("param", new HashMap<>());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            PageResponse<ExtensionMarketplaceVersionDTO> resp = restTemplate.exchange(
                    url, HttpMethod.POST, entity,
                    new ParameterizedTypeReference<PageResponse<ExtensionMarketplaceVersionDTO>>() {})
                    .getBody();
            return Objects.nonNull(resp) ? resp : PageResponse.empty();
        } catch (Exception e) {
            return PageResponse.empty();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public BaseResponse<String> installExtension(String pluginId, String version) {
        try {
            // Step 1: 从市场获取版本详情，拿到真实下载地址
            String versionUrl = VERSION_DETAIL_URL.replace("{pluginId}", pluginId).replace("{version}", version);
            Map<String, Object> versionResp = restTemplate.getForObject(versionUrl, Map.class);
            if (versionResp == null || versionResp.get("data") == null) {
                return BaseResponse.fail("Version not found in marketplace", null);
            }

            Map<String, Object> versionData = (Map<String, Object>) versionResp.get("data");
            String downloadUrl = (String) versionData.get("resolvedDownloadUrl");
            if (!StringUtils.isNotBlank(downloadUrl)) {
                downloadUrl = (String) versionData.get("downloadUrl");
            }
            if (!StringUtils.isNotBlank(downloadUrl)) {
                return BaseResponse.fail("No download URL available for this version", null);
            }

            // Step 2: 直接从真实下载地址下载 jar
            byte[] jarBytes = restTemplate.getForObject(downloadUrl, byte[].class);
            if (jarBytes == null || jarBytes.length == 0) {
                return BaseResponse.fail("Plugin download failed: empty file", null);
            }

            // Step 3: 构造 MultipartFile 并安装
            String filename = pluginId + "-" + version + ".jar";
            MultipartFile multipartFile = new MultipartFile() {
                @Override
                public String getName() {
                    return filename;
                }

                @Override
                public String getOriginalFilename() {
                    return filename;
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

            return systemExtensionService.uploadJar(multipartFile);
        } catch (Exception e) {
            return BaseResponse.fail("Plugin installation failed: " + e.getMessage(), null);
        }
    }
}
