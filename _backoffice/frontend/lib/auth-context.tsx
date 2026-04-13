'use client';

import React, { createContext, useContext, useEffect, useState } from 'react';
import { User } from '@/types';
import { api } from './api';

interface AuthState {
  me: User | null;
  loading: boolean;
}

const AuthContext = createContext<AuthState>({ me: null, loading: true });

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [me, setMe] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get<User>('/api/account/me')
      .then(setMe)
      .catch(() => setMe(null))
      .finally(() => setLoading(false));
  }, []);

  return (
    <AuthContext.Provider value={{ me, loading }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  return ctx;
}

export function useHasPermission(code: string): boolean {
  const { me } = useAuth();
  if (!me) return false;
  if (me.root) return true;
  return me.permissionCodes?.includes(code) ?? false;
}
