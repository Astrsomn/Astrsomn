package com.astrsomn.server.service.extension.base;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.api.runtime.common.dto.extension.SystemExtensionCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.extension.SystemExtensionQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.extension.SystemExtensionResponseDTO;
import com.astrsomn.api.runtime.common.dto.extension.SystemExtensionUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.SystemExtensionEntity;
import org.springframework.web.multipart.MultipartFile;

public interface SystemExtensionService extends IService<SystemExtensionEntity> {

    BaseResponse<String> create(SystemExtensionCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<SystemExtensionResponseDTO> detail(Long id);

    BaseResponse<String> update(SystemExtensionUpdateRequestDTO request);

    PageResponse<SystemExtensionResponseDTO> queryPage(BasePageRequest<SystemExtensionQueryRequestDTO> request);

    BaseResponse<String> apply(Long id);

    /**
     * 取消应用：从运行时卸载插件 jar，状态恢复为「已安装」。
     */
    BaseResponse<String> revokeApply(Long id);

    /**
     * 卸载：校验通过后从运行时卸载 jar、物理删除 {@code SYSTEM_EXTENSION} 行，并尝试删除 {@code plugins} 下对应 jar。
     */
    BaseResponse<String> uninstall(Long id);

    /**
     * 将 jar 保存到插件目录并写入 {@code SYSTEM_EXTENSION}（状态与「从市场安装」一致：已安装、未应用）。
     */
    BaseResponse<String> uploadJar(MultipartFile file);
}
