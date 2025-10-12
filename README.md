## WebSocket 관리

- 분산 서버 가정
- 30분간 사용자 접근 없으면 웹소켓 연결 종료 프로세스 시작
- 웹소켓 연결 종료 프로세스를 통해 서버 리소스 효율적으로 관리하는 것이 목적

### 웹소켓 연결 종료 프로세스 설계

- [x] Redis signal-key 만료 감지 및 종료 다이얼로그 출력
  - 모든 서버 pub/sub으로 signal-key TTL 만료 감지
  - 이후 사용자에게 종료 다이얼로그 출력 (종료 or 유지)
  - 유지 선택 시 signal-key 재생성 및 main-key TTL 연장
  - 종료 선택 시 종료 프로세스 실행

- [x] 종료 프로세스 시작
  - 사용자로부터 종료 응답을 받은 서버는 종료 체킹 ZSET에 client id 추가 후 stream을 사용해 모든 서버로 종료 명령 전달
  - 서버는 종료 명령을 읽고 로컬 자체 목록에서 client에 연결된 모든 웹소켓 세션 강제 종료 및 redis main-key에 연결된 session id 제거

- [x] 종료 프로세스 마무리 (리더 서버 역할)
  - 종료 체킹 ZSET에서 n분이 지난 client id를 가져와 main-key 상태 확인
  - 만약 main-key가 존재하지 않거나, value가 비었다면 종료 프로세스 종료
  - main-key에 session id가 남았다면 종료 명령 재전송 및 종료 체킹 ZSET에 client id 추가

- [x] 웹소켓 로컬 모니터링 (Fallback)
  - 메인 모니터링 장애 시 로컬 모니터링이 세션 관리
  - ConcurrentHashMap + ConcurrentSkipListSet 조합으로 time-based LRU 구현
  - lock striping 기법 적용해 두 컬렉션 간 데이터 일관성 보장

