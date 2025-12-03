## 🖥️ Tech Stack

| Category       | Stack                        |
|----------------|------------------------------|
| Language       | Java 21                      |
| Backend        | Spring Boot                  |
| Frontend       | Next.js (App Router 기반)      |
| Database       | MongoDB                      |
| Message Broker | Redis (Pub/Sub for WebSocket) |
| Web Server     | Nginx                        |
| Protocol       | WebSocket                    |

## 🛠️ Architecture

### Nginx 구조
- [x] FE-Nginx-BE 구조
- [x] client_id 기준 라우팅 (`hash $arg_client_id consistent`)

### WebSocket 관리

- [x] Redis 기반 메인 모니터링
- [x] 로컬 모니터링 (Fallback)
- [x] nginx proxy_read/send_timeout 설정
- [x] ping/pong (프로토콜 레벨)

### 기능
- [ ] 아이템
- [ ] 스터디
- [ ] 마이페이지
- [ ] 채팅