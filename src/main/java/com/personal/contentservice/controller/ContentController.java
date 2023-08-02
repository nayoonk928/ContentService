package com.personal.contentservice.controller;

import com.personal.contentservice.service.ContentService;
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

  private final ContentService contentService;

  @GetMapping("/search")
  public ResponseEntity<?> searchContents(
      @RequestParam("query") String query, @RequestParam("page") int page
  ) throws Exception {
    return ResponseEntity.ok().body(contentService.searchContents(query, page));
  }

}