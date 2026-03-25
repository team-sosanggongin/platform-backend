'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { Card, Button, Container } from '../../../components';
import styles from './locked.module.css';

interface FailureRecord {
  attempt: number;
  time: string;
  ip: string;
}

interface LockData {
  id: string;
  history: FailureRecord[];
}

export default function LockedPage() {
  const router = useRouter();
  const [lockData, setLockData] = useState<LockData | null>(null);

  useEffect(() => {
    const raw = sessionStorage.getItem('lockHistory');
    if (raw) {
      try {
        setLockData(JSON.parse(raw));
      } catch {
        // ignore
      }
    }
  }, []);

  const formatTime = (iso: string) =>
    new Date(iso).toLocaleString('ko-KR', {
      year: 'numeric', month: '2-digit', day: '2-digit',
      hour: '2-digit', minute: '2-digit', second: '2-digit',
    });

  return (
    <Container>
      <Card className={styles.cardWrapper}>
        <div className={styles.iconBadge}>
          <svg viewBox="0 0 24 24" fill="none" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" stroke="currentColor">
            <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
            <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
            <line x1="12" y1="16" x2="12" y2="16.01" strokeWidth="3"/>
          </svg>
        </div>

        <h1 className={styles.title}>계정이 잠겼습니다</h1>
        <p className={styles.subtitle}>
          {lockData?.id && <><strong>{lockData.id}</strong> 계정은 </>}
          비밀번호를 5회 이상 잘못 입력하여 보안을 위해 계정이 잠겼습니다.
          <br />
          관리자에게 문의하여 잠금을 해제받아야 합니다.
        </p>

        <div className={styles.contactBox}>
          <div className={styles.contactIcon}>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07A19.5 19.5 0 0 1 4.69 12 19.79 19.79 0 0 1 1.58 3.39 2 2 0 0 1 3.55 1h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 8.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/>
            </svg>
          </div>
          <div>
            <div className={styles.contactLabel}>관리자 문의</div>
            <div className={styles.contactValue}>admin@company.com</div>
          </div>
        </div>

        {lockData?.history && lockData.history.length > 0 && (
          <div className={styles.historySection}>
            <h3 className={styles.historyTitle}>로그인 실패 내역</h3>
            <table className={styles.historyTable}>
              <thead>
                <tr>
                  <th>시도</th>
                  <th>일시</th>
                  <th>IP 주소</th>
                </tr>
              </thead>
              <tbody>
                {lockData.history.map((row) => (
                  <tr key={row.attempt}>
                    <td>
                      <span className={styles.attemptBadge}>{row.attempt}회</span>
                    </td>
                    <td>{formatTime(row.time)}</td>
                    <td className={styles.ipCell}>{row.ip}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        <Button
          variant="secondary"
          style={{ marginTop: '1.5rem' }}
          onClick={() => router.push('/login')}
        >
          로그인 페이지로 돌아가기
        </Button>
      </Card>
    </Container>
  );
}
