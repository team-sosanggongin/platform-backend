import React from 'react';
import styles from './DetailRow.module.css';

interface DetailRowProps {
  label: string;
  children: React.ReactNode;
}

export const DetailRow: React.FC<DetailRowProps> = ({ label, children }) => (
  <div className={styles.row}>
    <div className={styles.label}>{label}</div>
    <div className={styles.content}>
      {typeof children === 'string' || typeof children === 'number' ? (
        <span className={styles.value}>{children}</span>
      ) : (
        children
      )}
    </div>
  </div>
);
