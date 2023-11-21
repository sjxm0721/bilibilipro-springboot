package com.sjxm.entity;

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
public class Account implements Serializable {
    private Long uid;

    private String accountId;

    private String password;

    private LocalDate birthday;

    private Integer followNum;

    private Integer fansNum;

    private Integer dynamicNum;

    private String accountBrief;

    private String avatar;

    private String accountName;

    private Integer clickNum;

    private Integer likeNum;

}
