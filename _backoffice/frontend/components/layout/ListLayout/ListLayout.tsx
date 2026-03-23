import React from 'react';
import { Input, Button, Select, Table, Pagination, TableColumn } from '../../index';
import styles from './ListLayout.module.css';

interface ListLayoutProps<T> {
  title: string;
  search?: {
    field: string;
    onFieldChange: (e: React.ChangeEvent<HTMLSelectElement>) => void;
    options: { value: string, label: string }[];
    query: string;
    onQueryChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
    onSearch?: () => void;
  };
  table: {
    columns: TableColumn<T>[];
    data: T[];
    rowKey: (item: T) => string | number;
    emptyMessage?: string;
    onRowClick?: (item: T) => void;
  };
  pagination?: {
    currentPage: number;
    totalPages: number;
    onPageChange: (page: number) => void;
  };
  extraActions?: React.ReactNode;
}

export function ListLayout<T>({ 
  title, 
  search, 
  table, 
  pagination, 
  extraActions 
}: ListLayoutProps<T>) {
  return (
    <div className={styles.container}>
      <header className={styles.header}>
        <h1 className={styles.title}>{title}</h1>
        {extraActions && <div>{extraActions}</div>}
      </header>

      {search && (
        <div className={styles.searchContainer}>
          <Select 
            label="검색 필드"
            options={search.options} 
            value={search.field} 
            onChange={search.onFieldChange} 
            style={{ marginBottom: 0 }}
          />
          <Input 
            label="검색어"
            placeholder="검색어를 입력하세요..." 
            value={search.query}
            onChange={search.onQueryChange}
            style={{ marginBottom: 0, flex: 1 }}
          />
          <Button 
            style={{ width: 'auto' }} 
            onClick={search.onSearch}
          >
            검색
          </Button>
        </div>
      )}

      <Table 
        columns={table.columns} 
        data={table.data} 
        rowKey={table.rowKey}
        emptyMessage={table.emptyMessage}
        onRowClick={table.onRowClick}
      />

      {pagination && (
        <Pagination 
          currentPage={pagination.currentPage} 
          totalPages={pagination.totalPages} 
          onPageChange={pagination.onPageChange} 
        />
      )}
    </div>
  );
}
