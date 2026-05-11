'use client';
import Link from 'next/link';
import { FaUser } from 'react-icons/fa6';
import { useEffect, useRef, useState } from 'react';
import { PiBell } from 'react-icons/pi';
import { HiOutlineChevronUpDown } from 'react-icons/hi2';
import { MdMenu } from 'react-icons/md';
import Popup from '@/components/app/structuur/header/popup';
import { useUser } from '@/providers/UserProvider';
import Image from 'next/image';
import { RiWifiOffLine } from 'react-icons/ri';
import { useSidebarStore } from '@/stores/sidebarStore';

export default function AppHeader() {
  const [isOpen, setIsOpen] = useState(false);
  const triggerRef = useRef<HTMLDivElement>(null);
  const popupRef = useRef<HTMLDivElement>(null);
  const user = useUser();
  const { toggle } = useSidebarStore();

  const handleToggle = () => {
    setIsOpen((prevState) => !prevState);
  };

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (
        popupRef.current &&
        !popupRef.current.contains(e.target as Node) &&
        triggerRef.current &&
        !triggerRef.current.contains(e.target as Node)
      ) {
        setIsOpen(false);
      }
    };

    if (isOpen) {
      setTimeout(() => {
        document.addEventListener('mousedown', handleClickOutside);
      }, 0);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isOpen, setIsOpen, triggerRef]);

  return (
    <div
      className={
        'absolute top-0 w-screen z-[9999] flex h-25 flex-row justify-between items-center pl-3 pr-4 sm:pl-5 sm:pr-14 gap-2 sm:gap-5'
      }
    >
      <div className={'h-full flex flex-row gap-1 sm:gap-2 items-center'}>
        {/* Hamburger button – hidden on large screens where sidebar is always visible */}
        <button
          className={
            'lg:hidden p-2 rounded-full hover:bg-zinc-200 active:scale-95 transition-all duration-300 flex items-center justify-center'
          }
          onClick={toggle}
          aria-label="Menu openen"
        >
          <MdMenu size={22} />
        </button>

        <Link
          href={'/overzicht'}
          className={
            'font-delaware text-zinc-900 text-xl flex flex-row gap-0 items-center'
          }
        >
          <Image
            src={'/logo.svg'}
            width={0}
            height={0}
            alt={'logo'}
            className={'h-13 w-fit'}
          />
        </Link>
      </div>

      <div className={'w-fit flex justify-end gap-2 sm:gap-5 items-center'}>
        {!window.navigator.onLine && (
          <div
            className={
              'truncate text-rose-700 font-medium text-sm flex flex-row gap-2 items-center'
            }
          >
            <RiWifiOffLine />
            <span className={'hidden sm:inline'}>je bent offline</span>
          </div>
        )}

        <div className={'w-fit flex flex-row gap-2'}>
          <Link
            href={'/notificaties'}
            className={
              'w-8 h-8 active:scale-95 flex items-center justify-center rounded-full border border-transparent transition-all duration-300 hover:border-zinc-300'
            }
          >
            <PiBell size={20} />
          </Link>
        </div>

        <div className={'relative flex flex-row'}>
          <div
            onClick={handleToggle}
            ref={triggerRef}
            className={
              'relative gap-2 cursor-pointer select-none active:scale-95 transition-all duration-300 flex items-center justify-end'
            }
          >
            <div
              className={
                'min-w-8 min-h-8 text-white overflow-hidden active:scale-95 transition-all duration-300 flex items-center justify-center rounded-full border border-zinc-50 bg-linear-0 from-rose-500 to-rose-600 hover:border-zinc-300'
              }
            >
              {user.user?.voornaam.split('')[0] || <FaUser size={12} />}
            </div>
            {/* Full name – hidden on small screens to save space */}
            <div
              className={
                'hidden sm:flex w-fit flex-row items-center justify-center'
              }
            >
              <span
                className={
                  'w-full h-full flex items-center pointer-events-none font-bold truncate'
                }
              >
                {user.user?.voornaam + ' ' + user.user?.naam}
              </span>
              <HiOutlineChevronUpDown size={20} />
            </div>
          </div>
          <Popup isOpen={isOpen} popupRef={popupRef} />
        </div>
      </div>
    </div>
  );
}
