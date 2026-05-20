package com.astrsomn.server.api.vector;

import com.astrsomn.api.vector.dto.vecfolder.AiVecFolderCreateRequestDTO;
import com.astrsomn.api.vector.dto.vecfolder.AiVecFolderQueryRequestDTO;
import com.astrsomn.api.vector.dto.vecfolder.AiVecFolderResponseDTO;
import com.astrsomn.api.vector.dto.vecfolder.AiVecFolderUpdateRequestDTO;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.server.service.vector.AiVecFolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/astro/ai-vec-folder")
@RequiredArgsConstructor
public class AiVecFolderController extends BaseController {

    private final AiVecFolderService aiVecFolderService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AiVecFolderCreateRequestDTO request) {
        return aiVecFolderService.create(request);
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AiVecFolderUpdateRequestDTO request) {
        return aiVecFolderService.update(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiVecFolderService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @GetMapping("/list")
    public BaseResponse<List<AiVecFolderResponseDTO>> list(
            @RequestParam("collectionId") Long collectionId,
            @RequestParam(value = "parentId", required = false) Long parentId) {
        AiVecFolderQueryRequestDTO param = new AiVecFolderQueryRequestDTO();
        param.setCollectionId(collectionId);
        param.setParentId(parentId);
        return BaseResponse.success(aiVecFolderService.queryList(param));
    }

    @PostMapping("/move-docs")
    public BaseResponse<String> moveDocs(@RequestBody MoveDocsRequest request) {
        return aiVecFolderService.moveDocs(request.getDocIds(), request.getFolderId());
    }

    public static class MoveDocsRequest {
        private long[] docIds;
        private Long folderId;

        public long[] getDocIds() { return docIds; }
        public void setDocIds(long[] docIds) { this.docIds = docIds; }
        public Long getFolderId() { return folderId; }
        public void setFolderId(Long folderId) { this.folderId = folderId; }
    }
}
