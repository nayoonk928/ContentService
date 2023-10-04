package com.personal.contentservice.controller;

import com.personal.contentservice.service.ContentSearchService;
import com.personal.contentservice.service.ContentService;
import com.personal.contentservice.service.impl.ESContentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contents")
public class ContentController {

  private final ContentSearchService contentSearchService;
  private final ContentService contentService;
  private final ESContentServiceImpl esContentService;

  @GetMapping("/search")
  public ResponseEntity<?> searchContents(
      @RequestParam("query") String query, @RequestParam("page") int page
  ) throws Exception {
    return ResponseEntity.ok().body(contentSearchService.searchContents(query, page));
  }

  @GetMapping("/save-all")
  public void saveAllContentsInfo() {
    esContentService.saveAllContentsInfo();
  }

}