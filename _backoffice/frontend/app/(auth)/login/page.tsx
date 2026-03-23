'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { Card, Input, Button, Container, Form } from '../../../components';
import styles from './login.module.css';

export default function LoginPage() {
  const [id, setId] = useState('');
  const [password, setPassword] = useState('');
  const router = useRouter();

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (id && password) {
      router.push('/verify');
    } else {
      alert('Please enter both ID and Password');
    }
  };

  return (
    <Container>
      <Card className={styles.cardWrapper}>
        <h1 className={styles.title}>Backoffice Login</h1>
        <Form onSubmit={handleLogin}>
          <Input
            label="ID"
            type="text"
            id="id"
            value={id}
            onChange={(e) => setId(e.target.value)}
            placeholder="Enter your ID"
            required
          />
          <Input
            label="Password"
            type="password"
            id="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Enter your password"
            required
          />
          <Button type="submit">
            Login
          </Button>
        </Form>
      </Card>
    </Container>
  );
}
