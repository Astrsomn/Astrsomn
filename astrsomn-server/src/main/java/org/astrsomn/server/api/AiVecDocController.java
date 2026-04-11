package org.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseController;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.vecdoc.AiVecDocCreateRequestDTO;
import org.astrsomn.core.common.dto.vecdoc.AiVecDocQueryRequestDTO;
import org.astrsomn.core.common.dto.vecdoc.AiVecDocResponseDTO;
import org.astrsomn.core.common.dto.vecdoc.AiVecDocUpdateRequestDTO;
import org.astrsomn.server.service.AiVecDocService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/v1/astro/ai-vec-doc")
@RequiredArgsConstructor
public class AiVecDocController extends BaseController {

    private final AiVecDocService aiVecDocService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AiVecDocCreateRequestDTO request) {
        return aiVecDocService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiVecDocService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AiVecDocUpdateRequestDTO request) {
        return aiVecDocService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AiVecDocResponseDTO> queryPage(@RequestBody BasePageRequest<AiVecDocQueryRequestDTO> request) {
        return aiVecDocService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AiVecDocResponseDTO> detail(@RequestParam("id") Long id) {
        return aiVecDocService.detail(id);
    }

    @PostMapping("/upload")
    public BaseResponse<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return BaseResponse.fail("文件为空", null);
        }

        try {
            // 生成日期+UUID的文件名
            String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String uuid = UUID.randomUUID().toString().replace("-", "");
            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf('.')) : "";
            String fileName = dateStr + "_" + uuid + extension;

            // 上传目录路径
            String uploadDir = System.getProperty("user.dir") + "/vec/document/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 保存文件
            File dest = new File(uploadDir + fileName);
            file.transferTo(dest);

            // 生成相对链接
            String relativePath = "/vec/document/" + fileName;
            return BaseResponse.success(relativePath);
        } catch (IOException e) {
            e.printStackTrace();
            return BaseResponse.fail("文件上传失败", null);
        }
    }
}
