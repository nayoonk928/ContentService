package com.personal.contentservice.repository;

import com.personal.contentservice.document.Content;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ESContentRepository extends ElasticsearchRepository<Content, Long> {

  boolean existsByContentIdAndMediaType(Long contentId, String mediaType);

}
