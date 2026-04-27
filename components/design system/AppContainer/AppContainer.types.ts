export interface AppContainerProps extends React.HTMLProps<HTMLDivElement> {
  children?: React.ReactNode;
  color?: string;
  label?: string;
  icon?: React.ReactNode;
  error?: string;
  onClick?: () => void;
  textColor?: string;
  gradient?: boolean;
  from?: string;
  to?: string;
  loading?: boolean;
}
