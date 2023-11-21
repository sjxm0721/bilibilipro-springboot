package com.sjxm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sjxm.dto.HistoryDTO;
import com.sjxm.result.PageResult;

public interface HistoryService {
    PageResult page(Long uid, Integer page, Integer pageSize) throws JsonProcessingException;

    void add(HistoryDTO historyDTO) throws JsonProcessingException;

    void transHistoryFromRedis2DB();
}
