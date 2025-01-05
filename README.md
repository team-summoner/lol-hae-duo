<p align="center"><img src="https://github.com/user-attachments/assets/b0e8519c-4520-43c8-8c3a-bc4fd46819d3" width="400"/></p>

# Lol-Hae-Duo 롤해듀오

롤(League of Legends) 유저들을 위한 듀오 매칭 및 전적 분석 서비스

요청 URL: http://lolhaeduo.site

<br>

## 📖 목차

1. 프로젝트 소개
2. 팀원 소개
3. 기술 스택
4. 실행 방법
5. 주요 기능
6. 문의
7. 참고 자료

<br>

## 🎤 프로젝트 소개

지긋지긋한 연패, 실버 4 LP 0점! 한번만 더 지면 브론즈로 강등이라니 ㅠㅠ

내 상황으로부터 날 구해줄 백마탄 버스기사님 어디 없을까??

게임을 혼자 즐기기 심심할 때, 같이 소통할 게임 친구가 필요할 때, 등을 맡기고 팀의 승리를 거머쥘 전우가 필요할 때!!

나만큼 게임 잘하는 사람 어디 없나? ***너, 내 동료가 돼라.***

날 웃음 짓게 만드는 버스기사님, 든든한 내 친구! **롤해 듀오**에서는 모두 만나볼 수 있습니다!

부담 갖지 말고 사용하세요!

<br>

## 팀원 소개

| 이름 | 블로그 주소 | 개인 이메일 |
|------|------------|--------------|
| [육심헌](https://github.com/yokxim2) | https://yokxim.tistory.com/ | yooksi53@gmail.com |
| [이은영](https://github.com/Eun-0) | https://1step2dream.tistory.com/ | shinely12@gmail.com |
| [백현욱](github.com/hubaek) | https://hubaek.tistory.com/ | hubaek0815@gmail.com |
| [이지훈](https://github.com/LEEJI-HOON1) | https://ezy-i.tistory.com/ | hoon0927@gmail.com |
| [박인선](https://github.com/inseonbak) | https://insight9145.tistory.com/ | dcba5824@gmail.com |

<br>

## 기술 스택

### 🛠️ Language & Server

<img src="https://img.shields.io/badge/intellij idea-000000?style=for-the-badge&logo=intellijidea&logoColor=white"> <br>
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> 
<img src="https://img.shields.io/badge/spring boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <br>
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> 
<img src="https://img.shields.io/badge/amazon rds-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white"> <br>
<img src="https://img.shields.io/badge/prometheus-E6522C?style=for-the-badge&logo=prometheus&logoColor=white"> 
<img src="https://img.shields.io/badge/grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white"> <br>
<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white"> 
<img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"> 
<img src="https://img.shields.io/badge/amazon ec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"> <br> 
<img src="https://img.shields.io/badge/riot games-EB0029?style=for-the-badge&logo=riotgames&logoColor=white">

### 사용한 기술
| 라이브러리 | 설명 |
|-----------|-------|
| RestTemplate | 외부 API 호출 |
| Spring Event | 비동기 이벤트 사용 |
| Spring Retry | 실패한 호출 재시도 |

### 🪄 Cowork Tools

<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"> <br>
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

## 🎯 주요 기능

### 1. 계정 연동(멤버당 최대 3개 계정 연동 가능)
- URL: http://lolhaeduo.site/link/riot  [POST]
   ```
   {
    "accountType": {{게임 타입}},
    "accountUsername": {{연동한 계정 ID}},
    "accountPassword": {{연동한 계정 비밀번호}},
    "summonerName": {{소환사명}},
    "tagLine": {{태그라인}},
    "server": {{서버}}
   }
   ```

### 2. 듀오 신청
- URL: http://lolhaeduo.site/duo  [POST]
   ```
   {
    "accountId": {{계정 ID}},
    "queueType": "{{게임 큐 타입}}",
    "primaryRole": "{{본인의 주 역할군}}",
    "primaryChamp": "{{본인의 주 챔피언}}",
    "secondaryRole": "{{본인의 부 역할군}}",
    "secondaryChamp": "{{본인의 부 챔피언}}",
    "targetRole": "{{원하는 듀오의 역할군}}",
    "memo": "{{메모}}",
    "mic": {{마이크 사용 여부}}
   }
   ```

### 3. 듀오 조회
- URL: http://lolhaeduo.site/duo   [GET]
   ```
   QueryParam 형식
   * queueType = {{게임 큐 타입}}
   * lane = {{라인}}
   * tier = {{티어}}
   * page = {{조회하려는 페이지 번호}}
   * size = {{한 페이지에 보이는 듀오 갯수}}
   ```

### 4. 듀오 수정
- URL: http://lolhaeduo.site/duo/{duoId}  [PUT]
   ```
   {
   "queueType": "{{게임 큐 타입}}",
    "primaryRole": "{{본인의 주 역할군}}",
    "targetRole": "{{원하는 듀오의 역할군}}",
    "memo": "{{메모}}",
    "mic": {{마이트 사용 여부}}
   }
   ```
  
### 5. 듀오 삭제
- URL: http://lolhaeduo.site/duo/{duoId}  [DELETE]

<br>

### 📜 API 문서
API 테스트를 위해 [Postman 워크스페이스]()를 활용하세요.

1. Postman에서 'Import' 버튼 클릭
2. 다운로드한 'lol-hae-duo_postman_workspace.json' 파일을 선택
3. 워크스페이스에서 API 테스트 진행

<br>

## 참고 자료

팀 노션 페이지 : [노션](https://www.notion.so/teamsparta/b953cd528364428a9c2fa6c5433efc38)

브로셔 노션 페이지 : [노션]()
