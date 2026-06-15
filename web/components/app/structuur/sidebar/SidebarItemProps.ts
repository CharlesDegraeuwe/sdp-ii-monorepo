export interface SidebarItemProps {
  url: string;
  title: string;
  icon?: React.ReactNode;
  collapsed: boolean;
  onNavigate?: () => void;
}
