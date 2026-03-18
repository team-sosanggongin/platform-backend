import React from 'react';
import styles from './Table.module.css';

export interface TableColumn<T> {
  header: string;
  render: (item: T) => React.ReactNode;
  width?: string;
}

interface TableProps<T> {
  columns: TableColumn<T>[];
  data: T[];
  emptyMessage?: string;
  rowKey: (item: T) => string | number;
  onRowClick?: (item: T) => void;
}

export function Table<T>({ 
  columns, 
  data, 
  emptyMessage = '데이터가 없습니다.', 
  rowKey,
  onRowClick
}: TableProps<T>) {
  return (
    <div className={styles.tableWrapper}>
      <table className={styles.table}>
        <thead>
          <tr>
            {columns.map((col, index) => (
              <th 
                key={index} 
                className={styles.th} 
                style={{ width: col.width }}
              >
                {col.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.length > 0 ? (
            data.map((item) => (
              <tr 
                key={rowKey(item)} 
                className={styles.tr} 
                onClick={() => onRowClick?.(item)}
              >
                {columns.map((col, index) => (
                  <td key={index} className={styles.td}>
                    {col.render(item)}
                  </td>
                ))}
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan={columns.length} className={styles.empty}>
                {emptyMessage}
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
