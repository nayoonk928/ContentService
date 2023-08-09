package com.personal.contentservice.service;

import com.personal.contentservice.domain.Content;

public interface ContentDetailService {

  Content getContentDetail(long id, String type) throws Exception;

}