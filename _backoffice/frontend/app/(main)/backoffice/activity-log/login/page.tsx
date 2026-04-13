'use client';

import { useState, useEffect, useCallback } from 'react';
import { useRouter } from 'next/navigation';
import { LoginHistory, PageResponse } from '@/types';
import { ListLayout, TableColumn, Badge } from '@/components';
import { api } from '@/lib/api';
import { useAuth } from '@/lib/auth-context';

export default function LoginHistoryPage() {
  const router = useRouter();
  const { me, loading } = useAuth();
  const [loginHistories, setLoginHistories] = useState<LoginHistory[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [searchField, setSearchField] = useState('accountId');
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);

  const fetchLoginHistories = useCallback(async () => {
    try {
      const params = new URLSearchParams({
        page: String(currentPage - 1),
        size: '10',
      });
      const res = await api.get<PageResponse<LoginHistory>>(`/api/activity-log/login?${params}`);
      setLoginHistories(res.content);
      setTotalPages(res.totalPages);
    } catch (e) {
      console.error('로그인 이력 조회 실패:', e);
    }
  }, [currentPage]);

  useEffect(() => {
    if (loading) return;
    if (!me?.root) {
      router.push('/backoffice');
      return;
    }
    fetchLoginHistories();
  }, [loading, me, router, fetchLoginHistories]);

  const filtered = searchQuery.trim()
    ? loginHistories.filter((log) => {
        const value = log[searchField as keyof LoginHistory]?.toString().toLowerCase() ?? '';
        return value.includes(searchQuery.toLowerCase());
      })
    : loginHistories;

  const columns: TableColumn<LoginHistory>[] = [
    { header: 'No', render: (log) => log.id, width: '60px' },
    { header: '계정ID', render: (log) => log.accountId ?? '-' },
    { header: '로그인ID', render: (log) => log.loginId, width: '120px' },
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
    { header: '등록일', render: (log) => log.createdAt, width: '180px' },
  ];

  const searchOptions = [
    { value: 'accountId', label: '계정ID' },
    { value: 'loginId', label: '로그인ID' },
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
        data: filtered,
        rowKey: (log) => String(log.id),
        emptyMessage: '로그인 이력이 없습니다.',
      }}
      pagination={{ currentPage, totalPages, onPageChange: setCurrentPage }}
    />
  );
}
