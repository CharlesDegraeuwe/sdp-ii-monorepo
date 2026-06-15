export interface FormHelperProps extends React.HTMLProps<HTMLFormElement> {
  onSubmit: (e: React.FormEvent) => Promise<void>;
  children: React.ReactNode;
  gap?: number;
}
