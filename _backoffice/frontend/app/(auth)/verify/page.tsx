'use client';

import { useState, useEffect } from 'react';
import { Card, Input, Button, Container, Form } from '../../../components';
import styles from './verify.module.css';

export default function VerifyPage() {
  const [phoneNumber, setPhoneNumber] = useState('');
  const [code, setCode] = useState('');
  const [timeLeft, setTimeLeft] = useState(180); // 3 minutes in seconds
  const [isTimerActive, setIsTimerActive] = useState(true);

  useEffect(() => {
    let timer: NodeJS.Timeout;
    if (isTimerActive && timeLeft > 0) {
      timer = setInterval(() => {
        setTimeLeft((prev) => prev - 1);
      }, 1000);
    } else if (timeLeft === 0) {
      setIsTimerActive(false);
    }
    return () => clearInterval(timer);
  }, [isTimerActive, timeLeft]);

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const handleVerify = (e: React.FormEvent) => {
    e.preventDefault();
    if (timeLeft === 0) {
      alert('Verification code expired. Please request a new one.');
      return;
    }
    alert(`Verifying phone: ${phoneNumber} with code: ${code}`);
  };

  const handleResend = () => {
    setTimeLeft(180);
    setIsTimerActive(true);
    alert('Verification code resent.');
  };

  return (
    <Container>
      <Card className={styles.cardWrapper}>
        <h1 className={styles.title}>Phone Verification</h1>
        <p className={styles.subtitle}>Please enter your phone number and verification code.</p>
        
        <Form onSubmit={handleVerify}>
          <Input
            label="Phone Number"
            type="tel"
            id="phone"
            value={phoneNumber}
            onChange={(e) => setPhoneNumber(e.target.value)}
            placeholder="010-0000-0000"
            required
          />
          <Input
            label="Verification Code"
            type="text"
            id="code"
            value={code}
            onChange={(e) => setCode(e.target.value)}
            placeholder="Enter 6-digit code"
            required
          />
          <div className={styles.timer}>
            Time remaining: {formatTime(timeLeft)}
          </div>
          <Button 
            type="submit" 
            variant="success"
            disabled={timeLeft === 0}
          >
            Verify
          </Button>
        </Form>

        <div className={styles.resend} onClick={handleResend}>
          Didn't receive the code? Resend
        </div>
      </Card>
    </Container>
  );
}
