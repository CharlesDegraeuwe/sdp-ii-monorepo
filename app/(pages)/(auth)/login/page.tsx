import type { Metadata } from 'next';
import Image from 'next/image';
import { AnimateOnMount } from '@/components/design-system/AnimateOnMount';
import LoginForm from '@/components/auth/login/LoginForm';

export const metadata: Metadata = {
  title: 'Login | Delaware Suite',
};

export default function Page() {
  return (
    <main className="w-full flex-1 bg-delaware_red">
      <div
        className={
          'relative flex flex-row p-10 items-center justify-end w-full h-full bg-linear-180 from-delaware_red to-rose-700'
        }
      >
        <div
          className={
            'absolute left-10 top-1/4 translate-y-1/2 text-[3rem] text-white font-futura font-bold flex flex-col'
          }
        >
          <AnimateOnMount delay={100}>
            {' '}
            <span>People</span>
          </AnimateOnMount>
          <AnimateOnMount delay={200}>
            {' '}
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
        <LoginForm />
      </div>
    </main>
  );
}
