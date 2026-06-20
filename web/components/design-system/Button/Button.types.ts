type variant =
  | 'primary'
  | 'secondary'
  | 'outline'
  | 'prompt'
  | 'ghost'
  | 'submit'
  | 'approve';
export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  children?: React.ReactNode;
  label?: string;
  icon?: React.ReactNode;
  error?: string;
  loading?: boolean;
  form?: 'rectangle' | 'square' | 'circle';
  variant?: variant;
  textColor?: 'white' | 'black';
  textSize?: 'sm' | 'md' | 'lg';
  borderColor?: 'default' | 'red';
  gradient?: 'rose-blue' | 'purple-pink';
  iconRight?: React.ReactNode;
  iconLeft?: React.ReactNode;
  size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl' | 'icon';
  px?: 'px-0' | 'px-3' | 'px-5' | 'px-8';
}
