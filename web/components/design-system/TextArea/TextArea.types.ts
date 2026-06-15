export interface TextAreaProps extends React.HTMLProps<HTMLTextAreaElement> {
  placeholder?: string;
  error?: string;
  errorOption?: boolean;
}
