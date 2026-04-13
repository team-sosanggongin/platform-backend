export interface NavigationItem {
  label: string;
  href?: string;
  subItems?: NavigationItem[];
}

export interface RoleSummary {
  id: number;
  roleName: string;
  description: string;
}

export interface User {
  id: string;
  loginId: string;
  name: string;
  email: string | null;
  phoneNumber: string | null;
  root: boolean;
  locked: boolean;
  passwordExpired: boolean;
  lockedAt: string | null;
  createdAt: string;
  roles?: RoleSummary[];
  permissionCodes?: string[];
}

export interface Permission {
  id: number;
  permissionName: string;
  permDomain: string;
}

export interface Role {
  id: number;
  roleName: string;
  description: string;
  permissions: Permission[];
  createdAt: string;
  updatedAt?: string;
}

export type NoticeStatus = 'DRAFT' | 'PUBLISHED' | 'HIDDEN' | 'SCHEDULED';

export interface Notice {
  id: number;
  title: string;
  content: string;
  isServiceMaintenance: boolean;
  status: NoticeStatus;
  isPinned: boolean;
  startsAt?: string;
  endsAt?: string;
  scheduledAt?: string;
  maintenanceStartAt?: string;
  maintenanceEndAt?: string;
  authorName: string;
  createdBy: string;
  viewCount: number | null;
  createdAt: string;
  updatedAt?: string;
}

export interface AuditLog {
  id: number;
  accountId: string;
  actionType: string;
  resourceDomain: string;
  resourceId: string | null;
  createdAt: string;
}

export interface LoginHistory {
  id: number;
  accountId: string;
  loginId: string;
  ipAddress: string;
  userAgent: string;
  success: boolean;
  createdAt: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}