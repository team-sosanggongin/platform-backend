'use client';

import { useState, useEffect } from 'react';
import { useRouter, useParams } from 'next/navigation';
import Link from 'next/link';
import { User } from '../../../../types';
import { Card, Button, Badge } from '../../../../components';
import styles from './edit.module.css';

export default function UserDetailPage() {
  const router = useRouter();
  const params = useParams();
  const userId = params.id as string;

  // Mock data
  const allUsers: User[] = [
    { id: '1', name: '김관리', email: 'admin@example.com', role: 'Admin', joinDate: '2023-01-15', status: 'Active' },
    { id: '2', name: '이매니저', email: 'manager@example.com', role: 'Manager', joinDate: '2023-03-22', status: 'Active' },
    { id: '3', name: '박편집', email: 'editor1@example.com', role: 'Editor', joinDate: '2023-06-10', status: 'Inactive' },
    { id: '4', name: '최수정', email: 'editor2@example.com', role: 'Editor', joinDate: '2023-08-05', status: 'Active' },
    { id: '5', name: '정운영', email: 'op@example.com', role: 'Manager', joinDate: '2023-11-12', status: 'Active' },
    { id: '6', name: '한보안', email: 'security@example.com', role: 'Admin', joinDate: '2024-01-20', status: 'Active' },
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
        <Link href="/users" className={styles.backLink}>
          <span>&larr;</span> 사용자 목록
        </Link>
      </nav>

      <Card className={styles.formCard}>
        <div className={styles.cardHeader}>
          <h2 className={styles.cardTitle}>사용자 상세 정보</h2>
        </div>
        
        <div className={styles.cardBody}>
          <div className={styles.formRow}>
            <div className={styles.rowLabel}>사용자 ID</div>
            <div className={styles.rowContent}>
              <span className={styles.readValue}>{user.id}</span>
            </div>
          </div>

          <div className={styles.formRow}>
            <div className={styles.rowLabel}>이름</div>
            <div className={styles.rowContent}>
              <span className={styles.readValue}>{user.name}</span>
            </div>
          </div>

          <div className={styles.formRow}>
            <div className={styles.rowLabel}>이메일 주소</div>
            <div className={styles.rowContent}>
              <span className={styles.readValue}>{user.email}</span>
            </div>
          </div>

          <div className={styles.formRow}>
            <div className={styles.rowLabel}>접근 권한</div>
            <div className={styles.rowContent}>
              <span className={styles.readValue}>{user.role}</span>
            </div>
          </div>

          <div className={styles.formRow}>
            <div className={styles.rowLabel}>계정 상태</div>
            <div className={styles.rowContent}>
              <Badge variant={user.status === 'Active' ? 'success' : 'error'}>
                {user.status === 'Active' ? '활성' : '비활성'}
              </Badge>
            </div>
          </div>

          <div className={styles.formRow}>
            <div className={styles.rowLabel}>가입일</div>
            <div className={styles.rowContent}>
              <span className={styles.readValue}>{user.joinDate}</span>
            </div>
          </div>

          <div className={styles.footer}>
            <Button 
              type="button" 
              variant="primary"
              className={styles.saveButton}
              onClick={() => router.push('/users')}
            >
              목록으로 돌아가기
            </Button>
          </div>
        </div>
      </Card>
    </div>
  );
}
