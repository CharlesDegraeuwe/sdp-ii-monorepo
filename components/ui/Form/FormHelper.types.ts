export interface FormHelperProps extends React.HTMLProps<HTMLFormElement> {
  onSubmit: () => void;
  children: React.ReactNode;
  gap?: number;
}
