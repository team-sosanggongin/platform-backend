'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import {Badge, Button, ListLayout, TableColumn} from "@/components";
import {User} from "@/types";

const ALL_USERS: User[] = [
  { id: '1', name: '김관리', email: 'admin@example.com', roles: ['Admin'], joinDate: '2023-01-15', status: 'Active' },
  { id: '2', name: '이매니저', email: 'manager@example.com', roles: ['Manager'], joinDate: '2023-03-22', status: 'Active' },
  { id: '3', name: '박편집', email: 'editor1@example.com', roles: ['Editor'], joinDate: '2023-06-10', status: 'Inactive' },
  { id: '4', name: '최수정', email: 'editor2@example.com', roles: ['Editor'], joinDate: '2023-08-05', status: 'Active' },
  { id: '5', name: '정운영', email: 'op@example.com', roles: ['Manager'], joinDate: '2023-11-12', status: 'Active' },
  { id: '6', name: '한보안', email: 'security@example.com', roles: ['Admin', 'Manager'], joinDate: '2024-01-20', status: 'Active' },
  { id: '7', name: '강개발', email: 'dev1@example.com', roles: ['Editor'], joinDate: '2024-02-10', status: 'Active' },
  { id: '8', name: '조디자인', email: 'design1@example.com', roles: ['Editor'], joinDate: '2024-02-15', status: 'Active' },
  { id: '9', name: '윤기획', email: 'plan1@example.com', roles: ['Manager'], joinDate: '2024-02-20', status: 'Inactive' },
  { id: '10', name: '임테스트', email: 'test1@example.com', roles: ['Editor'], joinDate: '2024-03-01', status: 'Active' },
  { id: '11', name: '성지원', email: 'support1@example.com', roles: ['Manager'], joinDate: '2024-03-05', status: 'Active' },
  { id: '12', name: '배로그', email: 'log1@example.com', roles: ['Editor'], joinDate: '2024-03-10', status: 'Active' },
  { id: '13', name: '오신입', email: 'new1@example.com', phone: '010-1234-5678', roles: ['Editor'], joinDate: '2024-03-20', status: 'Pending' },
  { id: '14', name: '권대기', email: 'new2@example.com', phone: '010-9876-5432', roles: ['Manager'], joinDate: '2024-03-21', status: 'Pending' },
  { id: '15', name: '류잠금', email: 'locked1@example.com', phone: '010-5555-1234', roles: ['Editor'], joinDate: '2024-01-08', status: 'Locked', lockedAt: '2024-03-24T14:32:00' },
  { id: '16', name: '노보안', email: 'locked2@example.com', phone: '010-7777-9999', roles: ['Manager'], joinDate: '2023-11-05', status: 'Locked', lockedAt: '2024-03-25T09:15:00' },
];

export default function UserManagementPage() {
  const router = useRouter();
  const [searchField, setSearchField] = useState('name');
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 5;

  const filteredUsers = ALL_USERS.filter((user) => {
    const value = user[searchField as keyof User]?.toString().toLowerCase() || '';
    return value.includes(searchQuery.toLowerCase());
  });

  const totalPages = Math.ceil(filteredUsers.length / itemsPerPage);
  const currentItems = filteredUsers.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

  const columns: TableColumn<User>[] = [
    { header: 'ID', render: (user) => user.id, width: '80px' },
    { header: '이름', render: (user) => user.name },
    { header: '이메일', render: (user) => user.email },
    {
      header: '권한',
      render: (user) => (
        <span style={{ display: 'flex', flexWrap: 'wrap', gap: '4px' }}>
          {user.roles.map((r) => (
            <Badge key={r} variant="info">{r}</Badge>
          ))}
        </span>
      ),
    },
    { header: '가입일', render: (user) => user.joinDate },
    {
      header: '상태',
      render: (user) => {
        if (user.status === 'Active') return <Badge variant="success">활성</Badge>;
        if (user.status === 'Inactive') return <Badge variant="error">비활성</Badge>;
        if (user.status === 'Locked') return <Badge variant="error">잠금</Badge>;
        return <Badge variant="warning">대기중</Badge>;
      },
    },
  ];

  const searchOptions = [
    { value: 'name', label: '이름' },
    { value: 'email', label: '이메일' },
    { value: 'id', label: 'ID' },
  ];

  return (
    <ListLayout
      title="사용자 관리"
      search={{
        field: searchField,
        onFieldChange: (e) => { setSearchField(e.target.value); setCurrentPage(1); },
        options: searchOptions,
        query: searchQuery,
        onQueryChange: (e) => { setSearchQuery(e.target.value); setCurrentPage(1); },
      }}
      table={{
        columns,
        data: currentItems,
        rowKey: (user) => user.id,
        emptyMessage: '검색 결과가 없습니다.',
        onRowClick: (user) => router.push(`/backoffice/users/${user.id}`),
      }}
      pagination={{ currentPage, totalPages, onPageChange: setCurrentPage }}
      extraActions={
        <Button style={{ width: 'auto' }} onClick={() => router.push('/backoffice/users/new')}>
          + 유저 등록
        </Button>
      }
    />
  );
}
