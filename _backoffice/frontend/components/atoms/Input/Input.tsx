import React from 'react';
import styles from './Input.module.css';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  wrapperStyle?: React.CSSProperties;
}

export const Input: React.FC<InputProps> = ({ label, id, wrapperStyle, ...props }) => {
  return (
    <div className={styles.inputGroup} style={wrapperStyle}>
      {label && <label className={styles.label} htmlFor={id}>{label}</label>}
      <input id={id} className={styles.input} {...props} />
    </div>
  );
};
