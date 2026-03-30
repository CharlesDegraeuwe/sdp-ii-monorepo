export interface InputProps extends React.HTMLProps<HTMLInputElement> {
  placeholder?: string;
  error?: string;
  type?: 'text' | 'password';
}
