package com.personal.contentservice.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Data;

@Data
@Embeddable
public class ContentKey implements Serializable {

  private long id;
  private String mediaType;

  public void setIdAndMediaType(long id, String mediaType) {
    this.id = id;
    this.mediaType = mediaType;
  }

}
