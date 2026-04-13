'use client';

import { useCallback, useEffect, useState } from 'react';
import { Badge, Button, ListLayout, TableColumn } from '@/components';
import { Role } from '@/types';
import { api, ApiError } from '@/lib/api';
import { useAuth } from '@/lib/auth-context';
import { RoleFormModal, RoleFormPayload } from './RoleFormModal';

const formatDateTime = (value?: string) => {
  if (!value) return '-';
  return new Date(value).toLocaleString('ko-KR');
};

export default function RolesPage() {
  const { me, loading } = useAuth();
  const [roles, setRoles] = useState<Role[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedRole, setSelectedRole] = useState<Role | null>(null);
  const [errorMsg, setErrorMsg] = useState('');

  const itemsPerPage = 8;
  const isRoot = me?.root ?? false;

  const fetchRoles = useCallback(async () => {
    try {
      const data = await api.get<Role[]>('/api/role');
      setRoles(data);
    } catch (e) {
      if (e instanceof ApiError) setErrorMsg(e.message);
    }
  }, []);

  useEffect(() => {
    if (loading) return;
    fetchRoles();
  }, [loading, fetchRoles]);

  const filtered = roles.filter(
    (r) =>
      r.roleName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      (r.description ?? '').toLowerCase().includes(searchQuery.toLowerCase()),
  );

  const totalPages = Math.max(1, Math.ceil(filtered.length / itemsPerPage));
  const currentItems = filtered.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage,
  );

  const handleRowClick = (role: Role) => {
    if (!isRoot) return;
    setSelectedRole(role);
    setIsModalOpen(true);
  };

  const handleOpenCreate = () => {
    setSelectedRole(null);
    setIsModalOpen(true);
  };

  const handleSave = async (payload: RoleFormPayload) => {
    try {
      let roleId: number;
      if (selectedRole) {
        const updated = await api.put<Role>(`/api/role/${selectedRole.id}`, {
          roleName: payload.roleName,
          description: payload.description,
        });
        roleId = updated.id;
      } else {
        const created = await api.post<Role>('/api/role', {
          roleName: payload.roleName,
          description: payload.description,
        });
        roleId = created.id;
      }

      await api.put(`/api/role/${roleId}/permissions`, {
        permissionIds: payload.permissionIds,
      });

      setIsModalOpen(false);
      setCurrentPage(1);
      await fetchRoles();
    } catch (e) {
      if (e instanceof ApiError) {
        alert(`저장에 실패했습니다: ${e.message}`);
      } else {
        alert('저장에 실패했습니다.');
      }
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await api.delete(`/api/role/${id}`);
      setIsModalOpen(false);
      await fetchRoles();
    } catch (e) {
      if (e instanceof ApiError) {
        alert(`삭제에 실패했습니다: ${e.message}`);
      }
    }
  };

  const columns: TableColumn<Role>[] = [
    { header: 'No', render: (r) => r.id, width: '60px' },
    { header: '역할', render: (r) => <strong>{r.roleName}</strong>, width: '160px' },
    { header: '설명', render: (r) => r.description ?? '-' },
    {
      header: '권한 목록',
      render: (r) => (
        <span style={{ display: 'flex', flexWrap: 'wrap', gap: '4px' }}>
          {r.permissions.slice(0, 3).map((p) => (
            <Badge key={p.id} variant="info">{p.permissionName}</Badge>
          ))}
          {r.permissions.length > 3 && (
            <Badge variant="info">+{r.permissions.length - 3}</Badge>
          )}
        </span>
      ),
    },
    { header: '권한 수', render: (r) => `${r.permissions.length}개`, width: '80px' },
    { header: '등록일', render: (r) => formatDateTime(r.createdAt), width: '180px' },
    { header: '수정일', render: (r) => formatDateTime(r.updatedAt), width: '180px' },
  ];

  const searchOptions = [{ value: 'name', label: '역할' }];

  if (loading) return null;

  return (
    <>
      {errorMsg && (
        <div style={{ padding: '12px 16px', margin: '0 0 12px', background: '#fee', color: '#c33', borderRadius: 4 }}>
          {errorMsg}
        </div>
      )}

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
          onRowClick: isRoot ? handleRowClick : undefined,
        }}
        pagination={{ currentPage, totalPages, onPageChange: setCurrentPage }}
        extraActions={
          isRoot ? (
            <Button style={{ width: 'auto' }} onClick={handleOpenCreate}>
              + 권한 추가
            </Button>
          ) : undefined
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
