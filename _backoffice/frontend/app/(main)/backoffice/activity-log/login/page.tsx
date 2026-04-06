'use client';

import { useState } from 'react';
import { ListLayout, TableColumn, Badge } from '@/components';

interface LoginHistory {
  id: number;
  accountId: string;
  ipAddress: string;
  userAgent: string;
  success: boolean;
  createdAt: string;
}

const MOCK_LOGIN_HISTORIES: LoginHistory[] = [
  {
    id: 1,
    accountId: '7331b320-06bd-4193-b675-96585cebd887',
    ipAddress: '0:0:0:0:0:0:0:1',
    userAgent: 'PostmanRuntime/7.53.0',
    success: true,
    createdAt: '2026-04-05 22:31:08',
  },
  {
    id: 2,
    accountId: '7331b320-06bd-4193-b675-96585cebd887',
    ipAddress: '0:0:0:0:0:0:0:1',
    userAgent: 'PostmanRuntime/7.53.0',
    success: false,
    createdAt: '2026-04-05 22:33:16',
  },
];

export default function LoginHistoryPage() {
  const [searchField, setSearchField] = useState('accountId');
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  const filtered = MOCK_LOGIN_HISTORIES.filter((log) => {
    const value = log[searchField as keyof LoginHistory]?.toString().toLowerCase() ?? '';
    return value.includes(searchQuery.toLowerCase());
  });

  const totalPages = Math.ceil(filtered.length / itemsPerPage);
  const currentItems = filtered.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage,
  );

  const columns: TableColumn<LoginHistory>[] = [
    { header: 'No', render: (log) => log.id, width: '60px' },
    { header: '계정ID', render: (log) => log.accountId },
    { header: 'IP주소', render: (log) => log.ipAddress, width: '160px' },
    { header: '브라우저', render: (log) => log.userAgent },
    {
      header: '결과',
      render: (log) => (
        <Badge variant={log.success ? 'success' : 'error'}>
          {log.success ? '성공' : '실패'}
        </Badge>
      ),
      width: '100px',
    },
    { header: '등록일', render: (log) => log.createdAt, width: '160px' },
  ];

  const searchOptions = [
    { value: 'accountId', label: '계정ID' },
    { value: 'success', label: '결과' },
  ];

  return (
    <ListLayout
      title="로그인 이력"
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
        rowKey: (log) => String(log.id),
        emptyMessage: '로그인 이력이 없습니다.',
      }}
      pagination={{ currentPage, totalPages, onPageChange: setCurrentPage }}
    />
  );
}