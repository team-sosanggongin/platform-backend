'use client';

import { useState } from 'react';
import { ListLayout, TableColumn } from '@/components';

interface AuditLog {
  id: number;
  accountId: string;
  actionType: string;
  resourceDomain: string;
  resourceId: string | null;
  createdAt: string;
}

const MOCK_AUDIT_LOGS: AuditLog[] = [
  {
    id: 1,
    accountId: '7331b320-06bd-4193-b675-96585cebd887',
    actionType: 'CREATE',
    resourceDomain: 'NOTICE',
    resourceId: '1',
    createdAt: '2026-04-05 22:31:24',
  },
  {
    id: 2,
    accountId: '7331b320-06bd-4193-b675-96585cebd887',
    actionType: 'DELETE',
    resourceDomain: 'NOTICE',
    resourceId: '1',
    createdAt: '2026-04-05 22:32:18',
  },
];

export default function AuditLogPage() {
  const [searchField, setSearchField] = useState('actionType');
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  const filtered = MOCK_AUDIT_LOGS.filter((log) => {
    const value = log[searchField as keyof AuditLog]?.toString().toLowerCase() ?? '';
    return value.includes(searchQuery.toLowerCase());
  });

  const totalPages = Math.ceil(filtered.length / itemsPerPage);
  const currentItems = filtered.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage,
  );

  const columns: TableColumn<AuditLog>[] = [
    { header: 'No', render: (log) => log.id, width: '60px' },
    { header: '계정ID', render: (log) => log.accountId },
    { header: '작업유형', render: (log) => log.actionType, width: '120px' },
    { header: '도메인', render: (log) => log.resourceDomain, width: '120px' },
    { header: '리소스ID', render: (log) => log.resourceId ?? '-', width: '100px' },
    { header: '등록일', render: (log) => log.createdAt, width: '160px' },
  ];

  const searchOptions = [
    { value: 'actionType', label: '작업유형' },
    { value: 'resourceDomain', label: '도메인' },
  ];

  return (
    <ListLayout
      title="활동 이력"
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
        emptyMessage: '활동 이력이 없습니다.',
      }}
      pagination={{ currentPage, totalPages, onPageChange: setCurrentPage }}
    />
  );
}