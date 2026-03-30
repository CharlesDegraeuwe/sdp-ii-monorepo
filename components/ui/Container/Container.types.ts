export interface ContainerProps extends React.HTMLProps<HTMLDivElement> {
  children?: React.ReactNode;
  color?: string;
  label?: string;
  icon?: React.ReactNode;
  error?: string;
  onClick?: () => void;
}
