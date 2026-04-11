package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.astrsomn.core.common.base.BaseEntity;


@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("AI_VEC_DRIVER")
@Builder
public class AiVecDriverEntity extends BaseEntity<Long> {

    @TableField("ID")
    private Long id;

    @TableField("DRIVER_NAME")
    private String driverName;

    @TableField("PROVIDER")
    private String provider;



    @TableField("PARAMS")
    private String params;

}
