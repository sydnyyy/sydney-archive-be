## WebSocket 관리

- 분산 서버 가정
- 30분간 사용자 접근 없으면 웹소켓 종료 프로세스 실행

### 관련 데이터
- Redis에서 signal key, main key 관리
  - main key에 session id SET으로 관리
- 로컬에서 WebSocketSession 객체 자체 목록 관리
- client id 기준 접근 시각 관리 (접근 로그 API로 업데이트)

### 웹소켓 연결 종료 프로세스 설계

- [x] signal key 만료 브로드캐스트
  - 모든 서버 pub/sub으로 signal key TTL 만료 감지
    - 종료 다이얼로그의 빠른 출력을 위해 pub/sub 선택
  - 이후 사용자에게 종료 다이얼로그 출력 (종료 or 유지)
  - 유지 선택 시 signal key 재생성 및 main key TTL 연장
  - 종료 선택 시 종료 프로세스 실행

- [x] 종료 프로세스 시작
  - 사용자로부터 종료 응답을 받은 서버는 종료 체킹 ZSET에 client id 추가 후 stream을 사용해 모든 서버로 종료 명령 전달
    - ZSET 종료 timestamp 기준 정렬
  - 서버는 종료 명령을 읽고 로컬 자체 목록에서 client에 연결된 모든 웹소켓 세션 강제 종료 및 redis main key에 연결된 session id 제거

- [x] 종료 프로세스 마무리 (리더 서버 역할)
  - 종료 체킹 ZSET에서 n분이 지난 client id를 가져와 main key 상태 확인
  - 만약 main key의 value가 비었다면 종료 프로세스 종료
  - main key의 value에 session id가 남았다면 종료 명령 재전송 및 종료 체킹 ZSET에 client id 추가

- [ ] 웹소켓 로컬 모니터링 (Fallback)
  - Redis 기반 모니터링(1차 책임)으로 웹소켓 종료 완료 (정상 시나리오)
  - Redis 시그널 키 만료 감지 실패 등 여러 원인으로 종료 누락 가능성 있음 (예외 케이스)
  - 각 서버에서 자체적으로 웹소켓 접근 시간 추적해 2차 종료 수행 (Fallback)
  - Redis 기반 종료 프로세스: 사용자 마지막 접근 25m 경과 시 종료 프로세스 시작
  - 로컬(Fallback) 종료 프로세스: 사용자 마지막 접근 30m 경과 시 종료 프로세스 시작

### 예외 케이스

- [ ] Abrupt shutdown된 서버의 session id 제거 방법
  - 종료 체킹 ZSET과 main key 상태 기반으로 main key에 있는 session id가 모두 제거될 때까지 재명령
  - 만약 session id를 소유했던 서버가 Abrupt shutdown했다면 재명령 효과 없으며, 의미 없는 재명령을 무한히 실행할 것
  - Graceful shutdown 했다면 main key까지 정상 제거됨
  - 이는 heartbeat와 리더가 강제 정리해 해결


