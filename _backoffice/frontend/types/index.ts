export interface NavigationItem {
  label: string;
  href?: string;
  subItems?: NavigationItem[];
}

export interface User {
  id: string;
  name: string;
  email: string;
  role: 'Admin' | 'Manager' | 'Editor';
  joinDate: string;
  status: 'Active' | 'Inactive';
}
