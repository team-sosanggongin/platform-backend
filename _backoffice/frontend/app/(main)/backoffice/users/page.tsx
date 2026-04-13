'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { Badge, Button, ListLayout, TableColumn } from '@/components';
import { User } from '@/types';
import { api } from '@/lib/api';
import { useAuth } from '@/lib/auth-context';

const getStatus = (user: User) => {
  if (user.locked) return { label: '잠금', variant: 'error' as const };
  if (user.passwordExpired) return { label: '대기중', variant: 'warning' as const };
  return { label: '활성', variant: 'success' as const };
};

export default function UserManagementPage() {
  const router = useRouter();
  const { me, loading } = useAuth();
  const [users, setUsers] = useState<User[]>([]);
  const [searchField, setSearchField] = useState('name');
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  useEffect(() => {
    if (loading) return;
    api.get<User[]>('/api/account').then(setUsers).catch(console.error);
  }, [loading]);

  const filtered = users.filter((user) => {
    const value = user[searchField as keyof User]?.toString().toLowerCase() ?? '';
    return value.includes(searchQuery.toLowerCase());
  });

  const totalPages = Math.max(1, Math.ceil(filtered.length / itemsPerPage));
  const currentItems = filtered.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage,
  );

  const isRoot = me?.root ?? false;

  const columns: TableColumn<User>[] = [
    { header: '아이디', render: (user) => user.loginId, width: '120px' },
    { header: '이름', render: (user) => user.name },
    { header: '이메일', render: (user) => user.email ?? '-' },
    {
      header: '역할',
      render: (user) => {
        if (user.root) return <Badge variant="info">ROOT</Badge>;
        const names = user.roles?.map((r) => r.roleName) ?? [];
        return names.length > 0 ? names.join(', ') : '-';
      },
      width: '180px',
    },
    {
      header: '상태',
      render: (user) => {
        const status = getStatus(user);
        return <Badge variant={status.variant}>{status.label}</Badge>;
      },
      width: '120px',
    },
    { header: '등록일', render: (user) => user.createdAt, width: '160px' },
  ];

  const searchOptions = [
    { value: 'name', label: '이름' },
    { value: 'loginId', label: '아이디' },
    { value: 'email', label: '이메일' },
  ];

  if (loading) return null;

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
        emptyMessage: '등록된 사용자가 없습니다.',
        onRowClick: isRoot ? (user) => router.push(`/backoffice/users/${user.id}`) : undefined,
      }}
      pagination={{ currentPage, totalPages, onPageChange: setCurrentPage }}
      extraActions={
        isRoot ? (
          <Button style={{ width: 'auto' }} onClick={() => router.push('/backoffice/users/new')}>
            + 유저 등록
          </Button>
        ) : undefined
      }
    />
  );
}
