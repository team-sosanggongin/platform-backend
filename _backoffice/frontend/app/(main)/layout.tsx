'use client';

import { Header, Footer } from '../../components';
import { AuthProvider } from '@/lib/auth-context';

export default function MainLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <AuthProvider>
      <div style={{ display: 'flex', flexDirection: 'column', height: '100vh', width: '100vw', overflow: 'hidden' }}>
        <Header />
        <main style={{ flex: 1, overflowY: 'auto' }}>
          {children}
        </main>
        <Footer />
      </div>
    </AuthProvider>
  );
}
