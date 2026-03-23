'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { User } from '../../../types';
import { Badge, ListLayout, TableColumn } from '../../../components';

export default function UserManagementPage() {
  const router = useRouter();
  const allUsers: User[] = [
    { id: '1', name: '김관리', email: 'admin@example.com', role: 'Admin', joinDate: '2023-01-15', status: 'Active' },
    { id: '2', name: '이매니저', email: 'manager@example.com', role: 'Manager', joinDate: '2023-03-22', status: 'Active' },
    { id: '3', name: '박편집', email: 'editor1@example.com', role: 'Editor', joinDate: '2023-06-10', status: 'Inactive' },
    { id: '4', name: '최수정', email: 'editor2@example.com', role: 'Editor', joinDate: '2023-08-05', status: 'Active' },
    { id: '5', name: '정운영', email: 'op@example.com', role: 'Manager', joinDate: '2023-11-12', status: 'Active' },
    { id: '6', name: '한보안', email: 'security@example.com', role: 'Admin', joinDate: '2024-01-20', status: 'Active' },
    { id: '7', name: '강개발', email: 'dev1@example.com', role: 'Editor', joinDate: '2024-02-10', status: 'Active' },
    { id: '8', name: '조디자인', email: 'design1@example.com', role: 'Editor', joinDate: '2024-02-15', status: 'Active' },
    { id: '9', name: '윤기획', email: 'plan1@example.com', role: 'Manager', joinDate: '2024-02-20', status: 'Inactive' },
    { id: '10', name: '임테스트', email: 'test1@example.com', role: 'Editor', joinDate: '2024-03-01', status: 'Active' },
    { id: '11', name: '성지원', email: 'support1@example.com', role: 'Manager', joinDate: '2024-03-05', status: 'Active' },
    { id: '12', name: '배로그', email: 'log1@example.com', role: 'Editor', joinDate: '2024-03-10', status: 'Active' },
  ];

  const [searchField, setSearchField] = useState('name');
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 5;

  const filteredUsers = allUsers.filter((user) => {
    const value = user[searchField as keyof User]?.toString().toLowerCase() || '';
    return value.includes(searchQuery.toLowerCase());
  });

  const totalPages = Math.ceil(filteredUsers.length / itemsPerPage);
  const currentItems = filteredUsers.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

  const columns: TableColumn<User>[] = [
    { header: 'ID', render: (user) => user.id, width: '80px' },
    { header: '이름', render: (user) => user.name },
    { header: '이메일', render: (user) => user.email },
    { header: '권한', render: (user) => <span style={{ fontWeight: 500 }}>{user.role}</span> },
    { header: '가입일', render: (user) => user.joinDate },
    { 
      header: '상태', 
      render: (user) => (
        <Badge variant={user.status === 'Active' ? 'success' : 'error'}>
          {user.status === 'Active' ? '활성' : '비활성'}
        </Badge>
      ) 
    },
  ];

  const searchOptions = [
    { value: 'name', label: '이름' },
    { value: 'email', label: '이메일' },
    { value: 'id', label: 'ID' },
  ];

  const handleRowClick = (user: User) => {
    router.push(`/users/${user.id}`);
  };

  return (
    <ListLayout
      title="사용자 관리"
      search={{
        field: searchField,
        onFieldChange: (e) => {
          setSearchField(e.target.value);
          setCurrentPage(1);
        },
        options: searchOptions,
        query: searchQuery,
        onQueryChange: (e) => {
          setSearchQuery(e.target.value);
          setCurrentPage(1);
        },
      }}
      table={{
        columns,
        data: currentItems,
        rowKey: (user) => user.id,
        emptyMessage: "검색 결과가 없습니다.",
        onRowClick: handleRowClick,
      }}
      pagination={{
        currentPage,
        totalPages,
        onPageChange: setCurrentPage,
      }}
    />
  );
}
