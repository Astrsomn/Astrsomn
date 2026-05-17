package com.astrsomn.api.vector.dto.vecsegment;

import lombok.Data;

import java.io.Serializable;

@Data
public class AiVecSegmentSearchResultDTO implements Serializable {

    private Long segmentId;
    private Long docId;
    private String segmentContent;
    private Double score;
    private String metadataJson;
    private String originalFileName;
}
