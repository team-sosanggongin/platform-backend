'use client';

import React, { useMemo, useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { Navbar } from '../../molecules/Navbar/Navbar';
import { ConfirmModal } from '../../molecules/ConfirmModal/ConfirmModal';
import styles from './Header.module.css';
import { NavigationItem } from '@/types';
import { api } from '../../../lib/api';
import { useAuth } from '@/lib/auth-context';

const ROOT_ONLY_PATHS = new Set<string>();

const allMenuItems: NavigationItem[] = [
  {
    label: '사용자 관리',
    subItems: [
      { label: '사용자 목록', href: '/backoffice/users' },
      { label: '권한 설정', href: '/backoffice/roles' },
    ]
  },
  {
    label: '시스템 설정',
    subItems: [
      { label: '공지사항 관리', href: '/backoffice/notices' },
      { label: '활동 이력', href: '/backoffice/activity-log/audit' },
      { label: '로그인 이력', href: '/backoffice/activity-log/login' },
    ]
  }
];

export const Header: React.FC = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const router = useRouter();
  const { me } = useAuth();

  const menuItems = useMemo(() => {
    if (me?.root) return allMenuItems;
    return allMenuItems
      .map((group) => ({
        ...group,
        subItems: group.subItems?.filter((item) => !item.href || !ROOT_ONLY_PATHS.has(item.href)),
      }))
      .filter((group) => (group.subItems?.length ?? 0) > 0);
  }, [me]);

  const handleLogoutClick = (e: React.MouseEvent) => {
    e.preventDefault();
    setIsModalOpen(true);
  };

  const handleConfirmLogout = async () => {
    try {
      await api.post('/api/auth/logout');
    } catch (e) {
      // 로그아웃 실패해도 로그인 페이지로 이동
    } finally {
      setIsModalOpen(false);
      router.push('/login');
    }
  };

  return (
    <header className={styles.header}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '2rem' }}>
        <Link href="/backoffice" className={styles.logo}>
          Backoffice
        </Link>
        <Navbar items={menuItems} />
      </div>
      <div className={styles.nav}>
        <span
          className={styles.navLink}
          onClick={handleLogoutClick}
          style={{ cursor: 'pointer' }}
        >
          Logout
        </span>
      </div>

      <ConfirmModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onConfirm={handleConfirmLogout}
        title="로그아웃 확인"
      >
        정말 로그아웃 하시겠습니까?
      </ConfirmModal>
    </header>
  );
};
