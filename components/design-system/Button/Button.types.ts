export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  children?: React.ReactNode;
  label?: string;
  icon?: React.ReactNode;
  error?: string;
  loading?: boolean;

  variant?: 'primary' | 'secondary' | 'outline' | 'prompt' | 'ghost' | 'submit';
  textColor?: 'white' | 'black';
  textSize?: 'sm' | 'md' | 'lg';
  borderColor?: 'default' | 'red';
  gradient?: 'rose-blue' | 'purple-pink';

  iconRight?: React.ReactNode;
  iconLeft?: React.ReactNode;

  px?: 'px-0' | 'px-3' | 'px-5' | 'px-8';
}
