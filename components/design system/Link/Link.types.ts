export interface LinkProps extends React.HTMLAttributes<HTMLAnchorElement> {
  href: string;
  label?: string;
  icon?: React.ReactNode;
  rounded?: string;
}
