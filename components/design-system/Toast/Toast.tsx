'use client';
import React, { useEffect, useState } from 'react';
import { ToastProps } from '@/components/design-system/Toast/Toast.types';
import { IoClose } from 'react-icons/io5';
import {
  IoCheckmarkCircle,
  IoAlertCircle,
  IoWarning,
  IoInformationCircle,
} from 'react-icons/io5';
import { Button } from '@/components/design-system/Button';

const variantStyles = {
  success: 'bg-green-500/20 border-green-400/30',
  error: 'bg-red-500/20 border-red-400/30',
  warning: 'bg-yellow-500/20 border-yellow-400/30',
  info: 'bg-blue-500/20 border-blue-400/30',
  ghost: '',
};

const variantIcons = {
  success: <IoCheckmarkCircle className={'text-green-600'} size={18} />,
  error: <IoAlertCircle className={'text-red-600'} size={18} />,
  warning: <IoWarning className={'text-yellow-600'} size={18} />,
  info: <IoInformationCircle className={'text-blue-600'} size={18} />,
};

const Toast = (props: ToastProps) => {
  const { message, variant, duration = 4000, onClose, open = true } = props;
  const [visible, setVisible] = useState<boolean>(open);

  useEffect(() => {
    setVisible(open);
  }, [open]);

  useEffect(() => {
    if (!visible || duration <= 0) return;
    const timer = setTimeout(() => {
      setVisible(false);
      onClose?.();
    }, duration);
    return () => clearTimeout(timer);
  }, [visible, duration, onClose]);

  const handleClose = () => {
    setVisible(false);
    onClose?.();
  };

  return (
    <div
      role={'status'}
      aria-live={'polite'}
      className={`border shadow-2xl bg-white/50 backdrop-blur-2xl
                      rounded-2xl border-zinc-400/30
                      min-w-64 max-w-sm h-fit
                      flex items-center gap-3 pl-4 pr-2 py-2
                      transition-all duration-300 ease-out
                      ${
                        visible
                          ? 'opacity-100 translate-x-0 scale-100 pointer-events-auto'
                          : 'opacity-0 translate-x-4 scale-95 pointer-events-none'
                      }
                      ${variant ? variantStyles[variant] : ''}
                    `}
    >
      {variant && variantIcons[variant]}
      <span
        className={'flex-1 text-sm text-zinc-800 truncate, overflow-hidden'}
      >
        {message}
      </span>
      <Button
        px={'px-3'}
        variant={'ghost'}
        icon={<IoClose size={15} />}
        onClick={handleClose}
      />
    </div>
  );
};

Toast.displayName = 'Toast';
export default Toast;
