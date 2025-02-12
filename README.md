<p align="center"><a href='https://ifh.cc/v-jHT8cF' target='_blank'><img src='https://ifh.cc/g/jHT8cF.webp'width="400"/></p>

# Lol-Hae-Duo 롤해듀오

롤(League of Legends) 유저들을 위한 듀오 매칭 및 전적 분석 서비스

요청 URL: http://lolhaeduo.site

<현재는 서비스 이용이 중단되었습니다>

<br>

## 📖 목차

1. [프로젝트 소개](#프로젝트-소개)
2. [팀원 소개](#팀원-소개)
3. [기술 스택](#기술-스택)
4. [실행 방법](#실행-방법)
5. [주요 기능](#주요-기능)

<br>

##  프로젝트 소개

<H4>
프로젝트 기간  (2024/12/02 ~ 2025/01/07)
</H4>
지긋지긋한 연패, 실버 4 LP 0점! 한번만 더 지면 브론즈로 강등이라니 ㅠㅠ

내 상황으로부터 날 구해줄 백마탄 버스기사님 어디 없을까??

게임을 혼자 즐기기 심심할 때, 같이 소통할 게임 친구가 필요할 때, 등을 맡기고 팀의 승리를 거머쥘 전우가 필요할 때!!

나만큼 게임 잘하는 사람 어디 없나? ***너, 내 동료가 돼라.***

날 웃음 짓게 만드는 버스기사님, 든든한 내 친구! **롤해 듀오**에서는 모두 만나볼 수 있습니다!

부담 갖지 말고 사용하세요!

<br>

팀 노션 페이지 : [노션](https://teamsparta.notion.site/17b2dc3ef51481e59c67f9c62e2da5fa)

브로셔 노션 페이지 : [노션](https://hubaek.notion.site/3-1723619d0e47815285c7f9ce371dc559?pvs=4)

<br>

## 팀원 소개


|                                        Backend                                       |                  Backend                  |                          Backend                         |                         Backend                         |                  Backend                   |
|:------------------------------------------------------------------------------------:|:-----------------------------------------:|:--------------------------------------------------------:|:-------------------------------------------------------:|:------------------------------------------:|
|![](https://i.postimg.cc/wjvLxZwK/RUID1213a1b32c4d4e2db5fae4628027f47c.jpg?size=120) | ![](https://ifh.cc/g/65d0vS.png?size=120) | ![](https://i.postimg.cc/vZN94hK4/IMG-0190.jpg?size=120) | ![](https://i.postimg.cc/pXkKHRYY/image-1.png?size=120) | ![](https://i.postimg.cc/c1RQFQvz/1702114078751.jpg?size=120) |
| [육심헌](https://github.com/yokxim2) |      [이은영](https://github.com/Eun-0)      |[백현욱](github.com/hubaek)| [이지훈](https://github.com/LEEJI-HOON1) | [박인선](https://github.com/inseonbak) |
| [블로그](https://yokxim.tistory.com/) |  [블로그](https://1step2dream.tistory.com)   | [블로그](https://hubaek.tistory.com/) | [블로그](https://ezy-i.tistory.com/) |  [블로그](https://insight9145.tistory.com/) |
<br>

|   팀원   | <center>역할</center> |
|:--------:|:-----------|
| 육심헌 | - 프로젝트 방향성 지도, 계획 수립, 전체 총괄 <br> - 외부 API 호출 Client 코드 작성(RestTemplate, 데이터 가공 로직) <br> - Spring Event를 사용한 비동기 로직 작성, 스레드 풀 조정 <br> - Lambda Function을 사용해 대체 서버 생성 및 운용 <br> - Spring Retry를 사용해서 메서드 재처리 로직 도입 |
| 이은영 | - Postman을 이용한 API 문서화, 시스템 아키텍처 설계 <br> - 커스텀 필터를 사용한 JWT 기반 인증/인가 처리 <br> - 비동기 처리 과정에서 thread pool 조정 <br> - RestTemplate을 이용한 Riot API 호출에 대한 커스텀 매트릭 등록 <br> - Prometheus를 이용한 매트릭 수집 및 Grafana를 이용한 매트릭 시각화 <br> - 모니터링 서버 구축 |
| 백현욱 | - Riot API 연동, 랭크 전적 데이터 및 프로필 아이콘 정보 수집 <br> - 가비아를 통해 프로젝트 도메인 구매 <br> - 구매한 도메인을 AWS EC2와 연결하기 위해 DNS 설정 구성 |
| 이지훈 | - 듀오 조회 리스트 API 코드 작성 <br> - 동기/비동기 개념 학습 <br> - block/non-block 개념 학습 <br> - AWS, EC2 개념 학습 <br> - 기타 프로젝트 부족한 부분 조사 및 학습 |
| 박인선 | - Amazon SQS 개념 학습 <br> - 단위시간 내 API 호출 횟수 제한: Resilience4J 라이브러리의 TimeLimiter 개념 학습 |

<br>

## 기술 스택

### 🛠️ Language & Server

<img src="https://img.shields.io/badge/intellij idea-000000?style=for-the-badge&logo=intellijidea&logoColor=white"> <br>
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> 
<img src="https://img.shields.io/badge/spring boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <br>
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> 
<img src="https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white"> <br>
<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white"> 
<img src="https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens"><br>
<img src="https://img.shields.io/badge/prometheus-E6522C?style=for-the-badge&logo=prometheus&logoColor=white"> 
<img src="https://img.shields.io/badge/grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white"> <br>
<img src="https://img.shields.io/badge/amazon ec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white">
<img src="https://img.shields.io/badge/amazon rds-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white">
<img src="https://img.shields.io/badge/amazon route53-6633FF?style=for-the-badge&logo=amazonroute53&logoColor=white"><br>
<img src="https://img.shields.io/badge/riot games-EB0029?style=for-the-badge&logo=riotgames&logoColor=white">

### 사용한 기술
| 라이브러리 | 설명 |
|-----------|-------|
| RestTemplate | 외부 API 호출 |
| Spring Event | 비동기 이벤트 사용 |
| Spring Retry | 실패한 호출 재시도 |

### 🪄 Cowork Tools

<img src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white"> <br>
<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"> <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"> <br>
<img src="https://img.shields.io/badge/notion-000000?style=or-the-badge&logo=notion&logoColor=white"> <img src="https://img.shields.io/badge/Slack-FE5196?style=or-the-badge&logo=slack&logoColor=white">
<br>
<hr/>

## 시스템 아키텍처
&emsp; <img src="https://github.com/user-attachments/assets/8c6bdf79-44ea-4fdf-a44f-971686b2961f" width=1200/>

<br> 

## 실행 방법

### 외부 API 키
> 라이엇 개발자 포털에서는 API Key를 지급합니다. 라이엇 서버에서 제공하는 실제 데이터를 가져오기 위해서는 이 Key가 필요합니다.
> API Key 지급 방법
>> 라이엇 개발자 포털에 접속한다. [라이엇 개발자 포털](https://developer.riotgames.com/)

>> ![image](https://github.com/user-attachments/assets/4af7ba85-bde9-425a-b065-c3fc8dc08fd8)
>> 로봇이 아닙니다 인증 후 **Regenerate API Key**를 눌러서 Development API Key를 지급받는다.

>> 추가로 **Register Product**를 눌러서 더 높은 수준의 API Key를 받을 수도 있다. (자세한 획득 방법은 생략합니다.)
>> ![image](https://github.com/user-attachments/assets/aacb0f04-ea8a-4fa2-b178-9f6e5f55517f)

>> Development API Key는 개발 목적의 API Key로 호출 제한은 다음과 같고, 만료 시간은 24시간이다.
>> ![image](https://github.com/user-attachments/assets/2f4ca34b-910d-4ccb-830a-b05908fd232d)

>> Personal API Key는 간단한 프로젝트에 적합한 API Key로, 호출 제한은 Development API Key와 같고, 만료 시간은 없다.

>> Production API Key는 복잡한 오픈소스용 프로젝트를 위한 API Key로, 호출 제한은 다음과 같고, 만료 시간은 없다.
>> ![image](https://github.com/user-attachments/assets/52295c52-196d-430e-90cf-fe24aed12e29)

<br>

##  주요 기능

### 1. 계정 연동(멤버당 최대 3개 계정 연동 가능)
- URL: http://lolhaeduo.site/link/riot  [POST]

RequestBody
   ```JSON
{
  "accountType": "LOL",
  "accountUsername": "summoner123",
  "accountPassword": "password123",
  "summonerName": "바텀개",
  "tagLine": "#KR1",
  "server": "KR"
}

   ```
ResponseBody
   ```JSON
{
  "accountId": 101
}

```
### 2. 듀오 신청
- URL: http://lolhaeduo.site/duo  [POST]

RequestBody
   ```JSON
{
  "accountId": 11,
  "queueType": "RANKED_SOLO_5x5",
  "primaryRole": "MID",
  "primaryChamp": "Ahri",
  "secondaryRole": "TOP",
  "secondaryChamp": "Yasuo",
  "targetRole": "JUNGLE",
  "memo": "듀오 구해요",
  "mic": true
}

   ```
ResponseBody
   ```JSON
{
  "id": 123,
  "queueType": "RANKED_SOLO_5x5",
  "primaryRole": "MID",
  "primaryChamp": "Ahri",
  "secondaryRole": "TOP",
  "secondaryChamp": "Yasuo",
  "targetRole": "JUNGLE",
  "memo": "듀오 구해요",
  "mic": true,
  "tier": "Diamond",
  "ranks": "II",
  "kda": {
    "kills": 8,
    "deaths": 2,
    "assists": 10
  },
  "favoriteId": [1, 101, 117],
  "wins": 50,
  "losses": 30,
  "profileIcon": "https://example.com/icon.png",
  "memberId": 2001,
  "accountId": 101,
  "winRate": 63
}


   ```


### 3. 듀오 조회
- URL: http://lolhaeduo.site/duo   [GET] ㅌ

ResponseBody

   ```JSON
{
  "contents": [
    {
      "id": 123,
      "profileIconId": "12345",
      "summonerName": "바텀개",
      "tagLine": "#KR1",
      "queueType": "RANKED_SOLO_5x5",
      "tier": "Diamond",
      "rank": "II",
      "winRate": 63,
      "kda": {
        "kills": 8,
        "deaths": 2,
        "assists": 10
      },
      "favorites": [
        "Ahri",
        "Yasuo",
        "Thresh"
      ],
      "primaryRole": "MID",
      "targetRole": "JUNGLE",
      "primaryChamp": "Ahri",
      "secondaryRole": "TOP",
      "secondaryChamp": "Yasuo",
      "memo": "듀오 하실분",
      "mic": true,
      "memberId": 2001,
      "accountId": 101,
      "relativeTime": "2 hours ago"
    }
  ]
}
   ```

### 4. 듀오 수정
- URL: http://lolhaeduo.site/duo/{duoId}  [PUT]

RequestBody
   ```JSON
{
  "queueType": "RANKED_SOLO_5x5",
  "primaryRole": "TOP",
  "primaryChamp": "Ahri",
  "secondaryRole": "MID",
  "secondaryChamp": "Yasuo",
  "targetRole": "JUNGLE",
  "memo": "듀오 안해요",
  "mic": true
}

   ```
ResponseBody
   ```JSON
   {
  "duoId": 123,
  "queueType": "RANKED_SOLO_5x5",
  "primaryRole": "TOP",
  "primaryChamp": "Ahri",
  "secondaryRole": "MID",
  "secondaryChamp": "Yasuo",
  "targetRole": "JUNGLE",
  "memo": "듀오 안해요.",
  "mic": true,
  "tier": "Diamond",
  "ranks": "II",
  "wins": 52,
  "losses": 30,
  "favoritesChamp": [1, 101, 117],
  "profileIcon": "https://example.com/icon.png",
  "kda": {
    "kills": 8,
    "deaths": 2,
    "assists": 10
  },
  "memberId": 2001,
  "accountId": 101,
  "createdAt": "2025-01-06T10:00:00",
  "modifiedAt": "2025-01-06T12:00:00"
}

   ```

### 5. 듀오 삭제
- URL: http://lolhaeduo.site/duo/{duoId}  [DELETE]

<br>

### 📜 API 문서
API 테스트를 위해 [Postman 워크스페이스](https://github.com/user-attachments/files/18320326/postman_collection.json)를 활용하세요.

1. Postman에서 'Import' 버튼 클릭
2. 다운로드한 'lol-hae-duo_postman_workspace.json' 파일을 선택
3. 워크스페이스에서 API 테스트 진행
