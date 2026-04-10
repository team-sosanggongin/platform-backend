export interface NavigationItem {
  label: string;
  href?: string;
  subItems?: NavigationItem[];
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
}

export interface RolePermission {
  id: string;
  permission: string;
}

export interface Role {
  id: string;
  name: string;
  description: string;
  permissions: RolePermission[];
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

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}