'use client';

import { useState, useEffect, useCallback } from 'react';
import { Notice, NoticeStatus, PageResponse } from "@/types";
import { Badge, Button, ListLayout, TableColumn } from '@/components';
import { api } from '@/lib/api';
import { NoticeFormModal } from './NoticeFormModal';

const STATUS_LABEL: Record<NoticeStatus, string> = {
  DRAFT: '임시저장',
  PUBLISHED: '발행됨',
  HIDDEN: '숨김',
  SCHEDULED: '예약됨',
};

const STATUS_VARIANT: Record<NoticeStatus, 'success' | 'info' | 'warning' | 'error'> = {
  DRAFT: 'warning',
  PUBLISHED: 'success',
  HIDDEN: 'error',
  SCHEDULED: 'info',
};

export default function NoticesPage() {
  const [notices, setNotices] = useState<Notice[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedNotice, setSelectedNotice] = useState<Notice | null>(null);

  const fetchNotices = useCallback(async () => {
    try {
      const params = new URLSearchParams({
        page: String(currentPage - 1),
        size: '8',
      });
      if (searchQuery.trim()) {
        params.set('keyword', searchQuery.trim());
      }
      const res = await api.get<PageResponse<Notice>>(`/api/notice?${params}`);
      setNotices(res.content);
      setTotalPages(res.totalPages);
    } catch (e) {
      console.error('공지사항 조회 실패:', e);
    }
  }, [currentPage, searchQuery]);

  useEffect(() => {
    fetchNotices();
  }, [fetchNotices]);

  const handleOpenCreate = () => {
    setSelectedNotice(null);
    setIsModalOpen(true);
  };

  const handleRowClick = (notice: Notice) => {
    setSelectedNotice(notice);
    setIsModalOpen(true);
  };

  const handleSave = async (data: Record<string, unknown>, status: NoticeStatus) => {
    try {
      if (selectedNotice) {
        await api.patch(`/api/notice/${selectedNotice.id}`, data);
        if (status !== selectedNotice.status) {
          await api.patch(`/api/notice/${selectedNotice.id}/status`, { status });
        }
      } else {
        const created = await api.post<Notice>('/api/notice', data);
        if (status !== 'DRAFT') {
          await api.patch(`/api/notice/${created.id}/status`, { status });
        }
      }
      setIsModalOpen(false);
      setCurrentPage(1);
      await fetchNotices();
    } catch (e) {
      console.error('공지사항 저장 실패:', e);
      alert('저장에 실패했습니다.');
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await api.delete(`/api/notice/${id}`);
      setIsModalOpen(false);
      await fetchNotices();
    } catch (e) {
      console.error('공지사항 삭제 실패:', e);
      alert('삭제에 실패했습니다.');
    }
  };

  const columns: TableColumn<Notice>[] = [
    { header: 'No', render: (n) => n.id, width: '60px' },
    {
      header: '제목',
      render: (n) => (
        <span style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          {n.isServiceMaintenance && <Badge variant="error">점검</Badge>}
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
        n.startsAt
          ? `${n.startsAt}${n.endsAt ? ` ~ ${n.endsAt}` : ''}`
          : '-',
      width: '260px',
    },
    {
      header: '점검 시간',
      render: (n) =>
        n.isServiceMaintenance && n.maintenanceStartAt
          ? `${n.maintenanceStartAt} ~ ${n.maintenanceEndAt ?? ''}`
          : '-',
      width: '200px',
    },
    { header: '작성자', render: (n) => n.authorName, width: '100px' },
    { header: '등록일', render: (n) => n.createdAt, width: '140px' },
    { header: '수정일', render: (n) => n.updatedAt ?? '-', width: '140px' },
  ];

  return (
    <>
      <ListLayout
        title="공지사항 관리"
        search={{
          field: 'keyword',
          onFieldChange: () => {},
          options: [{ value: 'keyword', label: '제목+내용' }],
          query: searchQuery,
          onQueryChange: (e) => { setSearchQuery(e.target.value); setCurrentPage(1); },
        }}
        table={{
          columns,
          data: notices,
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
