package com.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.astrsomn.core.common.base.BaseEntity;


@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("AI_VEC_DRIVER")
@Builder
public class AiVecDriverEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField("DRIVER_NAME")
    private String driverName;

    @TableField("PROVIDER")
    private String provider;


    @TableField("PARAMS")
    private String params;

    @TableField("STATUS")
    private String status;
}
