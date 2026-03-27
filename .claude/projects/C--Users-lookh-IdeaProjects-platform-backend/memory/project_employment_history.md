---
name: 고용 상태 이력 추적
description: Employment 상태 변경 히스토리를 도메인 이벤트로 자동 기록 — 향후 노무 기능(근무기간, 퇴직금, 근로계약 증빙) 대비
type: project
---

Employment 상태 변경 시 EmploymentStatusHistory에 이력을 자동 저장하도록 구현함.

**Why:** 향후 노무 관련 기능(근무기간 산정, 퇴직금, 연차, 근로계약 증빙) 추가 예정이므로 상태 변경 이력이 법적 증빙 자료로 필요함.

**How to apply:** Employment 상태 변경은 반드시 `changeStatus()` 메서드를 통해 수행해야 이벤트가 발행되고 히스토리가 기록됨. 직접 `status` 필드를 변경하면 이력이 남지 않음.