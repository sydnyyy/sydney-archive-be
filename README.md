## WebSocket 관리

- 분산 서버 가정
- 30분간 사용자 접근 없으면 웹소켓 종료 프로세스 실행

### 관련 데이터
- Redis에서 signal key, main key 관리
- 로컬에서 WebSocketSession 객체 자체 목록 관리
- client Id 기준 접근 시각 관리

### 웹소켓 연결 종료 프로세스 설계

모든 서버 pub/sub으로 signal key TTL 만료 감지
- 이후 사용자에게 종료 다이얼로그 출력 (종료 or 유지)
- 유지 선택 시 signal key 재생성 및 main key TTL 연장
- 종료 선택 시 종료 프로세스 실행

사용자로부터 종료 응답을 받은 서버는
- 종료 체킹 ZSET에 client id 추가 후
- stream을 사용해 모든 서버로 종료 명령 전달

서버는 종료 명령을 읽고
- 로컬 자체 목록에서 client에 연결된 모든 웹소켓 세션 강제 종료
- 및 redis main key에 연결된 session id 제거

리더 서버는
- 종료 체킹 ZSET에서 n분이 지난 client id를 가져와
- client id로 main key 상태 확인
- 만약 main key의 value가 비었다면 종료 프로세스 종료
- main key의 value에 session id가 남았다면 종료 명령 재전송 및 종료 체킹 ZSET에 client id 추가


