export interface AppContainerProps extends React.HTMLProps<HTMLDivElement> {
  children?: React.ReactNode;
  loading?: boolean;
  ai?: boolean;
  px?: string;
  py?: string;
}
