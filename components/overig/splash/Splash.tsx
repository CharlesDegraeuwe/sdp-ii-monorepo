'use client';
import Image from 'next/image';
import { useSplash } from '@/providers/SplashProvider';
import { useEffect } from 'react';

const SplashOverlay = () => {
  const date = new Date();
  const { splashOpen, setSplashOpen } = useSplash();

  useEffect(() => {
    const TIME: number = 1000;
    setTimeout(() => setSplashOpen(false), TIME);
  }, [splashOpen]);

  return (
    <div
      className={
        'min-w-screen relative z-[999999] min-h-screen bg-zinc-50 flex items-center justify-center'
      }
    >
      <div className={'w-1/4 h-50 items-center justify-center flex flex-col'}>
        <Image
          src={'/logo.svg'}
          width={0}
          height={0}
          alt={'logo'}
          className={'h-20 w-fit '}
        />
      </div>
      <span
        className={
          'absolute bottom-10 left-1/2 -translate-x-1/2 text-zinc-400 text-sm'
        }
      >
        copyright {date.getFullYear()} • alle rechten voorbehouden
      </span>
    </div>
  );
};

SplashOverlay.displayName = 'SplashOverlay';
export default SplashOverlay;
