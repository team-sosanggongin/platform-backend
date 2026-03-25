'use client';

import { useState } from 'react';
import { Notice, NoticeStatus } from "@/types";
import { Badge, Button, ListLayout, TableColumn } from '@/components';
import { NoticeFormModal } from './NoticeFormModal';

const INITIAL_NOTICES: Notice[] = [
  {
    id: '1',
    title: '시스템 점검 안내 (3월 20일 오전 2시)',
    content: '안정적인 서비스 제공을 위해 시스템 점검을 진행합니다.',
    isSystemMaintenance: true,
    status: 'published',
    startAt: '2024-03-19 22:00',
    endAt: '2024-03-20 06:00',
    publishedAt: '2024-03-15 10:00',
    maintenanceStartAt: '2024-03-20 02:00',
    maintenanceEndAt: '2024-03-20 04:00',
    author: '한보안',
    createdAt: '2024-03-15 09:30',
  },
  {
    id: '2',
    title: '서비스 이용약관 변경 안내',
    content: '2024년 4월 1일부터 서비스 이용약관이 변경됩니다.',
    isSystemMaintenance: false,
    status: 'scheduled',
    startAt: '2024-03-25 09:00',
    endAt: '2024-04-01 00:00',
    scheduledAt: '2024-03-25 09:00',
    author: '김관리',
    createdAt: '2024-03-14 14:00',
  },
  {
    id: '3',
    title: '신규 기능 출시 예정',
    content: '곧 새로운 기능이 출시될 예정입니다.',
    isSystemMaintenance: false,
    status: 'draft',
    author: '이매니저',
    createdAt: '2024-03-13 11:00',
  },
];

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

let nextId = INITIAL_NOTICES.length + 1;

export default function NoticesPage() {
  const [notices, setNotices] = useState<Notice[]>(INITIAL_NOTICES);
  const [searchField, setSearchField] = useState('title');
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedNotice, setSelectedNotice] = useState<Notice | null>(null);

  const itemsPerPage = 8;

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

  const handleSave = (data: Omit<Notice, 'id' | 'createdAt'>, status: NoticeStatus) => {
    const now = new Date().toISOString().slice(0, 16).replace('T', ' ');
    if (selectedNotice) {
      setNotices((prev) =>
        prev.map((n) =>
          n.id === selectedNotice.id ? { ...n, ...data, status, updatedAt: now } : n,
        ),
      );
    } else {
      const newNotice: Notice = {
        id: String(nextId++),
        createdAt: now,
        ...data,
        status,
      };
      setNotices((prev) => [newNotice, ...prev]);
    }
    setCurrentPage(1);
  };

  const handleDelete = (id: string) => {
    setNotices((prev) => prev.filter((n) => n.id !== id));
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
          rowKey: (n) => n.id,
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