'use client';

import React, { useState, useEffect } from 'react';
import { FormModal, Input, Button } from '@/components';
import { Role, RolePermission } from '@/types';
import styles from './roles.module.css';

interface RoleFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (data: Omit<Role, 'id' | 'createdAt'>) => void;
  onDelete?: (id: string) => void;
  role?: Role | null;
}

let permIdCounter = 100;

const emptyForm = { name: '', description: '' };

export const RoleFormModal: React.FC<RoleFormModalProps> = ({
  isOpen,
  onClose,
  onSave,
  onDelete,
  role,
}) => {
  const [form, setForm] = useState(emptyForm);
  const [permissions, setPermissions] = useState<RolePermission[]>([]);

  useEffect(() => {
    if (role) {
      setForm({ name: role.name, description: role.description });
      setPermissions(role.permissions);
    } else {
      setForm(emptyForm);
      setPermissions([]);
    }
  }, [role, isOpen]);

  const addPermission = () => {
    setPermissions((prev) => [...prev, { id: String(permIdCounter++), permission: '' }]);
  };

  const updatePermission = (id: string, value: string) => {
    setPermissions((prev) => prev.map((p) => (p.id === id ? { ...p, permission: value } : p)));
  };

  const removePermission = (id: string) => {
    setPermissions((prev) => prev.filter((p) => p.id !== id));
  };

  const handleSave = () => {
    if (!form.name.trim()) {
      alert('권한명을 입력해주세요.');
      return;
    }
    if (permissions.some((p) => !p.permission.trim())) {
      alert('비어있는 권한 항목이 있습니다.');
      return;
    }
    onSave({
      name: form.name,
      description: form.description,
      permissions,
      updatedAt: new Date().toISOString().slice(0, 16).replace('T', ' '),
    });
    onClose();
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
              confirmMessage: `"${role?.name}" 권한을 삭제하시겠습니까?`,
            }
          : undefined
      }
    >
      <Input
        label="권한명"
        id="role-name"
        value={form.name}
        onChange={(e) => setForm({ ...form, name: e.target.value })}
        placeholder="예: 콘텐츠 편집자"
      />
      <Input
        label="설명"
        id="role-desc"
        value={form.description}
        onChange={(e) => setForm({ ...form, description: e.target.value })}
        placeholder="이 권한의 역할을 간략히 설명해주세요"
      />

      <div className={styles.permSection}>
        <div className={styles.permHeader}>
          <span className={styles.permTitle}>권한 목록 ({permissions.length})</span>
          <Button style={{ width: 'auto' }} onClick={addPermission}>+ 추가</Button>
        </div>

        <div className={styles.permList}>
          {permissions.length === 0 ? (
            <div className={styles.emptyPerm}>
              등록된 권한이 없습니다. 추가 버튼을 눌러 권한을 입력하세요.
            </div>
          ) : (
            permissions.map((perm) => (
              <div key={perm.id} className={styles.permRow}>
                <Input
                  id={`perm-${perm.id}`}
                  value={perm.permission}
                  onChange={(e) => updatePermission(perm.id, e.target.value)}
                  placeholder="예: read.notices, *.members"
                />
                <button
                  className={styles.removeBtn}
                  onClick={() => removePermission(perm.id)}
                  title="삭제"
                >
                  ×
                </button>
              </div>
            ))
          )}
        </div>
      </div>
    </FormModal>
  );
};
