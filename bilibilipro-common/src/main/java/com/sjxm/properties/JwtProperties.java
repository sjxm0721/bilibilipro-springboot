package com.sjxm.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bilibilipro.jwt")
@Data
public class JwtProperties {
    /**
     * jwt令牌生成相关配置
     */
    private String secretKey;

    private long  ttl;

    private String tokenName;
}
