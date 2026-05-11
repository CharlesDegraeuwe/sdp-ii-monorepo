import { PageContainer } from '@/components/design-system/PageContainer';
import { Label } from '@/components/design-system/Label';
import LoginForm from '@/components/auth/login/LoginForm';
import { Suspense } from 'react';
import type { Metadata } from 'next';
import Image from 'next/image';
import { AnimateOnMount } from '@/components/design-system/AnimateOnMount';

export const metadata: Metadata = {
  title: 'Login | Delaware Suite',
};

export default function Page() {
  return (
    <main className="w-full min-h-screen flex-1 bg-delaware_red">
      <div
        className={
          'relative flex flex-col lg:flex-row p-6 lg:p-10 items-center justify-center lg:justify-end w-full min-h-screen bg-linear-180 from-delaware_red to-rose-700'
        }
      >
        {/* Logo – always top left */}
        <div className={'absolute top-6 left-6'}>
          <AnimateOnMount delay={100}>
            <Image
              src={'/logo-light.png'}
              width={300}
              height={0}
              alt={'logo'}
              className={'h-7 w-fit'}
            />
          </AnimateOnMount>
        </div>

        {/* Left side tagline – desktop only */}
        <div
          className={
            'hidden lg:flex absolute left-10 top-1/4 translate-y-1/2 text-[3rem] text-white font-futura font-bold flex-col'
          }
        >
          <AnimateOnMount delay={100}>
            <span>People</span>
          </AnimateOnMount>
          <AnimateOnMount delay={200}>
            <span>Planning</span>
          </AnimateOnMount>
          <AnimateOnMount delay={300}>
            <span>Presentation</span>
          </AnimateOnMount>
          <AnimateOnMount delay={400}>
            <span className={'text-3xl font-thin mt-3'}>
              Welkom bij Delaware Suite
            </span>
          </AnimateOnMount>
        </div>

        {/* Mobile tagline – shown above card on small screens */}
        <div className={'lg:hidden w-full flex flex-col pt-16 pb-8 text-white'}>
          <AnimateOnMount delay={100}>
            <span className={'text-4xl font-bold'}>People</span>
          </AnimateOnMount>
          <AnimateOnMount delay={200}>
            <span className={'text-4xl font-bold'}>Planning</span>
          </AnimateOnMount>
          <AnimateOnMount delay={300}>
            <span className={'text-4xl font-bold'}>Presentation</span>
          </AnimateOnMount>
          <AnimateOnMount delay={400}>
            <span className={'text-xl font-thin mt-2'}>
              Welkom bij Delaware Suite
            </span>
          </AnimateOnMount>
        </div>

        {/* Login card */}
        <div
          className={
            'relative w-full lg:max-w-[50rem] lg:w-1/2 gap-5 p-8 lg:p-10 xl:p-40 shadow-2xl flex-col flex items-center rounded-4xl bg-white justify-center'
          }
        >
          <div className={'flex flex-col gap-3 w-full'}>
            <Label text="Log in op je account " variant={'title'} />
          </div>
          <Suspense fallback={<div>Laden...</div>}>
            <LoginForm />
          </Suspense>

          <span
            className={
              'absolute bottom-6 left-1/2 text-sm opacity-50 -translate-x-1/2 whitespace-nowrap'
            }
          >
            copyright 2026 • alle rechten voorbehouden
          </span>
        </div>
      </div>
    </main>
  );
}
