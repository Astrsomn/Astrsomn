package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.astrsomn.core.common.base.BaseEntity;

@Data
@TableName("AI_ACCOUNT")
public class AiAccountEntity extends BaseEntity<Long> {

    @TableField("ACCOUNT_KEY")
    private String accountKey;


    @TableField("ACCOUNT_NAME")
    private String accountName;

    @TableField("API_KEY")
    private String apiKey;

    @TableField("API_SECRET")
    private String apiSecret;

    @TableField("ACCOUNT_TOKENS")
    private Long accountTokens;
}
