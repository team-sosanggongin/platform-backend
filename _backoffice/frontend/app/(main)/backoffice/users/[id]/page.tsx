'use client';

import { useState, useEffect } from 'react';
import { useRouter, useParams } from 'next/navigation';
import Link from 'next/link';
import { User } from '@/types';
import styles from './edit.module.css';
import {Badge, Button, Card, DetailRow} from "@/components";

export default function UserDetailPage() {
  const router = useRouter();
  const params = useParams();
  const userId = params.id as string;

  // Mock data
  const allUsers: User[] = [
    { id: '1', name: '김관리', email: 'admin@example.com', roles: ['Admin'], joinDate: '2023-01-15', status: 'Active' },
    { id: '2', name: '이매니저', email: 'manager@example.com', roles: ['Manager'], joinDate: '2023-03-22', status: 'Active' },
    { id: '3', name: '박편집', email: 'editor1@example.com', roles: ['Editor'], joinDate: '2023-06-10', status: 'Inactive' },
    { id: '4', name: '최수정', email: 'editor2@example.com', roles: ['Editor'], joinDate: '2023-08-05', status: 'Active' },
    { id: '5', name: '정운영', email: 'op@example.com', roles: ['Manager'], joinDate: '2023-11-12', status: 'Active' },
    { id: '6', name: '한보안', email: 'security@example.com', roles: ['Admin', 'Manager'], joinDate: '2024-01-20', status: 'Active' },
  ];

  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    const foundUser = allUsers.find(u => u.id === userId);
    if (foundUser) {
      setUser(foundUser);
    }
  }, [userId]);

  if (!user) {
    return <div className={styles.container}>사용자를 찾을 수 없습니다.</div>;
  }

  return (
    <div className={styles.container}>
      <nav className={styles.breadcrumb}>
        <Link href="/backoffice/users" className={styles.backLink}>
          <span>&larr;</span> 사용자 목록
        </Link>
      </nav>

      <Card className={styles.formCard}>
        <div className={styles.cardHeader}>
          <h2 className={styles.cardTitle}>사용자 상세 정보</h2>
        </div>
        
        <div className={styles.cardBody}>
          <DetailRow label="사용자 ID">{user.id}</DetailRow>
          <DetailRow label="이름">{user.name}</DetailRow>
          <DetailRow label="이메일 주소">{user.email}</DetailRow>
          <DetailRow label="접근 권한">
            <span style={{ display: 'flex', gap: '6px', flexWrap: 'wrap' }}>
              {user.roles.map((r) => (
                <Badge key={r} variant="info">{r}</Badge>
              ))}
            </span>
          </DetailRow>
          <DetailRow label="계정 상태">
            {user.status === 'Active' && <Badge variant="success">활성</Badge>}
            {user.status === 'Inactive' && <Badge variant="error">비활성</Badge>}
            {user.status === 'Pending' && <Badge variant="warning">대기중</Badge>}
          </DetailRow>
          <DetailRow label="가입일">{user.joinDate}</DetailRow>

          <div className={styles.footer}>
            <Button 
              type="button" 
              variant="primary"
              className={styles.saveButton}
              onClick={() => router.push('/backoffice/users')}
            >
              목록으로 돌아가기
            </Button>
          </div>
        </div>
      </Card>
    </div>
  );
}
