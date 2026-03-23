import React from 'react';
import Link from 'next/link';
import { NavigationItem } from '../../../types';
import styles from './NavItem.module.css';

interface NavItemProps {
  item: NavigationItem;
}

export const NavItem: React.FC<NavItemProps> = ({ item }) => {
  const hasSubItems = item.subItems && item.subItems.length > 0;

  return (
    <li className={styles.navItem}>
      {item.href ? (
        <Link href={item.href} className={styles.link}>
          {item.label}
          {hasSubItems && <span className={styles.arrow}>▼</span>}
        </Link>
      ) : (
        <span className={styles.link}>
          {item.label}
          {hasSubItems && <span className={styles.arrow}>▼</span>}
        </span>
      )}

      {hasSubItems && (
        <ul className={styles.dropdown}>
          {item.subItems!.map((subItem, index) => (
            <li key={index} className={styles.dropdownItem}>
              <Link href={subItem.href || '#'} className={styles.dropdownLink}>
                {subItem.label}
              </Link>
            </li>
          ))}
        </ul>
      )}
    </li>
  );
};
