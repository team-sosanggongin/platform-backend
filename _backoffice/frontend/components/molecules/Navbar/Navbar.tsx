import React from 'react';
import { NavigationItem } from '../../../types';
import { NavItem } from './NavItem';
import styles from './Navbar.module.css';

interface NavbarProps {
  items: NavigationItem[];
}

export const Navbar: React.FC<NavbarProps> = ({ items }) => {
  return (
    <nav>
      <ul className={styles.navbar}>
        {items.map((item, index) => (
          <NavItem key={index} item={item} />
        ))}
      </ul>
    </nav>
  );
};
