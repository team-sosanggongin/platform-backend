import React from 'react';
import styles from './Select.module.css';

interface Option {
  value: string;
  label: string;
}

interface SelectProps extends React.SelectHTMLAttributes<HTMLSelectElement> {
  options: Option[];
  label?: string;
}

export const Select: React.FC<SelectProps> = ({ options, label, id, className, style, ...props }) => {
  return (
    <div className={styles.selectGroup} style={style}>
      {label && <label className={styles.label} htmlFor={id}>{label}</label>}
      <select id={id} className={`${styles.select} ${className || ''}`} {...props}>
        {options.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
    </div>
  );
};
