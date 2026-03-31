'use client';

import React, { useState, useEffect } from 'react';
import { FormModal, Input } from '@/components';
import { Notice, NoticeStatus } from '@/types';
import styles from './notices.module.css';

interface NoticeFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (notice: Omit<Notice, 'id' | 'createdAt'>, status: NoticeStatus) => void;
  onDelete?: (id: number) => void;
  notice?: Notice | null;
}

const emptyForm = {
  title: '',
  content: '',
  author: '',
  isSystemMaintenance: false,
  startAt: '',
  endAt: '',
  maintenanceStartAt: '',
  maintenanceEndAt: '',
  isScheduled: false,
  scheduledAt: '',
};

export const NoticeFormModal: React.FC<NoticeFormModalProps> = ({
  isOpen,
  onClose,
  onSave,
  onDelete,
  notice,
}) => {
  const [form, setForm] = useState(emptyForm);

  useEffect(() => {
    if (notice) {
      setForm({
        title: notice.title,
        content: notice.content,
        author: notice.author,
        isSystemMaintenance: notice.isSystemMaintenance,
        startAt: notice.startAt ?? '',
        endAt: notice.endAt ?? '',
        maintenanceStartAt: notice.maintenanceStartAt ?? '',
        maintenanceEndAt: notice.maintenanceEndAt ?? '',
        isScheduled: notice.status === 'scheduled',
        scheduledAt: notice.scheduledAt ?? '',
      });
    } else {
      setForm(emptyForm);
    }
  }, [notice, isOpen]);

  const set = (patch: Partial<typeof emptyForm>) => setForm((prev) => ({ ...prev, ...patch }));

  const handleSubmit = (status: NoticeStatus) => {
    if (!form.title.trim() || !form.content.trim() || !form.author.trim()) {
      alert('제목, 내용, 작성자를 입력해주세요.');
      return;
    }
    if (status === 'scheduled' && !form.scheduledAt) {
      alert('예약 발행 시간을 입력해주세요.');
      return;
    }
    if (form.isSystemMaintenance && (!form.maintenanceStartAt || !form.maintenanceEndAt)) {
      alert('시스템 점검 시작/종료 시간을 입력해주세요.');
      return;
    }
    onSave(
      {
        title: form.title,
        content: form.content,
        author: form.author,
        isSystemMaintenance: form.isSystemMaintenance,
        status,
        startAt: form.startAt || undefined,
        endAt: form.endAt || undefined,
        scheduledAt: status === 'scheduled' ? form.scheduledAt : undefined,
        maintenanceStartAt: form.isSystemMaintenance ? form.maintenanceStartAt : undefined,
        maintenanceEndAt: form.isSystemMaintenance ? form.maintenanceEndAt : undefined,
      },
      status,
    );
  };

  const isEdit = !!notice;

  return (
    <FormModal
      isOpen={isOpen}
      onClose={onClose}
      title={isEdit ? '공지 수정' : '공지 작성'}
      actions={[
        { label: '임시저장', variant: 'secondary', onClick: () => handleSubmit('draft') },
        {
          label: form.isScheduled ? '예약 발행' : '발행',
          variant: form.isScheduled ? 'primary' : 'success',
          onClick: () => handleSubmit(form.isScheduled ? 'scheduled' : 'published'),
        },
      ]}
      deleteConfig={
        isEdit
          ? {
              onDelete: () => onDelete?.(notice!.id),
              confirmTitle: '공지 삭제',
              confirmMessage: '이 공지를 삭제하시겠습니까? 삭제 후 복구할 수 없습니다.',
            }
          : undefined
      }
    >
      <Input
        label="제목"
        id="notice-title"
        value={form.title}
        onChange={(e) => set({ title: e.target.value })}
        placeholder="공지 제목을 입력하세요"
      />

      <Input
        label="작성자"
        id="notice-author"
        value={form.author}
        onChange={(e) => set({ author: e.target.value })}
        placeholder="작성자 이름을 입력하세요"
      />

      <div className={styles.fieldGroup}>
        <label className={styles.fieldLabel}>내용</label>
        <textarea
          className={styles.textarea}
          value={form.content}
          onChange={(e) => set({ content: e.target.value })}
          placeholder="공지 내용을 입력하세요"
          rows={5}
        />
      </div>

      <div className={styles.section}>
        <div className={styles.sectionTitle}>공지 노출 기간</div>
        <div className={styles.dateRow}>
          <Input
            label="시작 일시"
            id="notice-startAt"
            type="datetime-local"
            value={form.startAt}
            onChange={(e) => set({ startAt: e.target.value })}
          />
          <Input
            label="종료 일시"
            id="notice-endAt"
            type="datetime-local"
            value={form.endAt}
            onChange={(e) => set({ endAt: e.target.value })}
          />
        </div>
      </div>

      <div className={styles.checkRow}>
        <label className={styles.checkLabel}>
          <input
            type="checkbox"
            checked={form.isSystemMaintenance}
            onChange={(e) =>
              set({ isSystemMaintenance: e.target.checked, maintenanceStartAt: '', maintenanceEndAt: '' })
            }
          />
          <span>시스템 점검 공지</span>
        </label>
      </div>

      {form.isSystemMaintenance && (
        <div className={styles.maintenanceBox}>
          <div className={styles.sectionTitle}>시스템 점검 시간</div>
          <div className={styles.dateRow}>
            <Input
              label="점검 시작"
              id="maintenance-start"
              type="datetime-local"
              value={form.maintenanceStartAt}
              onChange={(e) => set({ maintenanceStartAt: e.target.value })}
            />
            <Input
              label="점검 종료"
              id="maintenance-end"
              type="datetime-local"
              value={form.maintenanceEndAt}
              onChange={(e) => set({ maintenanceEndAt: e.target.value })}
            />
          </div>
        </div>
      )}

      <div className={styles.checkRow}>
        <label className={styles.checkLabel}>
          <input
            type="checkbox"
            checked={form.isScheduled}
            onChange={(e) => set({ isScheduled: e.target.checked, scheduledAt: '' })}
          />
          <span>예약 발행</span>
        </label>
      </div>

      {form.isScheduled && (
        <Input
          label="예약 발행 일시"
          id="notice-scheduled"
          type="datetime-local"
          value={form.scheduledAt}
          onChange={(e) => set({ scheduledAt: e.target.value })}
        />
      )}
    </FormModal>
  );
};
