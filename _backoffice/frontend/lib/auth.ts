const ROLE_KEY = 'backoffice_role';

export const ROLE_ROOT = 'ROLE_ROOT';
export const ROLE_BACKOFFICE_USER = 'ROLE_BACKOFFICE_USER';

export function saveRole(role: string): void {
  localStorage.setItem(ROLE_KEY, role);
}

export function getRole(): string {
  return localStorage.getItem(ROLE_KEY) ?? ROLE_BACKOFFICE_USER;
}

export function clearRole(): void {
  localStorage.removeItem(ROLE_KEY);
}

export function isRoot(): boolean {
  return getRole() === ROLE_ROOT;
}