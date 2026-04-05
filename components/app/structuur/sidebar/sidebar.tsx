'use client';
import { FiSidebar } from 'react-icons/fi';
import { LuLayoutDashboard } from 'react-icons/lu';
import { useEffect, useRef, useState, useCallback } from 'react';
import Link from 'next/link';
import {
  MdFormatListBulleted, // planning
  MdCheck, // tasks (taken)
  MdOutlineMap, // plants (locaties)
  MdOutlineCancel, // absense (afwezigheid)
  MdGroups, // teams
  MdAdminPanelSettings, // admin
} from 'react-icons/md';
import { usePathname } from 'next/navigation';
import SidebarItem from '@/components/app/structuur/sidebar/sidebarItem';
import Image from 'next/image';

const links = [
  {
    url: 'overzicht',
    title: 'overzicht',
    icon: <LuLayoutDashboard size={25} />,
  },
  {
    url: 'planner',
    title: 'planner',
    icon: <MdFormatListBulleted size={25} />,
  },
  {
    url: 'taken',
    title: 'taken',
    icon: <MdCheck size={25} />,
  },
  {
    url: 'locaties',
    title: 'locaties',
    icon: <MdOutlineMap size={25} />,
  },
  {
    url: 'afwezigheden',
    title: 'afwezigheden',
    icon: <MdOutlineCancel size={25} />,
  },
  {
    url: 'teams',
    title: 'teams',
    icon: <MdGroups size={25} />,
  },
];
export default function Sidebar() {
  const [collapsed, setCollapsed] = useState(true);
  const [isDragging, setIsDragging] = useState(false);
  const ugly = false;
  const startXRef = useRef(0);
  const wasCollapsedRef = useRef(true);
  const pathname = usePathname();
  const handleDragStart = (e: React.MouseEvent) => {
    e.preventDefault();
    setIsDragging(true);
    startXRef.current = e.clientX;
    wasCollapsedRef.current = collapsed;
    document.body.style.cursor = 'ew-resize';
    document.body.style.userSelect = 'none';
  };

  const handleDragMove = useCallback(
    (e: MouseEvent) => {
      if (!isDragging) return;
      const deltaX = e.clientX - startXRef.current;

      if (wasCollapsedRef.current && deltaX > 30) {
        setCollapsed(false);
      } else if (!wasCollapsedRef.current && deltaX < -30) {
        setCollapsed(true);
      }
    },
    [isDragging],
  );

  const handleDragEnd = useCallback(() => {
    if (!isDragging) return;
    setIsDragging(false);
    document.body.style.cursor = '';
    document.body.style.userSelect = '';
  }, [isDragging]);

  useEffect(() => {
    if (!isDragging) return;
    window.addEventListener('mousemove', handleDragMove);
    window.addEventListener('mouseup', handleDragEnd);
    return () => {
      window.removeEventListener('mousemove', handleDragMove);
      window.removeEventListener('mouseup', handleDragEnd);
    };
  }, [isDragging, handleDragMove, handleDragEnd]);

  return (
    <div
      className={`${collapsed ? 'w-30' : 'w-50'} h-screen flex flex-col z-[998] transition-all duration-200 bg-zinc-100 ${ugly && 'bg-linear-180 from-rose-600 to-rose-500'} py-10`}
    >
      <div className="flex flex-col items-center justify-baseline h-full text-zinc-900 font-sfpro px-5 gap-20">
        {collapsed ? (
          <div className="relative justify-center flex items-center group">
            <button
              onClick={() => setCollapsed(!collapsed)}
              className={`p-2 ${collapsed ? 'cursor-e-resize' : 'cursor-w-resize'} transition-opacity duration-300 opacity-0 bg-zinc-100 hover:opacity-100 relative z-20`}
            >
              <FiSidebar size={23} />
            </button>
            <Image
              src={'./icon.svg'}
              width={14}
              height={14}
              alt={'icon'}
              className={'absolute z-10'}
            />
          </div>
        ) : (
          <button
            onClick={() => setCollapsed(!collapsed)}
            className={`p-2 ${collapsed ? 'cursor-e-resize' : 'cursor-w-resize'} transition-opacity duration-300 bg-zinc-100 hover:opacity-100 relative z-20`}
          >
            <FiSidebar size={23} />
          </button>
        )}

        <div className={'w-full h-1/2 flex flex-col gap-5'}>
          {links.map((link, key) => (
            <SidebarItem
              key={key}
              url={link.url}
              title={link.title}
              icon={link.icon}
              collapsed={collapsed}
            />
          ))}
        </div>
        <div className={'w-full h-1/2 flex flex-col justify-end'}>
          <Link
            href="/admin"
            className={`w-full h-1/5 flex hover:bg-zinc-200 flex-col gap-1 justify-center items-center text-sm rounded-3xl ${pathname == '/admin' ? 'bg-zinc-200' : ''}`}
          >
            <MdAdminPanelSettings size={25} />
            <span className={`${collapsed ? 'hidden' : 'block'} truncate`}>
              admin
            </span>
          </Link>
        </div>
      </div>

      <div
        onMouseDown={handleDragStart}
        className="absolute right-0 top-0 h-full w-2 cursor-ew-resize"
      />
    </div>
  );
}
