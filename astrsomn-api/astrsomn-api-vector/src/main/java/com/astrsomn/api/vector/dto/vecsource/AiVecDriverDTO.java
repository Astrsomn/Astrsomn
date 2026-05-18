package com.astrsomn.api.vector.dto.vecsource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiVecDriverDTO {

    private String extensionKey;

    private String driverName;

    private String provider;

    private String version;

    private String author;

    
    private String source;
}
