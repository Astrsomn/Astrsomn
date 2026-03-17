package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.astrsomn.core.common.base.BaseEntity;

import java.time.LocalDateTime;

@Data
@TableName("AI_FILE")
public class AiFileEntity extends BaseEntity<Long> {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String fileUUID;

    private String fileName;

    private String fileType;

    private Long fileSize;

    private String filePath;

    private String fileUrl;

    private String storageType;

    private String bucketName;

    private String createBy;

    private String updateBy;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer status;

    private String remark;
}
