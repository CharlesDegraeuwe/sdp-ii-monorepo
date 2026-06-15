'use client';
import { LuLayoutDashboard } from 'react-icons/lu';
import { useState } from 'react';
import Link from 'next/link';
import {
  MdCheck,
  MdOutlineMap,
  MdOutlineCancel,
  MdGroups,
  MdAdminPanelSettings,
  MdClose,
} from 'react-icons/md';
import { usePathname } from 'next/navigation';
import SidebarItem from '@/components/app/structuur/sidebar/sidebarItem';
import Image from 'next/image';
import { IoCalendarOutline } from 'react-icons/io5';

const links = [
  {
    url: 'overzicht',
    title: 'overzicht',
    icon: <LuLayoutDashboard size={20} />,
  },
  {
    url: 'planner',
    title: 'planner',
    icon: <IoCalendarOutline size={20} />,
  },
  {
    url: 'taken',
    title: 'taken',
    icon: <MdCheck size={20} />,
  },
  {
    url: 'locaties',
    title: 'locaties',
    icon: <MdOutlineMap size={20} />,
  },
  {
    url: 'afwezigheden',
    title: 'afwezigheden',
    icon: <MdOutlineCancel size={20} />,
  },
  {
    url: 'teams',
    title: 'teams',
    icon: <MdGroups size={20} />,
  },
];

interface SidebarProps {
  mobileOpen?: boolean;
  onClose?: () => void;
}

export default function Sidebar({ mobileOpen = false, onClose }: SidebarProps) {
  const ugly = false;
  const pathname = usePathname();

  return (
    <div
      className={`
        fixed md:relative
        w-56 md:w-16 lg:w-50
        h-screen flex flex-col z-[999]
        transition-all duration-300
        ${mobileOpen ? 'translate-x-0' : '-translate-x-full md:translate-x-0'}
        ${ugly && 'bg-linear-180 from-rose-600 to-rose-500'}
        bg-zinc-300/40 py-10
      `}
    >
      <button
        onClick={onClose}
        className={
          'md:hidden absolute top-4 right-4 w-8 h-8 flex items-center justify-center rounded-full hover:bg-zinc-200 transition-all duration-300'
        }
        aria-label="Sluiten"
      >
        <MdClose size={20} />
      </button>

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
          {links.map((link, key) => (
            <SidebarItem
              key={key}
              url={link.url}
              title={link.title}
              icon={link.icon}
              collapsed={false}
              onNavigate={onClose}
            />
          ))}
        </div>
        <div className={'w-full h-1/2 flex flex-col justify-end'}>
          <Link
            href="/admin"
            onClick={onClose}
            className={`w-full h-1/5 flex hover:bg-zinc-200 flex-col gap-1 justify-center items-center text-sm rounded-3xl ${pathname == '/admin' ? 'bg-zinc-200' : ''}`}
          >
            <MdAdminPanelSettings size={20} />
            <span className={'block md:hidden lg:block truncate'}>admin</span>
          </Link>
        </div>
      </div>
    </div>
  );
}
