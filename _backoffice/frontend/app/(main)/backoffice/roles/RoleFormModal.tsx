'use client';

import React, { useEffect, useMemo, useState } from 'react';
import { FormModal, Input } from '@/components';
import { Permission, Role } from '@/types';
import { api, ApiError } from '@/lib/api';
import styles from './roles.module.css';

export interface RoleFormPayload {
  roleName: string;
  description: string;
  permissionIds: number[];
}

interface RoleFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (data: RoleFormPayload) => Promise<void> | void;
  onDelete?: (id: number) => void;
  role?: Role | null;
}

const emptyForm = { roleName: '', description: '' };

export const RoleFormModal: React.FC<RoleFormModalProps> = ({
  isOpen,
  onClose,
  onSave,
  onDelete,
  role,
}) => {
  const [form, setForm] = useState(emptyForm);
  const [allPermissions, setAllPermissions] = useState<Permission[]>([]);
  const [selectedIds, setSelectedIds] = useState<Set<number>>(new Set());
  const [loadError, setLoadError] = useState('');

  useEffect(() => {
    if (!isOpen) return;
    api.get<Permission[]>('/api/permission')
      .then(setAllPermissions)
      .catch((e) => {
        if (e instanceof ApiError) setLoadError(e.message);
      });
  }, [isOpen]);

  useEffect(() => {
    if (role) {
      setForm({ roleName: role.roleName, description: role.description ?? '' });
      setSelectedIds(new Set(role.permissions.map((p) => p.id)));
    } else {
      setForm(emptyForm);
      setSelectedIds(new Set());
    }
  }, [role, isOpen]);

  const grouped = useMemo(() => {
    const map = new Map<string, Permission[]>();
    allPermissions.forEach((p) => {
      const key = p.permDomain ?? 'etc';
      if (!map.has(key)) map.set(key, []);
      map.get(key)!.push(p);
    });
    return Array.from(map.entries()).sort(([a], [b]) => a.localeCompare(b));
  }, [allPermissions]);

  const togglePermission = (id: number) => {
    setSelectedIds((prev) => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id);
      else next.add(id);
      return next;
    });
  };

  const handleSave = async () => {
    if (!form.roleName.trim()) {
      alert('역할명을 입력해주세요.');
      return;
    }
    await onSave({
      roleName: form.roleName.trim(),
      description: form.description.trim(),
      permissionIds: Array.from(selectedIds),
    });
  };

  const isEdit = !!role;

  return (
    <FormModal
      isOpen={isOpen}
      onClose={onClose}
      title={isEdit ? '권한 수정' : '권한 추가'}
      actions={[
        { label: '취소', variant: 'secondary', onClick: onClose },
        { label: '저장', variant: 'success', onClick: handleSave },
      ]}
      deleteConfig={
        isEdit
          ? {
              onDelete: () => onDelete?.(role!.id),
              confirmTitle: '권한 삭제',
              confirmMessage: `"${role?.roleName}" 역할을 삭제하시겠습니까?`,
            }
          : undefined
      }
    >
      <Input
        label="역할명"
        id="role-name"
        value={form.roleName}
        onChange={(e) => setForm({ ...form, roleName: e.target.value })}
        placeholder="예: 콘텐츠 편집자"
      />
      <Input
        label="설명"
        id="role-desc"
        value={form.description}
        onChange={(e) => setForm({ ...form, description: e.target.value })}
        placeholder="이 역할의 책임을 간략히 설명해주세요"
      />

      <div className={styles.permSection}>
        <div className={styles.permHeader}>
          <span className={styles.permTitle}>
            권한 선택 ({selectedIds.size}/{allPermissions.length})
          </span>
        </div>

        {loadError && (
          <div className={styles.emptyPerm} style={{ color: '#c33', borderColor: '#f99' }}>
            권한 목록을 불러오지 못했습니다: {loadError}
          </div>
        )}

        {!loadError && grouped.length === 0 ? (
          <div className={styles.emptyPerm}>등록된 권한이 없습니다.</div>
        ) : (
          grouped.map(([domain, perms]) => (
            <div key={domain} className={styles.permGroup}>
              <div className={styles.permGroupTitle}>{domain}</div>
              <div className={styles.permGroupBody}>
                {perms.map((p) => (
                  <label key={p.id} className={styles.permCheckItem}>
                    <input
                      type="checkbox"
                      checked={selectedIds.has(p.id)}
                      onChange={() => togglePermission(p.id)}
                    />
                    <span>{p.permissionName}</span>
                  </label>
                ))}
              </div>
            </div>
          ))
        )}
      </div>
    </FormModal>
  );
};
