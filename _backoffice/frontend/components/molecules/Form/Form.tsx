import React from 'react';
import styles from './Form.module.css';

interface FormProps extends React.FormHTMLAttributes<HTMLFormElement> {
  children: React.ReactNode;
}

export const Form: React.FC<FormProps> = ({ children, onSubmit, className, ...props }) => {
  return (
    <form 
      onSubmit={onSubmit} 
      className={`${styles.form} ${className || ''}`}
      {...props}
    >
      {children}
    </form>
  );
};
