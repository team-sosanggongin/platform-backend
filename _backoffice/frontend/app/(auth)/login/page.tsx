'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { Card, Input, Button, Container, Form } from '../../../components';
import styles from './login.module.css';

// 최초 로그인 유저 시뮬레이션 (실제로는 API 응답으로 판단)
const FIRST_TIME_USERS = ['newuser', 'test123'];

export default function LoginPage() {
  const [id, setId] = useState('');
  const [password, setPassword] = useState('');
  const router = useRouter();

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault();
    if (!id || !password) return;

    if (FIRST_TIME_USERS.includes(id)) {
      router.push('/login/change-password');
    } else {
      router.push('/verify');
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
          <Button type="submit">로그인</Button>
        </Form>
      </Card>
    </Container>
  );
}
