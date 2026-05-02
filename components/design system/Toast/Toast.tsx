'use client';
import { ToastProps } from '@/components/design system/Toast/Toast.types';
import { IoClose } from 'react-icons/io5';
import { Button } from '@/components/design system/Button';

const variantStyles = {
  success: 'bg-green-500/20 border-green-400/30',
  error: 'bg-red-500/20 border-red-400/30',
  warning: 'bg-yellow-500/20 border-yellow-400/30',
  info: 'bg-blue-500/20 border-blue-400/30',
};

const Toast = (props: ToastProps) => {
  const { message, error, variant } = props;
  return (
    <div
      className={
        'border shadow-2xl z-[9999] bg-white/50 backdrop-blur-2xl rounded-lg border-zinc-400/30 ' +
        `h-10 w-35 flex items-center justify-center absolute bottom-7 right-7 ${variant && variantStyles[variant]}`
      }
    >
      <span className={'w-full text-center'}>{message}</span>
      <Button px={'px-2 opacity-70'} icon={<IoClose size={15} />} />
    </div>
  );
};

Toast.displayName = 'Toast';
export default Toast;
