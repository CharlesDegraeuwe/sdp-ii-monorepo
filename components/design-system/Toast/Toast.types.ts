export interface ToastProps extends React.HTMLProps<HTMLDivElement> {
  message?: string;
  error?: boolean;
  variant?: 'success' | 'error' | 'warning' | 'info' | 'ghost';
}
