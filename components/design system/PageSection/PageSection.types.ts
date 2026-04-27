import { ReactNode } from 'react';

export interface PageSectionProps extends React.HTMLProps<HTMLDivElement> {
  icon?: ReactNode;
  title?: string;
  danger?: boolean;
}
