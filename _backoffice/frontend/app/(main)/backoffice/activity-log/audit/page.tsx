'use client';

import { useState, useEffect, useCallback } from 'react';
import { useRouter } from 'next/navigation';
import { AuditLog, PageResponse } from '@/types';
import { ListLayout, TableColumn } from '@/components';
import { api } from '@/lib/api';
import { useAuth } from '@/lib/auth-context';

export default function AuditLogPage() {
  const router = useRouter();
  const { me, loading } = useAuth();
  const [auditLogs, setAuditLogs] = useState<AuditLog[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [searchField, setSearchField] = useState('actionType');
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);

  const fetchAuditLogs = useCallback(async () => {
    try {
      const params = new URLSearchParams({
        page: String(currentPage - 1),
        size: '10',
      });
      const res = await api.get<PageResponse<AuditLog>>(`/api/activity-log/audit?${params}`);
      setAuditLogs(res.content);
      setTotalPages(res.totalPages);
    } catch (e) {
      console.error('활동 이력 조회 실패:', e);
    }
  }, [currentPage]);

  useEffect(() => {
    if (loading) return;
    if (!me?.root) {
      router.push('/backoffice');
      return;
    }
    fetchAuditLogs();
  }, [loading, me, router, fetchAuditLogs]);

  const filtered = searchQuery.trim()
    ? auditLogs.filter((log) => {
        const value = log[searchField as keyof AuditLog]?.toString().toLowerCase() ?? '';
        return value.includes(searchQuery.toLowerCase());
      })
    : auditLogs;

  const columns: TableColumn<AuditLog>[] = [
    { header: 'No', render: (log) => log.id, width: '60px' },
    { header: '계정ID', render: (log) => log.accountId },
    { header: '작업유형', render: (log) => log.actionType, width: '160px' },
    { header: '도메인', render: (log) => log.resourceDomain, width: '120px' },
    { header: '리소스ID', render: (log) => log.resourceId ?? '-', width: '100px' },
    { header: '등록일', render: (log) => log.createdAt, width: '180px' },
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
        data: filtered,
        rowKey: (log) => String(log.id),
        emptyMessage: '활동 이력이 없습니다.',
      }}
      pagination={{ currentPage, totalPages, onPageChange: setCurrentPage }}
    />
  );
}
