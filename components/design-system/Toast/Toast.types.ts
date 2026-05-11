export type ToastVariant = 'success' | 'error' | 'warning' | 'info';

export type ToastProps = {
  message: string;
  variant?: ToastVariant;
  duration?: number;
  open?: boolean;
  onClose?: () => void;
};
