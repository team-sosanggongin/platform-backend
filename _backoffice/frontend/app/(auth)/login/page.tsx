'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { Card, Input, Button, Container, Form } from '../../../components';
import { api } from '@/lib/api';
import styles from './login.module.css';

interface LoginResponseData {
  accountId: string;
  name: string;
  passwordExpired: boolean;
}

export default function LoginPage() {
  const [id, setId] = useState('');
  const [password, setPassword] = useState('');
  const [errorMsg, setErrorMsg] = useState('');
  const [loading, setLoading] = useState(true);
  const router = useRouter();

  // 이미 로그인된 상태면 /backoffice로 이동
  useEffect(() => {
    api.get<LoginResponseData>('/api/auth/me').then((res) => {
      if (res.ok) {
        router.replace('/backoffice');
      } else {
        setLoading(false);
      }
    });
  }, [router]);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!id || !password) return;

    setErrorMsg('');

    const res = await api.post<LoginResponseData>('/api/auth/login', {
      loginId: id,
      password,
    });

    if (res.ok && res.data) {
      if (res.data.passwordExpired) {
        router.push('/login/change-password');
      } else {
        router.push('/backoffice');
      }
      return;
    }

    // 403: 계정 잠금
    if (res.status === 403) {
      router.push('/locked');
      return;
    }

    // 401 등: 에러 메시지 표시
    setErrorMsg(res.message ?? '로그인에 실패했습니다.');
  };

  if (loading) return null;

  return (
    <Container>
      <Card className={styles.cardWrapper}>
        <div className={styles.header}>
          <div className={styles.logo}>
            <div className={styles.logoMark}>
              <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" fill="none"/>
              </svg>
            </div>
            <span className={styles.logoText}>Backoffice</span>
          </div>
          <h1 className={styles.title}>관리자 로그인</h1>
          <p className={styles.subtitle}>계정 정보를 입력해 로그인하세요.</p>
        </div>

        <Form onSubmit={handleLogin}>
          <Input
            label="아이디"
            type="text"
            id="id"
            value={id}
            onChange={(e) => setId(e.target.value)}
            placeholder="아이디를 입력하세요"
            required
          />
          <Input
            label="비밀번호"
            type="password"
            id="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="비밀번호를 입력하세요"
            required
          />
          {errorMsg && (
            <p className={styles.errorMsg}>{errorMsg}</p>
          )}
          <Button type="submit">로그인</Button>
        </Form>
      </Card>
    </Container>
  );
}
