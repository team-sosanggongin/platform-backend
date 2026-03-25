'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { Card, Input, Button, DetailRow } from '../../../../components';
import styles from './new.module.css';
// TODO :: 새로운 유저가 등록되면, 등록된 유저의 전화번호로 id와 초기비밀번호, URL을 함께 전송
// 시스템에 등록된 권한 목록 (실제로는 API에서 가져옴)
const AVAILABLE_ROLES = ['Admin', 'Manager', 'Editor'];

interface FormState {
  name: string;
  id: string;
  phone: string;
  roles: string[];
}

const emptyForm: FormState = { name: '', id: '', phone: '', roles: [] };

export default function NewUserPage() {
  const router = useRouter();
  const [form, setForm] = useState<FormState>(emptyForm);
  const [submitted, setSubmitted] = useState(false);

  const set = (patch: Partial<FormState>) => setForm((prev) => ({ ...prev, ...patch }));

  const toggleRole = (role: string) => {
    set({
      roles: form.roles.includes(role)
        ? form.roles.filter((r) => r !== role)
        : [...form.roles, role],
    });
  };

  const validate = () => {
    if (!form.name.trim()) return false;
    if (!form.id.trim()) return false;
    if (!form.phone.trim()) return false;
    if (form.roles.length === 0) return false;
    return true;
  };

  const handleSubmit = () => {
    setSubmitted(true);
    if (!validate()) return;

    // TODO: API 호출
    alert(`유저 등록 완료\n이름: ${form.name}\nID: ${form.id}\n전화번호: ${form.phone}\n권한: ${form.roles.join(', ')}`);
    router.push('/backoffice/users');
  };

  const fieldError = (value: string) => submitted && !value.trim();
  const rolesError = submitted && form.roles.length === 0;

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
              id="user-id"
              value={form.id}
              onChange={(e) => set({ id: e.target.value })}
              placeholder="로그인에 사용할 아이디를 입력하세요"
              wrapperStyle={{ marginBottom: 0, maxWidth: 400 }}
            />
            {fieldError(form.id) && (
              <span className={styles.errorMessage}>아이디를 입력해주세요.</span>
            )}
          </DetailRow>

          <DetailRow label="전화번호">
            <Input
              id="user-phone"
              type="tel"
              value={form.phone}
              onChange={(e) => set({ phone: e.target.value })}
              placeholder="010-0000-0000"
              wrapperStyle={{ marginBottom: 0, maxWidth: 400 }}
            />
            {fieldError(form.phone) && (
              <span className={styles.errorMessage}>전화번호를 입력해주세요.</span>
            )}
          </DetailRow>

          <DetailRow label="권한">
            <div>
              <div className={styles.rolesGroup}>
                {AVAILABLE_ROLES.map((role) => (
                  <label key={role} className={styles.roleCheckLabel}>
                    <input
                      type="checkbox"
                      checked={form.roles.includes(role)}
                      onChange={() => toggleRole(role)}
                    />
                    {role}
                  </label>
                ))}
              </div>
              {rolesError && (
                <p className={styles.errorMessage}>권한을 하나 이상 선택해주세요.</p>
              )}
            </div>
          </DetailRow>

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
