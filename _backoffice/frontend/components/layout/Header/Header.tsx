'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { Navbar } from '../../molecules/Navbar/Navbar';
import { ConfirmModal } from '../../molecules/ConfirmModal/ConfirmModal';
import { NavigationItem } from '../../types';
import styles from './Header.module.css';

const menuItems: NavigationItem[] = [
  {
    label: '사용자 관리',
    subItems: [
      { label: '사용자 목록', href: '/users' },
      { label: '권한 설정', href: '/roles' },
    ]
  },
  {
    label: '시스템 설정',
    subItems: [
      { label: '공지사항 관리', href: '/notices' },
    ]
  },
  {
    label: '통계',
    href: '/stats'
  }
];

export const Header: React.FC = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const router = useRouter();

  const handleLogoutClick = (e: React.MouseEvent) => {
    e.preventDefault();
    setIsModalOpen(true);
  };

  const handleConfirmLogout = () => {
    setIsModalOpen(false);
    router.push('/login');
  };

  return (
    <header className={styles.header}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '2rem' }}>
        <Link href="/" className={styles.logo}>
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
