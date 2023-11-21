package com.sjxm.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AccountDTO implements Serializable {

    private String accountId;

    private String password;

}
