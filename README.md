# Content Service
드라마, 영화와 같은 컨텐츠 정보 제공, 리뷰 및 평점, 추천 서비스를 제공하는 API

# 🧑‍💻Tech Stack
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> 
<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
<img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white">
<img src="https://img.shields.io/badge/elasticsearch-005571?style=for-the-badge&logo=elasticsearch&logoColor=white">

# 🗒️프로젝트 기능 및 설계
### 회원 측 기능
* [회원가입]
  * 이메일, 아이디, 비밀번호, 닉네임을 입력하여 회원가입합니다.
    * 이메일, 아이디, 닉네임은 유일해야 합니다.
    * 일반적으로 모든 사용자는 회원가입시 USER 권한(일반 권한)을 지닙니다.
* [로그인]
  * 사용자는 로그인을 할 수 있습니다. 
    * 회원가입때 사용한 아이디 및 패스워드가 일치해야합니다.
* [내 정보 수정]
    * 비밀번호 및 닉네임 변경 
* [회원탈퇴]
  * 회원은 계정을 삭제하여 탈퇴 할 수 있습니다.
    * 회원 탈퇴 시 작성한 리뷰와 평점은 모두 삭제됩니다.

### 컨텐츠 조회 기능
* 검색하지 않아도 평점을 기반으로 Top10 목록을 제공합니다.
* 사용자는 회원가입하지 않아도 리뷰나 평점을 볼 수 있습니다.
* [컨텐츠 검색]
  * 아래 기준으로 사용자는 컨텐츠를 검색할 수 있습니다.
    * 제목
    * 배우
    * 감독
    * 장르
* [컨텐츠 상세 정보 보기]
  * 컨텐츠 하나에 대한 상세정보를 얻을 수 있습니다. (api에서 가져옵니다)
    * 영화
      * 제목, 원제, 원어, 장르, 간단소개, 개요, 제작 국가, 개봉일, 상영시간
      * 감독 및 배우
    * TV 시리즈
      * 제목, 원제, 원어, 장르, 간단소개, 개요, 제작 국가, 최초 방영일, 마지막 방영일, 방송사, 에피소드 개수, 시즌 개수
      * 감독 및 배우
    * 배우는 상위 다섯 명만 보여줍니다.

### 보고싶어요 기능
* 회원이 컨텐츠를 '보고싶어요'하면 wishlist에 담깁니다.
* 회원이 wishlist를 조회하면 모든 보고싶어요한 컨텐츠의 제목이 나열됩니다.
* 회원이 컨텐츠의 '보고싶어요'를 취소한다면 wishlist에서 제외됩니다.

### 리뷰 및 평점 기능
* [자신의 리뷰 및 평점]
  * 회원은 리뷰 및 평점을 등록/수정/삭제 할 수 있습니다.
    * 리뷰 등록시 내용, 평점, 작성 날짜에 대한 정보가 필요합니다.
    * 수정 시 작성 날짜도 업데이트 됩니다.
* [다른 회원 리뷰]
  * 다른 회원의 리뷰를 신고/추천/비추천 할 수 있습니다.
    * 신고 시 신고 사유를 작성해야 합니다.
    * 이미 추천(비추천)한 리뷰를 또 추천(비추천) 한다면 추천(비추천)이 취소됩니다.
* [리뷰 보기]
  * 사용자는 컨텐츠의 상세정보를 조회시 리뷰도 볼 수 있습니다.
    * 리뷰는 추천순으로 정렬되며, 5개씩 페이징 처리하여 보여줍니다.

### 컨텐츠 추천 기능
* 회원의 컨텐츠 평점 + 보고싶어요한 작품의 장르를 기반으로 컨텐츠를 추천합니다.

### 관리자 측 기능
* [관리자로 승인 하기]
  * 초기 관리자는 일반 가입자를 관리자로 승격시킬 수 있습니다.
* [리뷰 삭제]
  * 신고가 15건 이상 들어온 리뷰에 대해 삭제할 수 있습니다.

# ERD
![erd](/doc/ERD.png)

# Trouble Shooting
[go to the trouble shooting section](doc/TROUBLE_SHOOTING.md)