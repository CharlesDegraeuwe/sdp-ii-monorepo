'use client';
import Link from 'next/link';
import { FaUser } from 'react-icons/fa6';
import { useEffect, useRef, useState } from 'react';
import { PiBell } from 'react-icons/pi';
import { HiOutlineChevronUpDown } from 'react-icons/hi2';
import Popup from '@/components/app/structuur/header/popup';
import { useUser } from '@/providers/UserProvider';

export default function AppHeader() {
  const [isOpen, setIsOpen] = useState(false);
  const triggerRef = useRef<HTMLDivElement>(null);
  const popupRef = useRef<HTMLDivElement>(null);
  const user = useUser();
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

  console.log(user);
  return (
    <div
      className={
        'absolute top-0 w-screen h-fit pt-10 flex flex-row justify-end px-14 gap-5'
      }
    >
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
            'relative gap-2 cursor-pointer select-none  active:scale-95 transition-all duration-300 flex items-center justify-end'
          }
        >
          <div
            className={
              'min-w-8 min-h-8 text-white overflow-hidden active:scale-95 transition-all duration-300 flex items-center justify-center rounded-full border border-zinc-50 bg-linear-0 from-rose-500 to-rose-600 hover:border-zinc-300'
            }
          >
            {user.user?.voornaam.split('')[0] || <FaUser size={12} />}
          </div>
          <div className={'w-fit flex flex-row items-center justify-center'}>
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
  );
}
