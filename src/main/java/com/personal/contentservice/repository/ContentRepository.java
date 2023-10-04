package com.personal.contentservice.repository;

import com.personal.contentservice.domain.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long>  {

  Content findByContentKey_IdAndContentKey_MediaType(long id, String mediaType);

}