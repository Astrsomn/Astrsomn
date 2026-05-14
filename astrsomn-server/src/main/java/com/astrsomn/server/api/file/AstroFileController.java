package com.astrsomn.server.api.file;

import com.astrsomn.api.storage.dto.AstFileUploadResponseDTO;
import com.astrsomn.api.storage.entity.AstFileRecordEntity;
import com.astrsomn.api.storage.exception.AstFileErrorEnum;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.internal.storage.service.AstrsomnStorageClient;
import com.astrsomn.internal.storage.service.model.StorageDownloadRequest;
import com.astrsomn.server.service.AstroFileRecordService;
import com.astrsomn.server.service.AstroFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public BaseResponse<AstFileUploadResponseDTO> upload(@RequestParam("file") MultipartFile file,
                                                         @RequestParam(value = "bizType", required = false) String bizType,
                                                         @RequestParam(value = "bizId", required = false) String bizId) {
        return astroFileService.upload(file, bizType, bizId);
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<byte[]> view(@PathVariable("id") Long id) throws IOException {
        AstFileRecordEntity fileRow = astroFileRecordService.getById(id);
        if (fileRow == null || StringUtils.isBlank(fileRow.getObjectKey())) {
            throw new BusinessException(AstFileErrorEnum.FILE_NOT_FOUND);
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
