# ☁️ AWS 인프라 네트워크 (VPC & Subnet) 설계 정의서

## 1. 아키텍처 핵심 전략
* **비용 최적화 (Zero-NAT):** NAT Gateway 고정 비용(월 약 4~5만 원)을 원천 제거하기 위해 API 컨테이너를 Public Subnet에 전진 배치합니다.
* **철통 보안 (Security Group Chaining):** API 서버가 퍼블릭 망에 위치하지만, AWS API Gateway(VPC Link)를 통과한 트래픽만 받도록 보안 그룹을 겹겹이 설정하여 Private Subnet과 동일한 수준의 보안을 달성합니다.
* **모놀리틱 & 확장성:** 현재의 모놀리틱 백엔드 구조에 최적화하되, API Subnet에 `/20` 대역(IP 4,096개)을 할당하여 미래의 MSA(마이크로서비스) 대확장 시에도 네트워크 재설계가 필요 없도록 대비합니다.
* **고가용성 (Multi-AZ):** 모든 자원을 서울 리전의 `a존`과 `c존`에 물리적으로 이중화하여 배치합니다.

---

## 2. VPC (Virtual Private Cloud) 대역 지정
물리적으로 완벽하게 격리된 스테이징(테스트) 환경과 프로덕션(운영) 환경을 구성합니다. 외부 망과의 IP 충돌을 방지하기 위해 `10.0` 대역을 피하여 설정했습니다.

| 환경 (Environment) | VPC CIDR | 총 가용 IP 수 | 비고 |
| :--- | :--- | :--- | :--- |
| **Staging (stg)** | `10.21.0.0/16` | 65,536개 | 개발 및 테스트 환경 |
| **Production (prod)**| `10.22.0.0/16` | 65,536개 | 실제 서비스 운영 환경 |

---

## 3. Subnet 상세 설계표 (Staging: 10.21.0.0/16 기준)
*설계 원칙: 단편화(Fragmentation) 및 IP 할당 충돌을 막기 위해 가장 큰 대역(`/20`)부터 순차적으로 꽉 채워 배치합니다.*

| 서브넷 명칭 | CIDR 대역 | 가용 IP 수 | AZ (가용 영역) | 외부 통신 | 용도 및 타겟 리소스 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Public-API-a** | `10.21.0.0/20` | 4,091개 | ap-northeast-2a | IGW 연결 | 사장님/직원용 메인 API 서버 (ECS Fargate) |
| **Public-API-c** | `10.21.16.0/20`| 4,091개 | ap-northeast-2c | IGW 연결 | 메인 API 서버 (장애 대비 이중화) |
| **Public-Admin-a**| `10.21.32.0/24`| 251개 | ap-northeast-2a | IGW 연결 | 백오피스/관리자 전용 API 서버 (ECS) |
| **Public-Admin-c**| `10.21.33.0/24`| 251개 | ap-northeast-2c | IGW 연결 | 백오피스/관리자 전용 API 서버 (이중화) |
| **Private-DB-a** | `10.21.34.0/24`| 251개 | ap-northeast-2a | **외부 단절** | 메인 데이터베이스 (Amazon RDS) |
| **Private-DB-c** | `10.21.35.0/24`| 251개 | ap-northeast-2c | **외부 단절** | 예비/읽기 복제본 데이터베이스 (RDS) |

*(💡 운영 환경(prod) 구축 시에는 위 표의 `10.21` 부분을 `10.22`로 일괄 치환하여 적용합니다.)*

---

## 4. 트래픽 흐름 및 방화벽 (Security Group) 정책
데이터의 흐름과 보안 경계를 서브넷의 역할에 맞게 명확히 통제합니다.

### 4.1. 사용자 API 요청 흐름 (라우팅)
1. **Client (사장님/직원 앱/웹뷰)** ➡️ AWS API Gateway 호출
2. **API Gateway** ➡️ 주소록(Cloud Map) 확인 후, VPC Link를 통해 `Public-API` 서브넷의 특정 컨테이너 IP로 트래픽 전달
3. **ECS Container (API 서버)** ➡️ `Private-DB` 서브넷에 위치한 RDS에서 데이터 조회/저장

### 4.2. 핵심 보안 그룹 (SG) 인바운드 차단 규칙
* **메인 API 컨테이너 SG:** 인터넷의 직접 접근(`0.0.0.0/0`)을 완벽히 차단. **오직 API Gateway(VPC Link)의 보안 그룹에서 오는 트래픽만 허용.**
* **Admin 컨테이너 SG:** 향후 백오피스 망 분리를 대비하여, 인가된 관리자 IP 또는 백오피스 전용 API Gateway의 트래픽만 허용.
* **RDS (DB) SG:** 인터넷 접근 원천 불가. **오직 `Public-API` 및 `Public-Admin` 보안 그룹이 부여된 서버에서 들어오는 DB 포트(예: PostgreSQL 5432) 접근만 허용.**                                                                             