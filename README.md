## Tech Stack

| Category       | Stack                        |
|----------------|------------------------------|
| Language       | Java 21                      |
| Backend        | Spring Boot                  |
| Frontend       | Next.js (App Router 기반)      |
| Database       | MongoDB                      |
| Message Broker | Redis (Pub/Sub for WebSocket) |
| Web Server     | Nginx                        |
| Protocol       | WebSocket                    |


## Architecture

- [x] nginx 설정
  - FE-nginx-BE 구조
  - client_id 값 기준으로 라우팅

## WebSocket 관리

- [x] Redis 기반 메인 모니터링
- [x] 로컬 모니터링 (Fallback)
  - 사용자 접근 시각 수집
- [x] nginx proxy_read/send_timeout 설정
- [ ] ping/pong