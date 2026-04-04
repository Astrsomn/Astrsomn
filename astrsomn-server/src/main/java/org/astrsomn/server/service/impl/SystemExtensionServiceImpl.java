package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.extension.SystemExtensionCreateRequestDTO;
import org.astrsomn.core.common.dto.extension.SystemExtensionQueryRequestDTO;
import org.astrsomn.core.common.dto.extension.SystemExtensionResponseDTO;
import org.astrsomn.core.common.dto.extension.SystemExtensionUpdateRequestDTO;
import org.astrsomn.core.common.entity.SystemExtensionEntity;
import org.astrsomn.core.mapper.SystemExtensionMapper;
import org.astrsomn.server.service.SystemExtensionService;
import org.astrsomn.server.service.support.QueryEnvParamHelper;
import org.astrsomn.server.service.support.SystemExtensionModelGuard;
import org.astrsomn.starter.plugin.AstrsomnPluginManager;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class SystemExtensionServiceImpl extends ServiceImpl<SystemExtensionMapper, SystemExtensionEntity>
        implements SystemExtensionService {

    private final QueryEnvParamHelper queryEnvParamHelper;
    private final AstrsomnPluginManager pluginManager;
    private final SystemExtensionModelGuard systemExtensionModelGuard;

    @Override
    public BaseResponse<String> create(SystemExtensionCreateRequestDTO request) {
        SystemExtensionEntity entity = new SystemExtensionEntity();
        BeanUtils.copyProperties(request, entity);
        if (StringUtils.isBlank(entity.getStatus())) {
            entity.setStatus("INSTALLED");
        }
        if (StringUtils.isBlank(entity.getApplied())) {
            entity.setApplied("N");
        }
        boolean result = save(entity);
        return result ? BaseResponse.success("安装成功，待应用") : BaseResponse.fail("安装失败", null);
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        return result ? BaseResponse.success("删除成功") : BaseResponse.fail("删除失败", null);
    }

    @Override
    public BaseResponse<SystemExtensionResponseDTO> detail(Long id) {
        SystemExtensionEntity entity = getById(id);
        if (entity == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        SystemExtensionResponseDTO responseDTO = new SystemExtensionResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> update(SystemExtensionUpdateRequestDTO request) {
        SystemExtensionEntity entity = new SystemExtensionEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        return result ? BaseResponse.success("更新成功") : BaseResponse.fail("更新失败", null);
    }

    @Override
    public PageResponse<SystemExtensionResponseDTO> queryPage(BasePageRequest<SystemExtensionQueryRequestDTO> request) {
        IPage<SystemExtensionResponseDTO> page = request.buildPage();
        SystemExtensionQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new SystemExtensionQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<SystemExtensionResponseDTO> result = baseMapper.queryPage(page, param);
        return PageResponse.buildResponse(result);
    }

    @Override
    public BaseResponse<String> apply(Long id) {
        SystemExtensionEntity entity = getById(id);
        if (entity == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        if (StringUtils.isBlank(entity.getJarName())) {
            return BaseResponse.fail("未配置 jarName，无法应用插件", null);
        }
        try {
            pluginManager.applyPlugin(entity.getJarName());
            entity.setApplied("Y");
            entity.setStatus("APPLIED");
            updateById(entity);
            return BaseResponse.success("应用成功");
        } catch (Exception e) {
            return BaseResponse.fail("应用失败: " + e.getMessage(), null);
        }
    }

    @Override
    public BaseResponse<String> uninstall(Long id) {
        SystemExtensionEntity entity = getById(id);
        if (entity == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        if (SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER.getCode().equals(entity.getType())) {
            BaseResponse<Void> guard = systemExtensionModelGuard.assertNoInstancesUseProviderModels(id);
            if (!guard.isSuccess()) {
                return BaseResponse.fail(guard.getMessage(), null);
            }
        }
        if (StringUtils.isNotBlank(entity.getJarName())) {
            pluginManager.unloadPlugin(entity.getJarName());
        }
        entity.setApplied("N");
        entity.setStatus("UNINSTALLED");
        boolean result = updateById(entity);
        return result ? BaseResponse.success("卸载成功") : BaseResponse.fail("卸载失败", null);
    }
}
