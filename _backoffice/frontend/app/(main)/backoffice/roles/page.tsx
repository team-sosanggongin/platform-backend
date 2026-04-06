'use client';

import { useState } from 'react';
import { Role } from "@/types";
import {Badge, Button, ListLayout, TableColumn} from "@/components";
import {User} from "@/types";import { RoleFormModal } from './RoleFormModal';

const INITIAL_ROLES: Role[] = [
  {
    id: '1',
    name: 'Admin',
    description: '모든 기능에 대한 전체 접근 권한',
    permissions: [
      { id: 'p1', permission: '*.notices' },
      { id: 'p2', permission: '*.members' },
      { id: 'p3', permission: '*.roles' },
    ],
    createdAt: '2023-01-01 00:00',
  },
  {
    id: '2',
    name: 'Manager',
    description: '사용자 조회 및 공지 관리 권한',
    permissions: [
      { id: 'p4', permission: 'read.members' },
      { id: 'p5', permission: '*.notices' },
    ],
    createdAt: '2023-01-01 00:00',
  },
  {
    id: '3',
    name: 'Editor',
    description: '공지 조회 및 작성 권한',
    permissions: [
      { id: 'p6', permission: 'read.notices' },
      { id: 'p7', permission: 'write.notices' },
    ],
    createdAt: '2023-06-01 00:00',
    updatedAt: '2024-01-15 10:30',
  },
];

let nextId = INITIAL_ROLES.length + 1;

export default function RolesPage() {
  const [roles, setRoles] = useState<Role[]>(INITIAL_ROLES);
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedRole, setSelectedRole] = useState<Role | null>(null);

  const itemsPerPage = 8;

  const filtered = roles.filter(
    (r) =>
      r.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      r.description.toLowerCase().includes(searchQuery.toLowerCase()),
  );

  const totalPages = Math.ceil(filtered.length / itemsPerPage);
  const currentItems = filtered.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage,
  );

  const handleRowClick = (role: Role) => {
    setSelectedRole(role);
    setIsModalOpen(true);
  };

  const handleOpenCreate = () => {
    setSelectedRole(null);
    setIsModalOpen(true);
  };

  const handleSave = (data: Omit<Role, 'id' | 'createdAt'>) => {
    const now = new Date().toISOString().slice(0, 16).replace('T', ' ');
    if (selectedRole) {
      setRoles((prev) =>
        prev.map((r) => (r.id === selectedRole.id ? { ...r, ...data } : r)),
      );
    } else {
      setRoles((prev) => [...prev, { id: String(nextId++), createdAt: now, ...data }]);
    }
    setCurrentPage(1);
  };

  const handleDelete = (id: string) => {
    setRoles((prev) => prev.filter((r) => r.id !== id));
  };

  const columns: TableColumn<Role>[] = [
    { header: 'No', render: (r) => r.id, width: '60px' },
    { header: '역할', render: (r) => <strong>{r.name}</strong>, width: '130px' },
    { header: '설명', render: (r) => r.description },
    {
      header: '권한 목록',
      render: (r) => (
        <span style={{ display: 'flex', flexWrap: 'wrap', gap: '4px' }}>
          {r.permissions.slice(0, 3).map((p) => (
            <Badge key={p.id} variant="info">{p.permission}</Badge>
          ))}
          {r.permissions.length > 3 && (
            <Badge variant="info">+{r.permissions.length - 3}</Badge>
          )}
        </span>
      ),
    },
    { header: '권한 수', render: (r) => `${r.permissions.length}개`, width: '80px' },
    { header: '등록일', render: (r) => r.createdAt, width: '140px' },
    { header: '수정일', render: (r) => r.updatedAt ?? '-', width: '140px' },
  ];

  const searchOptions = [{ value: 'name', label: '역할' },
      { value: 'permission', label: '권한' },];

  return (
    <>
      <ListLayout
        title="권한 관리"
        search={{
          field: 'name',
          onFieldChange: () => {},
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
          rowKey: (r) => r.id,
          emptyMessage: '등록된 권한이 없습니다.',
          onRowClick: handleRowClick,
        }}
        pagination={{ currentPage, totalPages, onPageChange: setCurrentPage }}
        extraActions={
          <Button style={{ width: 'auto' }} onClick={handleOpenCreate}>
            + 권한 추가
          </Button>
        }
      />

      <RoleFormModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSave={handleSave}
        onDelete={handleDelete}
        role={selectedRole}
      />
    </>
  );
}
