'use client';

import { useState, useEffect, useCallback } from 'react';
import { useRouter, useParams } from 'next/navigation';
import Link from 'next/link';
import { Role, User } from '@/types';
import styles from './edit.module.css';
import { Badge, Button, Card, DetailRow, Input } from '@/components';
import { ConfirmModal } from '@/components/molecules/ConfirmModal/ConfirmModal';
import { api, ApiError } from '@/lib/api';
import { useAuth } from '@/lib/auth-context';

export default function UserDetailPage() {
  const router = useRouter();
  const params = useParams();
  const userId = params.id as string;
  const { me, loading } = useAuth();

  const [user, setUser] = useState<User | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [editForm, setEditForm] = useState({ name: '', email: '', phoneNumber: '' });
  const [isUnlockModalOpen, setIsUnlockModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [unlockSuccess, setUnlockSuccess] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');
  const [allRoles, setAllRoles] = useState<Role[]>([]);

  const [isEditingRoles, setIsEditingRoles] = useState(false);
  const [editingRoleIds, setEditingRoleIds] = useState<Set<number>>(new Set());
  const [roleSaving, setRoleSaving] = useState(false);

  const loadUser = useCallback(async () => {
    const data = await api.get<User>(`/api/account/${userId}`);
    setUser(data);
    setEditForm({
      name: data.name,
      email: data.email ?? '',
      phoneNumber: data.phoneNumber ?? '',
    });
    return data;
  }, [userId]);

  useEffect(() => {
    if (loading) return;
    if (!me?.root) {
      router.push('/backoffice');
      return;
    }
    Promise.all([loadUser(), api.get<Role[]>('/api/role')])
      .then(([, roles]) => setAllRoles(roles))
      .catch((e) => {
        if (e instanceof ApiError) setErrorMsg(e.message);
      });
  }, [me, loading, loadUser, router]);

  const handleUnlock = async () => {
    if (!user) return;
    try {
      await api.patch(`/api/account/${userId}/unlock`);
      setUser({ ...user, locked: false, lockedAt: null });
      setIsUnlockModalOpen(false);
      setUnlockSuccess(true);
      setTimeout(() => setUnlockSuccess(false), 3000);
    } catch (e) {
      if (e instanceof ApiError) setErrorMsg(e.message);
    }
  };

  const handleSave = async () => {
    if (!user) return;
    try {
      const updated = await api.patch<User>(`/api/account/${userId}`, {
        name: editForm.name,
        email: editForm.email || null,
        phoneNumber: editForm.phoneNumber || null,
      });
      setUser(updated);
      setIsEditing(false);
    } catch (e) {
      if (e instanceof ApiError) setErrorMsg(e.message);
    }
  };

  const handleDelete = async () => {
    try {
      await api.delete(`/api/account/${userId}`);
      router.push('/backoffice/users');
    } catch (e) {
      if (e instanceof ApiError) setErrorMsg(e.message);
    }
  };

  const originalRoleIds = new Set((user?.roles ?? []).map((r) => r.id));

  const handleStartEditRoles = () => {
    setEditingRoleIds(new Set(originalRoleIds));
    setIsEditingRoles(true);
  };

  const handleCancelEditRoles = () => {
    setEditingRoleIds(new Set(originalRoleIds));
    setIsEditingRoles(false);
  };

  const handleToggleEditingRole = (roleId: number) => {
    setEditingRoleIds((prev) => {
      const next = new Set(prev);
      if (next.has(roleId)) next.delete(roleId);
      else next.add(roleId);
      return next;
    });
  };

  const handleSaveRoles = async () => {
    const added = Array.from(editingRoleIds).filter((id) => !originalRoleIds.has(id));
    const removed = Array.from(originalRoleIds).filter((id) => !editingRoleIds.has(id));

    if (added.length === 0 && removed.length === 0) {
      setIsEditingRoles(false);
      return;
    }

    setRoleSaving(true);
    try {
      const requests: Promise<unknown>[] = [
        ...added.map((roleId) => api.post(`/api/role/${roleId}/account/${userId}`)),
        ...removed.map((roleId) => api.delete(`/api/role/${roleId}/account/${userId}`)),
      ];
      await Promise.all(requests);
      await loadUser();
      setIsEditingRoles(false);
    } catch (e) {
      if (e instanceof ApiError) {
        setErrorMsg(e.message);
      } else {
        alert('역할 저장에 실패했습니다.');
      }
    } finally {
      setRoleSaving(false);
    }
  };

  if (!user) {
    return <div className={styles.container}>로딩 중...</div>;
  }

  const displayRoleIds = isEditingRoles ? editingRoleIds : originalRoleIds;
  const hasRoleChanges = isEditingRoles && (
    Array.from(editingRoleIds).some((id) => !originalRoleIds.has(id)) ||
    Array.from(originalRoleIds).some((id) => !editingRoleIds.has(id))
  );

  return (
    <div className={styles.container}>
      <nav className={styles.breadcrumb}>
        <Link href="/backoffice/users" className={styles.backLink}>
          <span>&larr;</span> 사용자 목록
        </Link>
      </nav>

      {unlockSuccess && (
        <div className={styles.successBanner}>
          계정 잠금이 해제되었습니다.
        </div>
      )}

      {errorMsg && (
        <div className={styles.errorBanner}>{errorMsg}</div>
      )}

      <Card className={styles.formCard}>
        <div className={styles.cardHeader}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <h2 className={styles.cardTitle}>사용자 상세 정보</h2>
            {me?.root && (
              <div style={{ display: 'flex', gap: '8px' }}>
                {user.locked && (
                  <Button variant="danger" style={{ width: 'auto', padding: '0 16px', height: 36 }}
                    onClick={() => setIsUnlockModalOpen(true)}>
                    잠금 해제
                  </Button>
                )}
                {!isEditing ? (
                  <>
                    <Button style={{ width: 'auto', padding: '0 16px', height: 36 }}
                      onClick={() => setIsEditing(true)}>
                      수정
                    </Button>
                    <Button variant="danger" style={{ width: 'auto', padding: '0 16px', height: 36 }}
                      onClick={() => setIsDeleteModalOpen(true)}>
                      삭제
                    </Button>
                  </>
                ) : (
                  <>
                    <Button variant="success" style={{ width: 'auto', padding: '0 16px', height: 36 }}
                      onClick={handleSave}>
                      저장
                    </Button>
                    <Button variant="secondary" style={{ width: 'auto', padding: '0 16px', height: 36 }}
                      onClick={() => setIsEditing(false)}>
                      취소
                    </Button>
                  </>
                )}
              </div>
            )}
          </div>
        </div>

        <div className={styles.cardBody}>
          <DetailRow label="UUID">{user.id}</DetailRow>
          <DetailRow label="아이디">{user.loginId}</DetailRow>
          <DetailRow label="이름">
            {isEditing ? (
              <Input id="edit-name" value={editForm.name}
                onChange={(e) => setEditForm({ ...editForm, name: e.target.value })}
                wrapperStyle={{ marginBottom: 0, maxWidth: 400 }} />
            ) : user.name}
          </DetailRow>
          <DetailRow label="이메일">
            {isEditing ? (
              <Input id="edit-email" value={editForm.email}
                onChange={(e) => setEditForm({ ...editForm, email: e.target.value })}
                placeholder="이메일 (선택)"
                wrapperStyle={{ marginBottom: 0, maxWidth: 400 }} />
            ) : user.email ?? '-'}
          </DetailRow>
          <DetailRow label="전화번호">
            {isEditing ? (
              <Input id="edit-phone" value={editForm.phoneNumber}
                onChange={(e) => setEditForm({ ...editForm, phoneNumber: e.target.value })}
                placeholder="전화번호 (선택)"
                wrapperStyle={{ marginBottom: 0, maxWidth: 400 }} />
            ) : user.phoneNumber ?? '-'}
          </DetailRow>
          <DetailRow label="계정 상태">
            <span style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              {user.locked && <Badge variant="error">잠금</Badge>}
              {!user.locked && user.passwordExpired && <Badge variant="warning">임시 비밀번호</Badge>}
              {!user.locked && !user.passwordExpired && <Badge variant="success">활성</Badge>}
              {user.locked && (
                <span className={styles.lockReason}>비밀번호 5회 오류로 잠금됨</span>
              )}
            </span>
          </DetailRow>
          {user.lockedAt && (
            <DetailRow label="잠금 시각">{new Date(user.lockedAt).toLocaleString('ko-KR')}</DetailRow>
          )}
          <DetailRow label="등록일">{user.createdAt}</DetailRow>

          {!isEditing && (
            <div className={styles.footer}>
              <Button variant="secondary" style={{ width: 130 }}
                onClick={() => router.push('/backoffice/users')}>
                목록으로 돌아가기
              </Button>
            </div>
          )}
        </div>
      </Card>

      <Card className={styles.formCard} style={{ marginTop: 16 }}>
        <div className={styles.cardHeader}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <h2 className={styles.cardTitle}>할당된 역할</h2>
            {!user.root && allRoles.length > 0 && (
              <div style={{ display: 'flex', gap: '8px' }}>
                {!isEditingRoles ? (
                  <Button style={{ width: 'auto', padding: '0 16px', height: 36 }}
                    onClick={handleStartEditRoles}>
                    역할 편집
                  </Button>
                ) : (
                  <>
                    <Button variant="success" style={{ width: 'auto', padding: '0 16px', height: 36 }}
                      onClick={handleSaveRoles}
                      disabled={roleSaving || !hasRoleChanges}>
                      {roleSaving ? '저장 중...' : '저장'}
                    </Button>
                    <Button variant="secondary" style={{ width: 'auto', padding: '0 16px', height: 36 }}
                      onClick={handleCancelEditRoles}
                      disabled={roleSaving}>
                      취소
                    </Button>
                  </>
                )}
              </div>
            )}
          </div>
        </div>
        <div className={styles.cardBody}>
          {user.root ? (
            <div style={{ padding: '16px', color: '#64748b', fontSize: '0.875rem' }}>
              ROOT 계정은 모든 권한을 보유하므로 별도의 역할 할당이 필요하지 않습니다.
            </div>
          ) : allRoles.length === 0 ? (
            <div style={{ padding: '16px', color: '#64748b', fontSize: '0.875rem' }}>
              등록된 역할이 없습니다. 먼저 권한 관리 화면에서 역할을 생성하세요.
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
              {allRoles.map((role) => {
                const checked = displayRoleIds.has(role.id);
                const disabled = !isEditingRoles || roleSaving;
                return (
                  <label
                    key={role.id}
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: 10,
                      padding: '10px 12px',
                      border: `1px solid ${isEditingRoles && checked !== originalRoleIds.has(role.id) ? '#3b82f6' : '#eee'}`,
                      borderRadius: 4,
                      cursor: disabled ? 'default' : 'pointer',
                      backgroundColor: isEditingRoles && checked !== originalRoleIds.has(role.id) ? '#eff6ff' : 'transparent',
                      transition: 'all 0.15s',
                    }}
                  >
                    <input
                      type="checkbox"
                      checked={checked}
                      disabled={disabled}
                      onChange={() => handleToggleEditingRole(role.id)}
                      style={{ width: 16, height: 16, cursor: disabled ? 'default' : 'pointer' }}
                    />
                    <span style={{ fontWeight: 600, color: '#0f172a', minWidth: 140 }}>
                      {role.roleName}
                    </span>
                    <span style={{ fontSize: 12, color: '#64748b' }}>
                      {role.description ?? '-'}
                    </span>
                  </label>
                );
              })}
            </div>
          )}
        </div>
      </Card>

      <ConfirmModal
        isOpen={isUnlockModalOpen}
        onClose={() => setIsUnlockModalOpen(false)}
        onConfirm={handleUnlock}
        title="계정 잠금 해제"
      >
        <strong>{user.name}</strong> 님의 계정 잠금을 해제하시겠습니까?
      </ConfirmModal>

      <ConfirmModal
        isOpen={isDeleteModalOpen}
        onClose={() => setIsDeleteModalOpen(false)}
        onConfirm={handleDelete}
        title="계정 삭제"
      >
        <strong>{user.name}</strong> 님의 계정을 삭제하시겠습니까?
        <br />
        <span style={{ color: '#64748b', fontSize: '0.875rem' }}>
          삭제된 계정은 목록에서 숨겨집니다.
        </span>
      </ConfirmModal>
    </div>
  );
}
