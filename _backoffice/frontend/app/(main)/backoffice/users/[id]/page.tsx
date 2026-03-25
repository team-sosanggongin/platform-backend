'use client';

import { useState, useEffect } from 'react';
import { useRouter, useParams } from 'next/navigation';
import Link from 'next/link';
import { User } from '@/types';
import styles from './edit.module.css';
import { Badge, Button, Card, DetailRow } from '@/components';
import { ConfirmModal } from '@/components/molecules/ConfirmModal/ConfirmModal';

const ALL_USERS: User[] = [
  { id: '1', name: '김관리', email: 'admin@example.com', roles: ['Admin'], joinDate: '2023-01-15', status: 'Active' },
  { id: '2', name: '이매니저', email: 'manager@example.com', roles: ['Manager'], joinDate: '2023-03-22', status: 'Active' },
  { id: '3', name: '박편집', email: 'editor1@example.com', roles: ['Editor'], joinDate: '2023-06-10', status: 'Inactive' },
  { id: '4', name: '최수정', email: 'editor2@example.com', roles: ['Editor'], joinDate: '2023-08-05', status: 'Active' },
  { id: '5', name: '정운영', email: 'op@example.com', roles: ['Manager'], joinDate: '2023-11-12', status: 'Active' },
  { id: '6', name: '한보안', email: 'security@example.com', roles: ['Admin', 'Manager'], joinDate: '2024-01-20', status: 'Active' },
  { id: '13', name: '오신입', email: 'new1@example.com', phone: '010-1234-5678', roles: ['Editor'], joinDate: '2024-03-20', status: 'Pending' },
  { id: '14', name: '권대기', email: 'new2@example.com', phone: '010-9876-5432', roles: ['Manager'], joinDate: '2024-03-21', status: 'Pending' },
  { id: '15', name: '류잠금', email: 'locked1@example.com', phone: '010-5555-1234', roles: ['Editor'], joinDate: '2024-01-08', status: 'Locked', lockedAt: '2024-03-24T14:32:00' },
  { id: '16', name: '노보안', email: 'locked2@example.com', phone: '010-7777-9999', roles: ['Manager'], joinDate: '2023-11-05', status: 'Locked', lockedAt: '2024-03-25T09:15:00' },
];

export default function UserDetailPage() {
  const router = useRouter();
  const params = useParams();
  const userId = params.id as string;

  const [user, setUser] = useState<User | null>(null);
  const [isUnlockModalOpen, setIsUnlockModalOpen] = useState(false);
  const [unlockSuccess, setUnlockSuccess] = useState(false);

  useEffect(() => {
    const found = ALL_USERS.find((u) => u.id === userId);
    if (found) setUser(found);
  }, [userId]);

  const handleUnlock = () => {
    if (!user) return;
    setUser({ ...user, status: 'Active', lockedAt: undefined });
    setIsUnlockModalOpen(false);
    setUnlockSuccess(true);
    setTimeout(() => setUnlockSuccess(false), 3000);
  };

  const formatDateTime = (iso?: string) => {
    if (!iso) return '-';
    return new Date(iso).toLocaleString('ko-KR', {
      year: 'numeric', month: '2-digit', day: '2-digit',
      hour: '2-digit', minute: '2-digit',
    });
  };

  if (!user) {
    return <div className={styles.container}>사용자를 찾을 수 없습니다.</div>;
  }

  const isLocked = user.status === 'Locked';

  return (
    <div className={styles.container}>
      <nav className={styles.breadcrumb}>
        <Link href="/backoffice/users" className={styles.backLink}>
          <span>&larr;</span> 사용자 목록
        </Link>
      </nav>

      {unlockSuccess && (
        <div className={styles.successBanner}>
          계정 잠금이 해제되었습니다. 사용자가 다시 로그인할 수 있습니다.
        </div>
      )}

      <Card className={styles.formCard}>
        <div className={styles.cardHeader}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <h2 className={styles.cardTitle}>사용자 상세 정보</h2>
            {isLocked && (
              <Button variant="danger" style={{ width: 'auto', padding: '0 16px', height: 36 }} onClick={() => setIsUnlockModalOpen(true)}>
                잠금 해제
              </Button>
            )}
          </div>
        </div>

        <div className={styles.cardBody}>
          <DetailRow label="사용자 ID">{user.id}</DetailRow>
          <DetailRow label="이름">{user.name}</DetailRow>
          <DetailRow label="이메일 주소">{user.email}</DetailRow>
          {user.phone && <DetailRow label="전화번호">{user.phone}</DetailRow>}
          <DetailRow label="접근 권한">
            <span style={{ display: 'flex', gap: '6px', flexWrap: 'wrap' }}>
              {user.roles.map((r) => (
                <Badge key={r} variant="info">{r}</Badge>
              ))}
            </span>
          </DetailRow>
          <DetailRow label="계정 상태">
            <span style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              {user.status === 'Active' && <Badge variant="success">활성</Badge>}
              {user.status === 'Inactive' && <Badge variant="error">비활성</Badge>}
              {user.status === 'Pending' && <Badge variant="warning">대기중</Badge>}
              {user.status === 'Locked' && <Badge variant="error">잠금</Badge>}
              {isLocked && (
                <span className={styles.lockReason}>
                  비밀번호 5회 오류로 잠금됨
                </span>
              )}
            </span>
          </DetailRow>
          {isLocked && user.lockedAt && (
            <DetailRow label="잠금 시각">{formatDateTime(user.lockedAt)}</DetailRow>
          )}
          <DetailRow label="가입일">{user.joinDate}</DetailRow>

          <div className={styles.footer}>
            <Button
              type="button"
              variant="secondary"
              style={{ width: 130 }}
              onClick={() => router.push('/backoffice/users')}
            >
              목록으로 돌아가기
            </Button>
          </div>
        </div>
      </Card>

      <ConfirmModal
        isOpen={isUnlockModalOpen}
        onClose={() => setIsUnlockModalOpen(false)}
        onConfirm={handleUnlock}
        title="계정 잠금 해제"
      >
        <strong>{user.name}</strong> 님의 계정 잠금을 해제하시겠습니까?
        <br />
        <span style={{ color: '#64748b', fontSize: '0.875rem' }}>
          잠금 해제 후 사용자는 즉시 로그인이 가능합니다.
        </span>
      </ConfirmModal>
    </div>
  );
}
