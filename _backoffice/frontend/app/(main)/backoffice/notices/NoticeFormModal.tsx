'use client';

import React, { useState, useEffect } from 'react';
import { FormModal, Input } from '@/components';
import { Notice, NoticeStatus } from '@/types';
import styles from './notices.module.css';

interface NoticeFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave?: (data: Record<string, unknown>, status: NoticeStatus) => void;
  onDelete?: (id: number) => void;
  notice?: Notice | null;
}

const emptyForm = {
  title: '',
  content: '',
  isServiceMaintenance: false,
  startsAt: '',
  endsAt: '',
  maintenanceStartAt: '',
  maintenanceEndAt: '',
  isScheduled: false,
  scheduledAt: '',
};

const formatDisplay = (value?: string) => {
  if (!value) return '-';
  return value.replace('T', ' ').slice(0, 16);
};

export const NoticeFormModal: React.FC<NoticeFormModalProps> = ({
  isOpen,
  onClose,
  onSave,
  onDelete,
  notice,
}) => {
  const [form, setForm] = useState(emptyForm);
  const [isEditing, setIsEditing] = useState(false);

  const isCreate = !notice;
  const canEdit = !!onSave;
  const canDelete = !!onDelete;

  useEffect(() => {
    if (notice) {
      setForm({
        title: notice.title,
        content: notice.content,
        isServiceMaintenance: notice.isServiceMaintenance,
        startsAt: notice.startsAt?.replace(' ', 'T')?.slice(0, 16) ?? '',
        endsAt: notice.endsAt?.replace(' ', 'T')?.slice(0, 16) ?? '',
        maintenanceStartAt: notice.maintenanceStartAt?.replace(' ', 'T')?.slice(0, 16) ?? '',
        maintenanceEndAt: notice.maintenanceEndAt?.replace(' ', 'T')?.slice(0, 16) ?? '',
        isScheduled: notice.status === 'SCHEDULED',
        scheduledAt: notice.scheduledAt?.replace(' ', 'T')?.slice(0, 16) ?? '',
      });
      setIsEditing(false);
    } else {
      setForm(emptyForm);
      setIsEditing(true);
    }
  }, [notice, isOpen]);

  const set = (patch: Partial<typeof emptyForm>) => setForm((prev) => ({ ...prev, ...patch }));

  const handleSubmit = (status: NoticeStatus) => {
    if (!form.title.trim() || !form.content.trim()) {
      alert('제목과 내용을 입력해주세요.');
      return;
    }
    if (!form.startsAt) {
      alert('노출 시작 일시를 입력해주세요.');
      return;
    }
    if (form.isScheduled && !form.scheduledAt) {
      alert('예약 발행 시간을 입력해주세요.');
      return;
    }
    if (form.isServiceMaintenance && (!form.maintenanceStartAt || !form.maintenanceEndAt)) {
      alert('시스템 점검 시작/종료 시간을 입력해주세요.');
      return;
    }

    const payload: Record<string, unknown> = {
      title: form.title,
      content: form.content,
      isServiceMaintenance: form.isServiceMaintenance,
      startsAt: form.startsAt ? `${form.startsAt}:00` : null,
      endsAt: form.endsAt ? `${form.endsAt}:00` : null,
      scheduledAt: form.isScheduled && form.scheduledAt ? `${form.scheduledAt}:00` : null,
      maintenanceStartAt: form.isServiceMaintenance && form.maintenanceStartAt ? `${form.maintenanceStartAt}:00` : null,
      maintenanceEndAt: form.isServiceMaintenance && form.maintenanceEndAt ? `${form.maintenanceEndAt}:00` : null,
    };

    onSave?.(payload, status);
  };

  const handleClose = () => {
    setIsEditing(false);
    onClose();
  };

  const handleStartEdit = () => {
    setIsEditing(true);
  };

  const handleCancelEdit = () => {
    if (notice) {
      setForm({
        title: notice.title,
        content: notice.content,
        isServiceMaintenance: notice.isServiceMaintenance,
        startsAt: notice.startsAt?.replace(' ', 'T')?.slice(0, 16) ?? '',
        endsAt: notice.endsAt?.replace(' ', 'T')?.slice(0, 16) ?? '',
        maintenanceStartAt: notice.maintenanceStartAt?.replace(' ', 'T')?.slice(0, 16) ?? '',
        maintenanceEndAt: notice.maintenanceEndAt?.replace(' ', 'T')?.slice(0, 16) ?? '',
        isScheduled: notice.status === 'SCHEDULED',
        scheduledAt: notice.scheduledAt?.replace(' ', 'T')?.slice(0, 16) ?? '',
      });
    }
    setIsEditing(false);
  };

  const title = isCreate ? '공지 작성' : (isEditing ? '공지 수정' : '공지 상세');

  const actions = isEditing
    ? [
        { label: '임시저장', variant: 'secondary' as const, onClick: () => handleSubmit('DRAFT') },
        {
          label: form.isScheduled ? '예약 발행' : '발행',
          variant: (form.isScheduled ? 'primary' : 'success') as 'primary' | 'success',
          onClick: () => handleSubmit(form.isScheduled ? 'SCHEDULED' : 'PUBLISHED'),
        },
      ]
    : [
        ...(canEdit ? [{ label: '수정', variant: 'primary' as const, onClick: handleStartEdit }] : []),
        { label: '닫기', variant: 'secondary' as const, onClick: handleClose },
      ];

  const deleteConfig = (isEditing || !isCreate) && canDelete
    ? {
        onDelete: () => onDelete?.(notice!.id),
        confirmTitle: '공지 삭제',
        confirmMessage: '이 공지를 삭제하시겠습니까? 삭제 후 복구할 수 없습니다.',
      }
    : undefined;

  return (
    <FormModal
      isOpen={isOpen}
      onClose={handleClose}
      title={title}
      actions={actions}
      deleteConfig={isEditing ? deleteConfig : undefined}
    >
      {isEditing ? (
        <>
          <Input
            label="제목"
            id="notice-title"
            value={form.title}
            onChange={(e) => set({ title: e.target.value })}
            placeholder="공지 제목을 입력하세요"
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
                id="notice-startsAt"
                type="datetime-local"
                value={form.startsAt}
                onChange={(e) => set({ startsAt: e.target.value })}
              />
              <Input
                label="종료 일시"
                id="notice-endsAt"
                type="datetime-local"
                value={form.endsAt}
                onChange={(e) => set({ endsAt: e.target.value })}
              />
            </div>
          </div>

          <div className={styles.checkRow}>
            <label className={styles.checkLabel}>
              <input
                type="checkbox"
                checked={form.isServiceMaintenance}
                onChange={(e) =>
                  set({ isServiceMaintenance: e.target.checked, maintenanceStartAt: '', maintenanceEndAt: '' })
                }
              />
              <span>시스템 점검 공지</span>
            </label>
          </div>

          {form.isServiceMaintenance && (
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

          {!isCreate && (
            <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: 8 }}>
              <button
                onClick={handleCancelEdit}
                style={{
                  background: 'none', border: 'none', color: '#64748b',
                  fontSize: '0.8rem', cursor: 'pointer', textDecoration: 'underline',
                }}
              >
                수정 취소
              </button>
            </div>
          )}
        </>
      ) : (
        <>
          <div className={styles.detailRow}>
            <span className={styles.detailLabel}>제목</span>
            <span className={styles.detailValue}>{notice?.title}</span>
          </div>

          <div className={styles.detailRow}>
            <span className={styles.detailLabel}>내용</span>
            <div className={styles.detailContent}>{notice?.content}</div>
          </div>

          <div className={styles.detailRow}>
            <span className={styles.detailLabel}>상태</span>
            <span className={styles.detailValue}>{notice?.status}</span>
          </div>

          <div className={styles.detailRow}>
            <span className={styles.detailLabel}>노출 시작</span>
            <span className={styles.detailValue}>{formatDisplay(notice?.startsAt)}</span>
          </div>

          <div className={styles.detailRow}>
            <span className={styles.detailLabel}>노출 종료</span>
            <span className={styles.detailValue}>{formatDisplay(notice?.endsAt)}</span>
          </div>

          {notice?.isServiceMaintenance && (
            <>
              <div className={styles.detailRow}>
                <span className={styles.detailLabel}>점검 시작</span>
                <span className={styles.detailValue}>{formatDisplay(notice?.maintenanceStartAt)}</span>
              </div>
              <div className={styles.detailRow}>
                <span className={styles.detailLabel}>점검 종료</span>
                <span className={styles.detailValue}>{formatDisplay(notice?.maintenanceEndAt)}</span>
              </div>
            </>
          )}

          {notice?.scheduledAt && (
            <div className={styles.detailRow}>
              <span className={styles.detailLabel}>예약 발행</span>
              <span className={styles.detailValue}>{formatDisplay(notice.scheduledAt)}</span>
            </div>
          )}

          <div className={styles.detailRow}>
            <span className={styles.detailLabel}>작성자</span>
            <span className={styles.detailValue}>{notice?.authorName}</span>
          </div>

          <div className={styles.detailRow}>
            <span className={styles.detailLabel}>등록일</span>
            <span className={styles.detailValue}>{formatDisplay(notice?.createdAt)}</span>
          </div>

          <div className={styles.detailRow}>
            <span className={styles.detailLabel}>수정일</span>
            <span className={styles.detailValue}>{formatDisplay(notice?.updatedAt)}</span>
          </div>
        </>
      )}
    </FormModal>
  );
};
