'use client';

import { useState, useEffect, useCallback } from 'react';
import { Notice, NoticeStatus } from "@/types";
import { Badge, Button, ListLayout, TableColumn } from '@/components';
import { api } from '@/lib/api';
import { NoticeFormModal } from './NoticeFormModal';

const STATUS_LABEL: Record<NoticeStatus, string> = {
  published: '발행됨',
  draft: '임시저장',
  scheduled: '예약됨',
};

const STATUS_VARIANT: Record<NoticeStatus, 'success' | 'info' | 'warning'> = {
  published: 'success',
  draft: 'warning',
  scheduled: 'info',
};

export default function NoticesPage() {
  const [notices, setNotices] = useState<Notice[]>([]);
  const [searchField, setSearchField] = useState('title');
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedNotice, setSelectedNotice] = useState<Notice | null>(null);

  const itemsPerPage = 8;

  const fetchNotices = useCallback(async () => {
    const res = await api.get<Notice[]>('/api/notices');
    if (res.ok && res.data) {
      setNotices(res.data);
    }
  }, []);

  useEffect(() => {
    fetchNotices();
  }, [fetchNotices]);

  const filtered = notices.filter((n) => {
    const value = n[searchField as keyof Notice]?.toString().toLowerCase() ?? '';
    return value.includes(searchQuery.toLowerCase());
  });

  const totalPages = Math.ceil(filtered.length / itemsPerPage);
  const currentItems = filtered.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

  const handleOpenCreate = () => {
    setSelectedNotice(null);
    setIsModalOpen(true);
  };

  const handleRowClick = (notice: Notice) => {
    setSelectedNotice(notice);
    setIsModalOpen(true);
  };

  const handleSave = async (data: Omit<Notice, 'id' | 'createdAt'>, status: NoticeStatus) => {
    if (selectedNotice) {
      await api.put(`/api/notices/${selectedNotice.id}`, { ...data, status });
    } else {
      await api.post('/api/notices', { ...data, status });
    }
    setIsModalOpen(false);
    setCurrentPage(1);
    await fetchNotices();
  };

  const handleDelete = async (id: number) => {
    await api.delete(`/api/notices/${id}`);
    setIsModalOpen(false);
    await fetchNotices();
  };

  const columns: TableColumn<Notice>[] = [
    { header: 'No', render: (n) => n.id, width: '60px' },
    {
      header: '제목',
      render: (n) => (
        <span style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          {n.isSystemMaintenance && <Badge variant="error">점검</Badge>}
          {n.title}
        </span>
      ),
    },
    {
      header: '상태',
      render: (n) => (
        <Badge variant={STATUS_VARIANT[n.status]}>{STATUS_LABEL[n.status]}</Badge>
      ),
      width: '100px',
    },
    {
      header: '노출 기간',
      render: (n) =>
        n.startAt
          ? `${n.startAt}${n.endAt ? ` ~ ${n.endAt}` : ''}`
          : '-',
      width: '260px',
    },
    {
      header: '점검 시간',
      render: (n) =>
        n.isSystemMaintenance && n.maintenanceStartAt
          ? `${n.maintenanceStartAt} ~ ${n.maintenanceEndAt ?? ''}`
          : '-',
      width: '200px',
    },
    { header: '작성자', render: (n) => n.author, width: '100px' },
    { header: '등록일', render: (n) => n.createdAt, width: '140px' },
    { header: '수정일', render: (n) => n.updatedAt ?? '-', width: '140px' },
  ];

  const searchOptions = [
    { value: 'title', label: '제목' },
    { value: 'content', label: '내용' },
  ];

  return (
    <>
      <ListLayout
        title="공지사항 관리"
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
          rowKey: (n) => String(n.id),
          emptyMessage: '등록된 공지사항이 없습니다.',
          onRowClick: handleRowClick,
        }}
        pagination={{ currentPage, totalPages, onPageChange: setCurrentPage }}
        extraActions={
          <Button style={{ width: 'auto' }} onClick={handleOpenCreate}>
            + 새 공지 작성
          </Button>
        }
      />

      <NoticeFormModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSave={handleSave}
        onDelete={handleDelete}
        notice={selectedNotice}
      />
    </>
  );
}
