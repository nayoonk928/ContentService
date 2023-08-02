package com.personal.contentservice.controller;

import com.personal.contentservice.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contents")
@RequiredArgsConstructor
public class ContentController {

  private final ContentService contentService;

  @GetMapping("/search/{query}/{page}")
  public ResponseEntity<?> searchContents(
      @PathVariable String query, @PathVariable int page
  ) throws Exception {
    return ResponseEntity.ok().body(contentService.searchContents(query, page));
  }
}