package com.personal.contentservice.service;

import java.util.List;

public interface ContentService {

  List<?> searchContents(String query, int page) throws Exception;

}