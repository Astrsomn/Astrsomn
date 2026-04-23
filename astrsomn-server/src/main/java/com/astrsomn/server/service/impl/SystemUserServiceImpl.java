package com.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import com.astrsomn.core.common.base.BasePageRequest;
import com.astrsomn.core.common.base.BaseResponse;
import com.astrsomn.core.common.base.PageResponse;
import com.astrsomn.core.common.constant.SystemUserEnum.AdminEnum;
import com.astrsomn.core.common.constant.SystemUserEnum.UserRoleEnum;
import com.astrsomn.core.common.dto.user.SystemUserCreateRequestDTO;
import com.astrsomn.core.common.dto.user.SystemUserQueryRequestDTO;
import com.astrsomn.core.common.dto.user.SystemUserResponseDTO;
import com.astrsomn.core.common.dto.user.SystemUserUpdateRequestDTO;
import com.astrsomn.core.common.entity.SystemUserEntity;
import com.astrsomn.core.common.utils.StringUtils;
import com.astrsomn.core.exception.base.BusinessException;
import com.astrsomn.core.exception.constant.SystemUserErrorEnum;
import com.astrsomn.core.mapper.SystemUserMapper;
import com.astrsomn.server.service.SystemUserService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUserEntity> implements SystemUserService {

    private final QueryEnvParamHelper queryEnvParamHelper;
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    @Override
    public BaseResponse<String> create(SystemUserCreateRequestDTO request) {
        SystemUserEntity entity = new SystemUserEntity();
        BeanUtils.copyProperties(request, entity);
        applyUserRole(entity);
        if (StringUtils.isNotBlank(entity.getPassword())) {
            entity.setPassword(PASSWORD_ENCODER.encode(entity.getPassword()));
        }
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(SystemUserErrorEnum.USER_CREATE_FAILED);
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) {
            throw new BusinessException(SystemUserErrorEnum.USER_DELETE_FAILED);
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<SystemUserResponseDTO> detail(Long id) {
        SystemUserEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(SystemUserErrorEnum.USER_NOT_FOUND);
        }
        SystemUserResponseDTO responseDTO = new SystemUserResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> update(SystemUserUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(SystemUserErrorEnum.USER_PARAM_ERROR);
        }
        SystemUserEntity entity = new SystemUserEntity();
        BeanUtils.copyProperties(request, entity);
        applyUserRole(entity);
        SystemUserEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(SystemUserErrorEnum.USER_NOT_FOUND);
        }
        if (StringUtils.isBlank(entity.getPassword())) {
            entity.setPassword(existing.getPassword());
        } else {
            String p = entity.getPassword();
            if (!p.startsWith("$2a$") && !p.startsWith("$2b$") && !p.startsWith("$2y$")) {
                entity.setPassword(PASSWORD_ENCODER.encode(p));
            }
        }
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(SystemUserErrorEnum.USER_UPDATE_FAILED);
        }
        return BaseResponse.success("更新成功");
    }

    /**
     * 统一角色与 ADMIN_FLAG：管理员类（超管、环境管理员）为 Y，普通用户为 N。
     */
    private static void applyUserRole(SystemUserEntity entity) {
        UserRoleEnum role = UserRoleEnum.fromCode(entity.getUserRole());
        entity.setUserRole(role.getCode());
        entity.setAdminFlag(role == UserRoleEnum.USER ? AdminEnum.NO.getCode() : AdminEnum.YES.getCode());
    }

    @Override
    public PageResponse<SystemUserResponseDTO> queryPage(BasePageRequest<SystemUserQueryRequestDTO> request) {
        IPage<SystemUserResponseDTO> page = request.buildPage();
        SystemUserQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new SystemUserQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<SystemUserResponseDTO> result = baseMapper.queryPage(page, param);
        return PageResponse.buildResponse(result);
    }
}
