package com.personal.contentservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ESContentServiceImplTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void testSaveAllContentInfo_Success() {
    ResponseEntity<String> response = restTemplate.getForEntity(
        "http://localhost:" + port + "/contents/save-all", String.class);

    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  void testSaveDailyContentInfo_Success() {
    ResponseEntity<String> response = restTemplate.getForEntity(
        "http://localhost:" + port + "/contents/save-daily", String.class);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals("오늘의 컨텐츠가 성공적으로 저장되었습니다", response.getBody());
  }

}