'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { Card, Input, Button, DetailRow } from '@/components';
import { api, ApiError } from '@/lib/api';
import styles from './new.module.css';

interface FormState {
  name: string;
  loginId: string;
  email: string;
  phoneNumber: string;
}

const emptyForm: FormState = { name: '', loginId: '', email: '', phoneNumber: '' };

export default function NewUserPage() {
  const router = useRouter();
  const [form, setForm] = useState<FormState>(emptyForm);
  const [submitted, setSubmitted] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');
  const [createdLoginId, setCreatedLoginId] = useState<string | null>(null);

  const set = (patch: Partial<FormState>) => setForm((prev) => ({ ...prev, ...patch }));

  const validate = () => {
    if (!form.name.trim()) return false;
    if (!form.loginId.trim()) return false;
    return true;
  };

  const handleSubmit = async () => {
    setSubmitted(true);
    if (!validate()) return;

    try {
      await api.post('/api/account', {
        name: form.name,
        loginId: form.loginId,
        email: form.email || null,
        phoneNumber: form.phoneNumber || null,
      });
      setCreatedLoginId(form.loginId);
    } catch (e) {
      if (e instanceof ApiError) {
        setErrorMsg(e.message);
      } else {
        setErrorMsg('오류가 발생했습니다.');
      }
    }
  };

  const fieldError = (value: string) => submitted && !value.trim();

  if (createdLoginId) {
    return (
      <div className={styles.container}>
        <Card className={styles.formCard}>
          <div className={styles.cardHeader}>
            <h2 className={styles.cardTitle}>등록 완료</h2>
          </div>
          <div className={styles.cardBody}>
            <p style={{ marginBottom: 16, color: '#334155' }}>
              새 관리자 계정이 등록되었습니다.
            </p>
            <div style={{ background: '#f8fafc', border: '1px solid #e2e8f0', borderRadius: 6, padding: '12px 16px', marginBottom: 24 }}>
              <p style={{ fontSize: 14, color: '#64748b', marginBottom: 8 }}>초기 로그인 정보</p>
              <p style={{ fontSize: 14, color: '#334155' }}>아이디: <strong>{createdLoginId}</strong></p>
              <p style={{ fontSize: 14, color: '#334155' }}>초기 비밀번호: <strong>{createdLoginId}</strong></p>
              <p style={{ fontSize: 12, color: '#94a3b8', marginTop: 8 }}>
                첫 로그인 시 비밀번호 변경이 필요합니다.
              </p>
            </div>
            <div className={styles.footer}>
              <Button variant="secondary" style={{ width: 130 }}
                onClick={() => router.push('/backoffice/users')}>
                목록으로
              </Button>
              <Button style={{ width: 160 }}
                onClick={() => { setCreatedLoginId(null); setForm(emptyForm); setSubmitted(false); }}>
                추가 등록
              </Button>
            </div>
          </div>
        </Card>
      </div>
    );
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
          <h2 className={styles.cardTitle}>새 유저 등록</h2>
        </div>

        <div className={styles.cardBody}>
          <DetailRow label="이름">
            <Input
              id="user-name"
              value={form.name}
              onChange={(e) => set({ name: e.target.value })}
              placeholder="이름을 입력하세요"
              wrapperStyle={{ marginBottom: 0, maxWidth: 400 }}
            />
            {fieldError(form.name) && (
              <span className={styles.errorMessage}>이름을 입력해주세요.</span>
            )}
          </DetailRow>

          <DetailRow label="아이디">
            <Input
              id="user-loginId"
              value={form.loginId}
              onChange={(e) => set({ loginId: e.target.value })}
              placeholder="로그인에 사용할 아이디를 입력하세요"
              wrapperStyle={{ marginBottom: 0, maxWidth: 400 }}
            />
            {fieldError(form.loginId) && (
              <span className={styles.errorMessage}>아이디를 입력해주세요.</span>
            )}
          </DetailRow>

          <DetailRow label="이메일">
            <Input
              id="user-email"
              type="email"
              value={form.email}
              onChange={(e) => set({ email: e.target.value })}
              placeholder="이메일을 입력하세요 (선택)"
              wrapperStyle={{ marginBottom: 0, maxWidth: 400 }}
            />
          </DetailRow>

          <DetailRow label="전화번호">
            <Input
              id="user-phone"
              type="tel"
              value={form.phoneNumber}
              onChange={(e) => set({ phoneNumber: e.target.value })}
              placeholder="010-0000-0000 (선택)"
              wrapperStyle={{ marginBottom: 0, maxWidth: 400 }}
            />
          </DetailRow>

          {errorMsg && (
            <p className={styles.errorMessage}>{errorMsg}</p>
          )}

          <div className={styles.footer}>
            <Button
              variant="secondary"
              style={{ width: 120 }}
              onClick={() => router.push('/backoffice/users')}
            >
              취소
            </Button>
            <Button variant="success" style={{ width: 140 }} onClick={handleSubmit}>
              등록
            </Button>
          </div>
        </div>
      </Card>
    </div>
  );
}