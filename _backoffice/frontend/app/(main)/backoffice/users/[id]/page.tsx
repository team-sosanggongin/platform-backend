'use client';

import { useState, useEffect } from 'react';
import { useRouter, useParams } from 'next/navigation';
import Link from 'next/link';
import { User } from '@/types';
import styles from './edit.module.css';
import { Badge, Button, Card, DetailRow, Input } from '@/components';
import { ConfirmModal } from '@/components/molecules/ConfirmModal/ConfirmModal';
import { api, ApiError } from '@/lib/api';

export default function UserDetailPage() {
  const router = useRouter();
  const params = useParams();
  const userId = params.id as string;

  const [user, setUser] = useState<User | null>(null);
  const [isRoot, setIsRoot] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [editForm, setEditForm] = useState({ name: '', email: '', phoneNumber: '' });
  const [isUnlockModalOpen, setIsUnlockModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [unlockSuccess, setUnlockSuccess] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  useEffect(() => {
    api.get<User>('/api/account/me')
      .then((me) => {
        if (!me.root) {
          router.push('/backoffice/users');
          return;
        }
        setIsRoot(true);
        return api.get<User>(`/api/account/${userId}`);
      })
      .then((data) => {
        if (!data) return;
        setUser(data);
        setEditForm({
          name: data.name,
          email: data.email ?? '',
          phoneNumber: data.phoneNumber ?? '',
        });
      })
      .catch(console.error);
  }, [userId]);

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

  if (!user) {
    return <div className={styles.container}>로딩 중...</div>;
  }

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
            {isRoot && (
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