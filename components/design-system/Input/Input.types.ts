export interface InputProps extends React.HTMLProps<HTMLInputElement> {
  placeholder?: string;
  error?: string;
  errorOption?: boolean;
  type?: 'text' | 'password' | 'date' | 'url' | 'email' | 'tel';
}
