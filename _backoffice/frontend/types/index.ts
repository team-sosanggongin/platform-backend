export interface NavigationItem {
  label: string;
  href?: string;
  subItems?: NavigationItem[];
}

export interface User {
  id: string;
  name: string;
  email: string;
  phone?: string;
  roles: string[];
  joinDate: string;
  status: 'Active' | 'Inactive' | 'Pending' | 'Locked';
  lockedAt?: string;
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

export type NoticeStatus = 'published' | 'draft' | 'scheduled';

export interface Notice {
  id: number;
  title: string;
  content: string;
  isSystemMaintenance: boolean;
  status: NoticeStatus;
  startAt?: string;
  endAt?: string;
  scheduledAt?: string;
  publishedAt?: string;
  maintenanceStartAt?: string;
  maintenanceEndAt?: string;
  author: string;
  createdAt: string;
  updatedAt?: string;
}
