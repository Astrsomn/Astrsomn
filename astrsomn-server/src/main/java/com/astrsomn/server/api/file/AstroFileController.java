package com.astrsomn.server.api.file;

import com.astrsomn.commn.base.BaseController;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.server.service.AstroFileService;
import com.astrsomn.storage.core.dto.AstroFileUploadResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/astro/file")
@RequiredArgsConstructor
public class AstroFileController extends BaseController {

    private final AstroFileService astroFileService;

    @PostMapping("/upload")
    public BaseResponse<AstroFileUploadResponseDTO> upload(@RequestParam("file") MultipartFile file,
                                                            @RequestParam(value = "bizType", required = false) String bizType,
                                                            @RequestParam(value = "bizId", required = false) String bizId) {
        return astroFileService.upload(file, bizType, bizId);
    }
}
