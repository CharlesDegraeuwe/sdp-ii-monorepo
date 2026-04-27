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
    <PageContainer>
      <div
        className={
          'relative flex flex-row p-10 items-center justify-end w-full h-full bg-delaware_red'
        }
      >
        <div className={'absolute top-10 left-10'}>
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
        <div
          className={
            'relative max-w-[50rem] w-1/2 gap-5 p-40 h-full flex-col flex items-center rounded-4xl bg-white justify-center'
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
              'absolute bottom-10 left-1/2 text-sm opacity-50 -translate-1/2'
            }
          >
            copyright 2026 • alle rechten voorbehouden
          </span>
        </div>
      </div>
    </PageContainer>
  );
}
