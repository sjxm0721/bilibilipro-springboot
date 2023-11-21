package com.sjxm.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountVO implements Serializable {

    private Long uid;

    private String accountId;

    private LocalDate birthday;

    private String avatar;

    private String accountName;

    private Integer followNum;

    private Integer fansNum;

    private Integer dynamicNum;

    private String accountBrief;

    private Integer clickNum;

    private Integer likeNum;
}
