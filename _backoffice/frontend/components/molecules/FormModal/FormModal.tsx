'use client';

import React, { useState } from 'react';
import { Modal } from '../Modal/Modal';
import { ConfirmModal } from '../ConfirmModal/ConfirmModal';
import { Button } from '../../atoms/Button/Button';
import styles from './FormModal.module.css';

type ButtonVariant = 'primary' | 'success' | 'secondary' | 'danger';

export interface FormModalAction {
  label: string;
  onClick: () => void;
  variant?: ButtonVariant;
}

export interface DeleteConfig {
  onDelete: () => void;
  confirmTitle: string;
  confirmMessage: React.ReactNode;
}

interface FormModalProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  children: React.ReactNode;
  actions: FormModalAction[];
  deleteConfig?: DeleteConfig;
}

export const FormModal: React.FC<FormModalProps> = ({
  isOpen,
  onClose,
  title,
  children,
  actions,
  deleteConfig,
}) => {
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);

  const handleConfirmDelete = () => {
    deleteConfig?.onDelete();
    setIsDeleteModalOpen(false);
    onClose();
  };

  return (
    <>
      <Modal isOpen={isOpen} onClose={onClose} title={title}>
        <div className={styles.body}>
          {children}

          <div className={styles.actions}>
            {deleteConfig && (
              <Button
                variant="danger"
                style={{ width: 'auto' }}
                onClick={() => setIsDeleteModalOpen(true)}
              >
                삭제
              </Button>
            )}
            <div className={styles.rightActions}>
              {actions.map((action) => (
                <Button
                  key={action.label}
                  variant={action.variant ?? 'primary'}
                  style={{ width: 'auto' }}
                  onClick={action.onClick}
                >
                  {action.label}
                </Button>
              ))}
            </div>
          </div>
        </div>
      </Modal>

      {deleteConfig && (
        <ConfirmModal
          isOpen={isDeleteModalOpen}
          onClose={() => setIsDeleteModalOpen(false)}
          onConfirm={handleConfirmDelete}
          title={deleteConfig.confirmTitle}
        >
          {deleteConfig.confirmMessage}
        </ConfirmModal>
      )}
    </>
  );
};
