'use client';
import Link from 'next/link';
import { FaUser } from 'react-icons/fa6';
import { useEffect, useRef, useState } from 'react';
import { PiBell } from 'react-icons/pi';
import { HiOutlineChevronUpDown } from 'react-icons/hi2';
import { RiMenu3Line, RiCloseLine } from 'react-icons/ri';
import Popup from '@/components/overig/structuur/header/popup';
import { useUser } from '@/providers/UserProvider';
import Image from 'next/image';
import OnlineScanner from '@/components/overig/structuur/header/OnlineScanner';
import { useSidebarStore } from '@/stores/sidebarStore';

export default function AppHeader() {
  const [isOpen, setIsOpen] = useState(false);
  const triggerRef = useRef<HTMLDivElement>(null);
  const popupRef = useRef<HTMLDivElement>(null);
  const user = useUser();
  const { toggle, isMobileOpen } = useSidebarStore();

  const handleToggle = () => {
    setIsOpen((prevState) => !prevState);
  };

  useEffect(() => {
    if (!isOpen) return;

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

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isOpen]);

  return (
    <div
      className={
        'absolute top-0 w-screen z-[9999] flex h-25 flex-row justify-between items-center pl-5 pr-5 sm:pr-8 lg:pr-14 gap-3 lg:gap-5'
      }
    >
      <div className={'h-full flex flex-row gap-2 items-center'}>
        {/* Hamburger – mobile/tablet only */}
        <button
          onClick={toggle}
          className={`lg:hidden w-9 h-9 flex items-center justify-center rounded-full border transition-all duration-300 active:scale-95
            ${
              isMobileOpen
                ? 'bg-zinc-900 border-zinc-900 text-white shadow-md'
                : 'border-transparent hover:border-zinc-300 hover:bg-zinc-100'
            }`}
          aria-label={isMobileOpen ? 'Menu sluiten' : 'Menu openen'}
        >
          {isMobileOpen ? <RiCloseLine size={20} /> : <RiMenu3Line size={20} />}
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
            className={'h-13 w-fit '}
          />
        </Link>
      </div>
      <div className={'w-fit flex justify-end gap-3 lg:gap-5 items-center'}>
        <OnlineScanner />
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
            <div
              className={
                'hidden sm:flex w-fit flex-row items-center justify-center'
              }
            >
              <span
                className={
                  'w-full h-full flex items-center pointer-events-none font-bold truncate max-w-32 lg:max-w-none'
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
