package com.astrsomn.server.api.file;

import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.service.AstroFileRecordService;
import com.astrsomn.server.service.AstroFileService;
import com.astrsomn.api.storage.entity.AstroFileRecordEntity;
import com.astrsomn.api.storage.exception.AstroFileErrorEnum;
import com.astrsomn.api.storage.dto.AstroFileUploadResponseDTO;
import com.astrsomn.internal.storage.service.AstrsomnStorageClient;
import com.astrsomn.internal.storage.service.model.StorageDownloadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/v1/astro/file")
@RequiredArgsConstructor
public class AstroFileController extends BaseController {

    private final AstroFileService astroFileService;
    private final AstroFileRecordService astroFileRecordService;
    private final AstrsomnStorageClient astrsomnStorageClient;

    @PostMapping("/upload")
    public BaseResponse<AstroFileUploadResponseDTO> upload(@RequestParam("file") MultipartFile file,
                                                            @RequestParam(value = "bizType", required = false) String bizType,
                                                            @RequestParam(value = "bizId", required = false) String bizId) {
        return astroFileService.upload(file, bizType, bizId);
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<byte[]> view(@PathVariable("id") Long id) throws IOException {
        AstroFileRecordEntity fileRow = astroFileRecordService.getById(id);
        if (fileRow == null || StringUtils.isBlank(fileRow.getObjectKey())) {
            throw new BusinessException(AstroFileErrorEnum.FILE_NOT_FOUND);
        }

        byte[] body;
        try (InputStream inputStream = astrsomnStorageClient.openInputStream(StorageDownloadRequest.builder()
                .platform(fileRow.getPlatform())
                .objectKey(fileRow.getObjectKey())
                .build())) {
            body = inputStream.readAllBytes();
        }

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (StringUtils.isNotBlank(fileRow.getMimeType())) {
            try {
                mediaType = MediaType.parseMediaType(fileRow.getMimeType());
            } catch (IllegalArgumentException ignore) {
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
            }
        }
        String filename = StringUtils.isNotBlank(fileRow.getOriginName()) ? fileRow.getOriginName() : "file";

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename*=UTF-8''" + java.net.URLEncoder.encode(filename, StandardCharsets.UTF_8))
                .body(body);
    }
}
