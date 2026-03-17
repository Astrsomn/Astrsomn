package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.astrsomn.core.common.base.BaseEntity;

@Data
@TableName("SYSTEM_USER")
public class SystemUser extends BaseEntity<Long> {


    private String username;


    private String password;

}
