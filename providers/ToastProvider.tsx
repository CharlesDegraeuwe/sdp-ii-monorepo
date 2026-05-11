'use client';
import React from 'react';
import Toast from '@/components/design-system/Toast/Toast';
import { ToastVariant } from '@/components/design-system/Toast/Toast.types';

type ToastItem = {
  id: string;
  message: string;
  variant?: ToastVariant;
  duration?: number;
};

// eslint-disable-next-line @typescript-eslint/no-unused-vars
const test = {
  id: 'qsfdqsfqsdf',
  message:
    'super mega lange testmessage voor in de toast om te zien of de overflow deftig werkt',
  variant: 'error',
};

type ToastContextValue = {
  show: (
    message: string,
    options?: { variant?: ToastVariant; duration?: number },
  ) => void;
  success: (message: string, duration?: number) => void;
  error: (message: string, duration?: number) => void;
  warning: (message: string, duration?: number) => void;
  info: (message: string, duration?: number) => void;
  dismiss: (id: string) => void;
};

const ToastContext = React.createContext<ToastContextValue | null>(null);

export const ToastProvider = ({ children }: { children: React.ReactNode }) => {
  const [toasts, setToasts] = React.useState<ToastItem[]>([]);

  const dismiss = React.useCallback((id: string) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  const show = React.useCallback(
    (
      message: string,
      options?: { variant?: ToastVariant; duration?: number },
    ) => {
      const id =
        typeof crypto !== 'undefined' && 'randomUUID' in crypto
          ? crypto.randomUUID()
          : Math.random().toString(36).slice(2);
      setToasts((prev) => [
        ...prev,
        { id, message, variant: options?.variant, duration: options?.duration },
      ]);
    },
    [],
  );

  const value = React.useMemo<ToastContextValue>(
    () => ({
      show,
      success: (m, d) => show(m, { variant: 'success', duration: d }),
      error: (m, d) => show(m, { variant: 'error', duration: d }),
      warning: (m, d) => show(m, { variant: 'warning', duration: d }),
      info: (m, d) => show(m, { variant: 'info', duration: d }),
      dismiss,
    }),
    [show, dismiss],
  );

  return (
    <ToastContext.Provider value={value}>
      {children}
      <div
        className={
          'fixed bottom-7 right-7 z-[9999] flex flex-col gap-2 items-end pointer-events-none'
        }
      >
        {toasts.map((t) => (
          <div key={t.id} className={'pointer-events-auto'}>
            <Toast
              message={t.message}
              variant={t.variant}
              duration={t.duration}
              onClose={() => dismiss(t.id)}
            />
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
};

export const useToast = () => {
  const ctx = React.useContext(ToastContext);
  if (!ctx) {
    throw new Error('useToast moet binnen ToastProvider gebruikt worden');
  }
  return ctx;
};
