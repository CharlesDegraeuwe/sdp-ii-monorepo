export interface InputProps extends React.HTMLProps<HTMLTextAreaElement> {
  placeholder?: string;
  error?: string;
  errorOption?: boolean;
  type?: 'text' | 'password';
}
