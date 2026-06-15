import { PageContainer } from '@/components/design system/PageContainer';
import { Container } from '@/components/design system/Container';
import { Label } from '@/components/design system/Label';
import LoginForm from '@/components/auth/login/LoginForm';
import { Suspense } from 'react';
import type { Metadata } from 'next';
import Image from 'next/image';
import { Button } from '@/components/design system/Button';
import { AnimateOnMount } from '@/components/design system/AnimateOnMount';

export const metadata: Metadata = {
  title: 'Login | Delaware Suite',
};

export default function Page() {
  return (
    <main className="w-full flex-1 bg-delaware_red">
      <div
        className={
          'relative flex flex-col md:flex-row p-5 md:p-10 items-center md:items-stretch justify-center md:justify-end w-full h-full gap-8 md:gap-0'
        }
      >
        <div className={'relative md:absolute md:top-10 md:left-10'}>
          <AnimateOnMount delay={100}>
            <Image
              src={'/logo-light.png'}
              width={300}
              height={0}
              alt={'logo'}
              className={'h-6 md:h-7 w-fit'}
            />
          </AnimateOnMount>
        </div>
        <div
          className={
            'relative w-full max-w-lg md:max-w-[50rem] md:w-1/2 gap-5 p-8 md:p-20 lg:p-40 min-h-[500px] md:h-full flex-col flex items-center rounded-4xl bg-white justify-center'
          }
        >
          <div className={'absolute top-5 right-5'}>
            <Button
              label={'Wachtwoord vergeten'}
              color={'zinc-100 hover:bg-zinc-200'}
              className={'top-10 right-10'}
            />
          </div>

          <div>
            <Label size={'2xl'} text="Log in op je account " />
          </div>
          <Suspense fallback={<div>Laden...</div>}>
            <LoginForm />
          </Suspense>

          <span
            className={
              'absolute bottom-5 md:bottom-10 left-1/2 text-xs md:text-sm opacity-50 -translate-x-1/2 text-center whitespace-nowrap'
            }
          >
            copyright 2026 • alle rechten voorbehouden
          </span>
        </div>
      </div>
    </main>
  );
}
