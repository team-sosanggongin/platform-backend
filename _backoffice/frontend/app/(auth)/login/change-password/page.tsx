'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { Card, Button, Container, Form } from '../../../../components';
import { api, ApiError } from '../../../../lib/api';
import styles from './change-password.module.css';

interface Requirements {
  minLength: boolean;
  hasNumber: boolean;
  hasSpecial: boolean;
}

const checkRequirements = (pw: string): Requirements => ({
  minLength: pw.length >= 8,
  hasNumber: /\d/.test(pw),
  hasSpecial: /[!@#$%^&*()_+\-=\[\]{}|;':",.<>?]/.test(pw),
});

const allMet = (req: Requirements) => Object.values(req).every(Boolean);

export default function ChangePasswordPage() {
  const router = useRouter();
  const [currentPassword, setCurrentPassword] = useState('');
  const [password, setPassword] = useState('');
  const [confirm, setConfirm] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  const req = checkRequirements(password);
  const passwordsMatch = password === confirm;
  const confirmTouched = confirm.length > 0;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitted(true);
    if (!allMet(req) || !passwordsMatch || !confirm) return;

    try {
      await api.patch('/api/account/me/password', {
        currentPassword,
        newPassword: password,
      });
      router.push('/backoffice');
    } catch (e) {
      if (e instanceof ApiError) {
        setErrorMsg(e.message);
      } else {
        setErrorMsg('오류가 발생했습니다.');
      }
    }
  };

  const EyeIcon = ({ open }: { open: boolean }) => (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      {open ? (
        <>
          <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
          <circle cx="12" cy="12" r="3"/>
        </>
      ) : (
        <>
          <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/>
          <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/>
          <line x1="1" y1="1" x2="23" y2="23"/>
        </>
      )}
    </svg>
  );

  const requirements = [
    { key: 'minLength' as const, label: '8자 이상' },
    { key: 'hasNumber' as const, label: '숫자 포함' },
    { key: 'hasSpecial' as const, label: '특수문자 포함' },
  ];

  return (
    <Container>
      <Card className={styles.cardWrapper}>
        <div className={styles.iconBadge}>
          <svg viewBox="0 0 24 24" fill="none" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
            <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
          </svg>
        </div>

        <h1 className={styles.title}>비밀번호 변경</h1>
        <p className={styles.subtitle}>
          최초 로그인을 환영합니다.<br />
          보안을 위해 임시 비밀번호를 새 비밀번호로 변경해주세요.
        </p>

        <div className={styles.requirements}>
          <p className={styles.requirementsTitle}>비밀번호 조건</p>
          <ul className={styles.requirementsList}>
            {requirements.map(({ key, label }) => (
              <li key={key} className={`${styles.requirementItem} ${req[key] ? styles.met : ''}`}>
                <span className={styles.requirementDot} />
                {label}
              </li>
            ))}
          </ul>
        </div>

        <Form onSubmit={handleSubmit}>
          <div className={styles.passwordWrapper}>
            <input
              id="current-password"
              type="password"
              value={currentPassword}
              onChange={(e) => setCurrentPassword(e.target.value)}
              placeholder="현재 비밀번호 (임시 비밀번호)"
              style={{
                width: '100%',
                height: 38,
                padding: '0 12px',
                border: '1px solid #e2e8f0',
                borderRadius: 6,
                fontSize: 14,
                marginBottom: 16,
                boxSizing: 'border-box',
                outline: 'none',
                fontFamily: 'inherit',
              }}
            />
          </div>

          <div className={styles.passwordWrapper}>
            <input
              id="new-password"
              type={showPassword ? 'text' : 'password'}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="새 비밀번호"
              style={{
                width: '100%',
                height: 38,
                padding: '0 44px 0 12px',
                border: `1px solid ${submitted && !allMet(req) ? '#ef4444' : '#e2e8f0'}`,
                borderRadius: 6,
                fontSize: 14,
                marginBottom: 16,
                boxSizing: 'border-box',
                outline: 'none',
                fontFamily: 'inherit',
              }}
            />
            <button
              type="button"
              className={styles.toggleBtn}
              onClick={() => setShowPassword((v) => !v)}
              tabIndex={-1}
            >
              <EyeIcon open={showPassword} />
            </button>
          </div>

          <div className={styles.passwordWrapper}>
            <input
              id="confirm-password"
              type={showConfirm ? 'text' : 'password'}
              value={confirm}
              onChange={(e) => setConfirm(e.target.value)}
              placeholder="비밀번호 확인"
              style={{
                width: '100%',
                height: 38,
                padding: '0 44px 0 12px',
                border: `1px solid ${confirmTouched && !passwordsMatch ? '#ef4444' : '#e2e8f0'}`,
                borderRadius: 6,
                fontSize: 14,
                marginBottom: confirmTouched ? 8 : 16,
                boxSizing: 'border-box',
                outline: 'none',
                fontFamily: 'inherit',
              }}
            />
            <button
              type="button"
              className={styles.toggleBtn}
              onClick={() => setShowConfirm((v) => !v)}
              tabIndex={-1}
            >
              <EyeIcon open={showConfirm} />
            </button>
          </div>

          {confirmTouched && (
            <p className={`${styles.matchMessage} ${passwordsMatch ? styles.matchOk : styles.matchFail}`}>
              {passwordsMatch ? '✓ 비밀번호가 일치합니다.' : '비밀번호가 일치하지 않습니다.'}
            </p>
          )}

          {errorMsg && (
            <p style={{ color: '#ef4444', fontSize: 14, marginBottom: 8 }}>{errorMsg}</p>
          )}

          <Button type="submit" variant="success">
            비밀번호 변경
          </Button>
        </Form>
      </Card>
    </Container>
  );
}