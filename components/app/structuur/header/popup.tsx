'use client';

import { PopupProps } from '@/components/app/structuur/header/popupprops';
import { IoServerOutline } from 'react-icons/io5';
import { signOut } from 'next-auth/react';
import Link from 'next/link';
import {
  IoIosSettings,
  IoMdAddCircle,
  IoMdAddCircleOutline,
} from 'react-icons/io';
import { HiOutlineLogout } from 'react-icons/hi';

export default function Popup({ popupRef, isOpen }: PopupProps) {
  const onClick = () => {
    signOut({ callbackUrl: '/login' });
  };

  return (
    <div
      ref={popupRef}
      className={`absolute right-0 top-10
                z-[9999] w-55 p-3 border border-zinc-300
                rounded-2xl text-black 
                bg-zinc-100 gap-2 flex flex-col h-fit
                shadow-xl shadow-black/10 dark:shadow-black/30
                transition-all duration-300 ease-out origin-top
                ${
                  isOpen
                    ? 'opacity-100 scale-100 translate-y-0 visible pointer-events-auto'
                    : 'opacity-0 scale-95 -translate-y-2 invisible pointer-events-none'
                }
              `}
      onClick={(e) => e.stopPropagation()}
    >
      <div className="w-full flex flex-col gap-1">
        <span className={'w-full text-xs text-zinc-400'}>Account</span>
        <div className="w-full h-fit flex flex-col">
          <Link
            href={'/instellingen'}
            className={
              'w-full hover:bg-zinc-400/20 rounded-lg p-2 text-sm flex flex-row items-center justify-between cursor-pointer'
            }
          >
            <span>Instellingen</span>
            <IoIosSettings />
          </Link>
          <button
            onClick={onClick}
            className={
              'w-full hover:bg-zinc-400/20 rounded-lg p-2 text-sm flex flex-row items-center justify-between cursor-pointer'
            }
          >
            <span>Log out</span>
            <HiOutlineLogout />
          </button>
        </div>
      </div>
      <hr className={'rounded-full border-zinc-300'} />
      <div className="w-full flex flex-col gap-1">
        <span className={'w-full text-xs text-zinc-400'}>Admin</span>
        <div className="w-full h-fit flex flex-col">
          <Link
            href={'/admin/creeer-werknemer'}
            className={
              'w-full hover:bg-zinc-400/20 rounded-lg p-2 text-sm flex flex-row items-center justify-between cursor-pointer'
            }
          >
            <span>Creëer werknemer</span>
            <IoMdAddCircleOutline />
          </Link>
          <Link
            href={'/admin/creeer-manager'}
            className={
              'w-full hover:bg-zinc-400/20 rounded-lg p-2 text-sm flex flex-row items-center justify-between cursor-pointer'
            }
          >
            <span>Creëer manager</span>
            <IoMdAddCircle />
          </Link>
          <Link
            href={'/admin/beheer-gebruikers'}
            className={
              'w-full hover:bg-zinc-400/20 rounded-lg p-2 text-sm flex flex-row items-center justify-between cursor-pointer'
            }
          >
            <span>Beheer gebruikers</span>
            <IoServerOutline />
          </Link>
        </div>
      </div>
    </div>
  );
}
