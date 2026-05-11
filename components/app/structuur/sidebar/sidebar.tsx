'use client';
import { LuLayoutDashboard } from 'react-icons/lu';
import { useEffect, useState } from 'react';
import Link from 'next/link';
import {
  MdCheck,
  MdOutlineMap,
  MdOutlineCancel,
  MdGroups,
  MdAdminPanelSettings,
} from 'react-icons/md';
import { usePathname } from 'next/navigation';
import SidebarItem from '@/components/app/structuur/sidebar/sidebarItem';
import Image from 'next/image';
import { IoCalendarOutline } from 'react-icons/io5';
import { RiChatAiLine } from 'react-icons/ri';
import { useSession } from 'next-auth/react';
import { useSidebarStore } from '@/stores/sidebarStore';

const links = [
  {
    url: 'overzicht',
    title: 'overzicht',
    icon: <LuLayoutDashboard size={20} />,
    role: ['Admin', 'Manager', 'Supervisor', 'Werknemer'],
  },
  {
    url: 'planner',
    title: 'planner',
    icon: <IoCalendarOutline size={20} />,
    role: ['Admin', 'Manager', 'Supervisor', 'Werknemer'],
  },
  {
    url: 'taken',
    title: 'taken',
    icon: <MdCheck size={20} />,
    role: ['Admin', 'Manager', 'Supervisor', 'Werknemer'],
  },
  {
    url: 'locaties',
    title: 'locaties',
    icon: <MdOutlineMap size={20} />,
    role: ['Admin', 'Manager', 'Supervisor'],
  },
  {
    url: 'afwezigheden',
    title: 'afwezigheden',
    icon: <MdOutlineCancel size={20} />,
    role: ['Admin', 'Manager', 'Supervisor', 'Werknemer'],
  },
  {
    url: 'teams',
    title: 'teams',
    icon: <MdGroups size={20} />,
    role: ['Admin', 'Manager', 'Supervisor'],
  },
  {
    url: 'chat',
    title: 'chat',
    icon: <RiChatAiLine size={20} />,
    role: ['Admin', 'Manager', 'Supervisor', 'Werknemer'],
  },
];

export default function Sidebar() {
  const [collapsed] = useState(true);
  const ugly = false;
  const pathname = usePathname();
  const { data: session } = useSession();
  const userRole = session?.user?.rol;
  const { isMobileOpen, close } = useSidebarStore();

  useEffect(() => {
    close();
  }, [pathname, close]);

  return (
    <>
      {/* Backdrop – mobile/tablet only */}
      <div
        className={`fixed inset-0 bg-black/30 z-[998] lg:hidden transition-opacity duration-300 ${
          isMobileOpen
            ? 'opacity-100 pointer-events-auto'
            : 'opacity-0 pointer-events-none'
        }`}
        onClick={close}
      />

      {/* Sidebar drawer */}
      <div
        className={`
          fixed lg:relative
          top-0 left-0
          w-72 lg:w-50
          h-screen flex flex-col z-[999]
          transition-transform duration-300 ease-in-out
          bg-white lg:bg-zinc-300/40 ${ugly && 'bg-linear-180 from-rose-600 to-rose-500'} pt-28 pb-10 lg:py-10
          ${isMobileOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'}
        `}
      >
        <div className="flex flex-col items-center justify-baseline h-full text-zinc-900 font-sfpro px-5 gap-20">
          <div className="relative justify-center flex items-center group">
            <Image
              src={'./icon.svg'}
              width={14}
              height={14}
              alt={'icon'}
              className={'absolute z-10'}
            />
          </div>

          <div className={'w-full h-1/2 flex flex-col gap-5'}>
            {links
              .filter((link) => userRole && link.role.includes(userRole))
              .map((link) => (
                <SidebarItem
                  key={link.url}
                  url={link.url}
                  title={link.title}
                  icon={link.icon}
                  collapsed={false}
                />
              ))}
          </div>

          <div className={'w-full h-1/2 flex flex-col justify-end'}>
            <Link
              href="/admin"
              className={`w-full h-1/5 flex hover:bg-zinc-200 flex-col gap-1 justify-center items-center text-sm rounded-3xl ${pathname == '/admin' ? 'bg-zinc-200' : ''}`}
            >
              <MdAdminPanelSettings size={20} />
              <span className={`${collapsed ? 'hidden' : 'block'} truncate`}>
                admin
              </span>
            </Link>
          </div>
        </div>
      </div>
    </>
  );
}
