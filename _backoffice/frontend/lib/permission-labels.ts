const PERMISSION_LABELS: Record<string, string> = {
  'create.notice': '공지 생성',
  'update.notice': '공지 수정',
  'delete.notice': '공지 삭제',
  'create.role': '역할 생성',
  'update.role': '역할 수정',
  'delete.role': '역할 삭제',
};

const DOMAIN_LABELS: Record<string, string> = {
  notice: '공지사항',
  role: '역할',
};

export function getPermissionLabel(permissionName: string): string {
  return PERMISSION_LABELS[permissionName] ?? permissionName;
}

export function getDomainLabel(permDomain: string): string {
  return DOMAIN_LABELS[permDomain] ?? permDomain;
}
