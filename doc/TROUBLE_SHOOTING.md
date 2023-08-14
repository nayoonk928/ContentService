# Trouble Shooting
프로젝트를 진행하면서 발생한 문제점들과 해결법을 서술합니다.

## Json String to Entity Error
```
Caused by: com.fasterxml.jackson.databind.exc.MismatchedInputException:
Cannot construct instance of ContentDetailDto(although at least one Creator exists):
no String-argument constructor/factory method to deserialize from String value
```
Tmdb Api에서 받은 값을 db에 Json String 형으로 저장해두었는데 이것을 다시 Entity로 변환하면서
JSON 데이터를 String 값에서 역직렬화하기 위한 생성자나 메서드가 없다는 에러가 발생했습니다.

```java
@JsonCreator
public ContentDetailDto(String value) throws JsonProcessingException {
  ObjectMapper objectMapper = new ObjectMapper();   
  ContentDetailDto root = objectMapper.readValue(value, ContentDetailDto.class);

  this.originalTitle = root.getOriginalTitle();
  this.originalLanguage = root.getOriginalLanguage();
  ...
}
```

그래서 이렇게 직접 @JsonCreator 어노테이션을 활용하여 지정해주었더니 db에 저장된 값을 가져올 수 있었습니다.

## EmbeddedId
컨텐츠의 id는 같지만 mediaType이 다른 경우가 있어서 복합키를 사용해야 했습니다.
처음에는 단순하게 @Id 어노테이션을 각각의 필드에 붙이면 될거라고 생각했지만 에러가 발생하였고 @EmbeddedId 어노테이션에 
대해 알게 되었습니다.

@EmbeddedId 어노테이션을 사용하기 위해서는 @Embeddable 어노테이션이 붙어있는 객체가 필요하여 ContentKey 객체를 생성하였고,
이것을 Content의 PK로 사용하였습니다.
```java
public class Content {

  @EmbeddedId
  private ContentKey contentKey;

}
```

```java
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
```

## 연관관계로 인해 리뷰 삭제 불가능
```
Caused by: org.hibernate.exception.ConstraintViolationException: could not execute statement [Cannot delete or update a parent row: a foreign key constraint fails (`content`.`report`, CONSTRAINT `FKmcui10qh03nnf6h3glch6pvmy` FOREIGN KEY (`review_id`) REFERENCES `review` (`id`))] [delete from review where id=?]
```
추천/비추천과 신고가 있는 리뷰를 삭제하려고 하였더니 당연하게도 위의 에러가 발생하였습니다.

```java
@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
private List<ReviewReaction> reactions;

@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
private List<ReviewReport> reports;
```
@OneToMany 어노테이션을 이용해 관련 데이터를 연결하고, CasecadeType.ALL로 설정하여 부모 엔티티의 모든 변경과 삭제가
자식 엔티티에 전파되도록 하였고, orphanRemoval = truel로 설정하여 부모 엔티티에서 자식 엔티티가 제거될 때, 
해당 자식 엔티티를 데이터베이스에서도 삭제하도록 하였습니다.