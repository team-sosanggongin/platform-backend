'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { Card, Input, Button, Container, Form } from '../../../components';
import { api, ApiError } from '../../../lib/api';
import styles from './login.module.css';

export default function LoginPage() {
  const [id, setId] = useState('');
  const [password, setPassword] = useState('');
  const [errorMsg, setErrorMsg] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const router = useRouter();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!id || !password) return;

    setIsLoading(true);
    setErrorMsg('');

    try {
      const response = await api.post<{
        id: string;
        name: string;
        root: boolean;
        passwordExpired: boolean;
      }>('/api/auth/login', { loginId: id, password });

      if (response.passwordExpired) {
        router.push('/login/change-password');
        return;
      }

      router.push('/backoffice');
    } catch (e) {
      if (e instanceof ApiError) {
        if (e.status === 403) {
          router.push('/locked');
        } else {
          setErrorMsg(e.message);
        }
      } else {
        setErrorMsg('오류가 발생했습니다.');
      }
    } finally {
      setIsLoading(false);
    }
  };

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
          <Button type="submit" disabled={isLoading}>
            {isLoading ? '로그인 중...' : '로그인'}
          </Button>
        </Form>
      </Card>
    </Container>
  );
}