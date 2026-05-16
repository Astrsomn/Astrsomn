package com.astrsomn.api.vector.dto.vecsegment;

import lombok.Data;

import java.io.Serializable;

@Data
public class AiVecSegmentSearchRequestDTO implements Serializable {

    private Long collectionId;
    private String queryText;
    private Integer topK = 5;
    private Double minScore;
    private String envCode;
}
