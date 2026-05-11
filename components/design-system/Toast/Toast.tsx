'use client';
import { ToastProps } from '@/components/design-system/Toast/Toast.types';
import { IoClose } from 'react-icons/io5';
import { Button } from '@/components/design-system/Button';

const variantStyles = {
  success: 'bg-green-500/20 border-green-400/30',
  error: 'bg-red-500/20 border-red-400/30',
  warning: 'bg-yellow-500/20 border-yellow-400/30',
  info: 'bg-blue-500/20 border-blue-400/30',
};

const Toast = (props: ToastProps) => {
  const { message, error, variant, onDismiss } = props;
  return (
    <div
      className={
        'border shadow-2xl bg-white/50 backdrop-blur-2xl rounded-lg border-zinc-400/30 ' +
        `min-w-48 max-w-72 h-10 flex items-center justify-between px-3 gap-2 ${variant && variantStyles[variant]}`
      }
    >
      <span className={'flex-1 text-sm truncate'}>{message}</span>
      <Button
        px={'px-0'}
        variant="ghost"
        icon={<IoClose size={15} />}
        onClick={onDismiss}
      />
    </div>
  );
};

Toast.displayName = 'Toast';
export default Toast;
