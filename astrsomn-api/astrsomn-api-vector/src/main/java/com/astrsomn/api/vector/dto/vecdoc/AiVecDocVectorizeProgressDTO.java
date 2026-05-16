package com.astrsomn.api.vector.dto.vecdoc;

import lombok.Data;

import java.io.Serializable;

@Data
public class AiVecDocVectorizeProgressDTO implements Serializable {

    private String taskId;
    private String status;
    private Integer progress;
    private String message;
    private Integer totalSegments;
    private Integer doneSegments;
}
