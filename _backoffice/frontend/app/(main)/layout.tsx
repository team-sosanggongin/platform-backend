'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { Header, Footer } from '../../components';
import { api } from '@/lib/api';

export default function MainLayout({
  children,
}: {
  children: React.ReactNode
}) {
  const [authenticated, setAuthenticated] = useState(false);
  const router = useRouter();

  useEffect(() => {
    api.get('/api/auth/me').then((res) => {
      if (res.ok) {
        setAuthenticated(true);
      } else {
        router.replace('/login');
      }
    });
  }, [router]);

  if (!authenticated) return null;

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100vh', width: '100vw', overflow: 'hidden' }}>
      <Header />
      <main style={{ flex: 1, overflowY: 'auto' }}>
        {children}
      </main>
      <Footer />
    </div>
  );
}
