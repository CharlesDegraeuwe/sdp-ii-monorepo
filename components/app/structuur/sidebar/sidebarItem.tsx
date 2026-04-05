import Link from 'next/link';
import { SidebarItemProps } from '@/components/app/structuur/sidebar/SidebarItemProps';
import { usePathname } from 'next/navigation';

const SidebarItem = (props: SidebarItemProps) => {
  const pathname = usePathname();
  const { collapsed, icon, title, url } = props;

  return (
    <Link
      href={`/${url}`}
      className={`w-full h-150 flex hover:bg-zinc-200  flex-col gap-1 justify-center items-center text-sm transition-all duration-300 rounded-3xl ${pathname == '/' + url ? 'bg-zinc-200' : ''}`}
    >
      {icon}
      <span className={`${collapsed ? 'hidden' : 'block'} truncate`}>
        {title}
      </span>
    </Link>
  );
};

SidebarItem.displayName = 'SidebarItem';
export default SidebarItem;
