export interface LabelProps extends React.HTMLAttributes<HTMLSpanElement> {
  text?: string;
  weight?: number;
  color?: string;
  size?: number;
  className?: string;
  children?: React.ReactNode;
  icon?: React.ReactNode;
  px?: number;
  py?: number;
  p?: number;
}
