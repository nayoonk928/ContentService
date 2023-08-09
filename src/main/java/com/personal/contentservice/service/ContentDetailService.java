package com.personal.contentservice.service;

import com.personal.contentservice.dto.ContentDto;

public interface ContentDetailService {

  ContentDto getContentDetail(long id, String type) throws Exception;

}