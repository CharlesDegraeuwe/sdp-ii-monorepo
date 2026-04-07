export interface ContainerProps extends React.HTMLProps<HTMLDivElement> {
  children?: React.ReactNode;
  color?: string;
  label?: string;
  icon?: React.ReactNode;
  error?: string;
  onClick?: () => void;
  width?: number | string;
  height?: number | string;
  pointer?: boolean;
  padding?: number | string;
  flex?: number | string;
  gap?: number | string;
  flexDirection?: 'row' | 'col';
  indent?: boolean;
}
