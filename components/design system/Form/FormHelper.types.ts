export interface FormHelperProps extends React.HTMLProps<HTMLFormElement> {
  onSubmit: (e: React.FormEvent) => void | Promise<void>;
  children: React.ReactNode;
  gap?: number;
  noHeight?: boolean;
}
