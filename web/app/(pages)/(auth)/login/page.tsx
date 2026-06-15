import type { Metadata } from 'next';
import Image from 'next/image';
import { AnimateOnMount } from '@/components/design-system/AnimateOnMount';
import LoginForm from '@/components/auth/login/LoginForm';

export const metadata: Metadata = {
  title: 'Login | Delaware Suite',
};

export default function Page() {
  return (
    <main className="w-full flex-1 bg-delaware_red overflow-x-hidden">
      <div
        className={
          'relative flex flex-col sm:flex-row p-6 sm:p-10 items-center justify-center sm:justify-end w-full min-h-full h-full bg-linear-180 from-delaware_red to-rose-700'
        }
      >
        {/* Branding – hidden on small mobile, shown on tablet+ */}
        <div
          className={
            'hidden md:flex absolute left-10 top-1/4 translate-y-1/2 text-[3rem] text-white font-futura font-bold flex-col'
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
        <div className={'absolute top-6 left-6 sm:top-10 sm:left-10'}>
          <AnimateOnMount delay={100}>
            <Image
              src={'/logo-light.png'}
              width={300}
              height={0}
              alt={'logo'}
              className={'h-6 sm:h-7 w-fit'}
            />
          </AnimateOnMount>
        </div>
        <LoginForm />
      </div>
    </main>
  );
}
