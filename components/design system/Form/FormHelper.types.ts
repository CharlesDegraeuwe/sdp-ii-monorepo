export interface FormHelperProps extends React.HTMLProps<HTMLFormElement> {
  onSubmit: (e: React.FormEvent<HTMLFormElement>) => void | Promise<void>;
  children: React.ReactNode;
  gap?: number;
  noHeight?: boolean;
}
