// components/app/AppLayoutClient.tsx
'use client';
import { memo, ReactNode } from 'react';
import { UserProvider } from '@/providers/UserProvider';
import AppHeader from '@/components/app/structuur/header/header';
import Sidebar from '@/components/app/structuur/sidebar/sidebar';

const MemoizedHeader = memo(AppHeader);
const MemoizedBurger = memo(Sidebar);

function AppLayoutInner({ children }: { children: ReactNode }) {
  return (
    <div className="h-screen min-w-screen flex flex-col overflow-hidden">
      <MemoizedHeader />

      <div className="flex-1 min-h-0 w-full flex flex-row relative">
        <div className={'min-w-57 h-full'}>
          <MemoizedBurger />
        </div>
        <div className={'w-full flex items-center justify-center h-full'}>
          <main
            className="flex-1 min-h-0 xl:w-9/10 3xl:w-1/3 h-full pl-5 pr-5 lg:pl-10 lg:pr-77 overflow-y-scroll scroll-hidden [&::-webkit-scrollbar]:hidden
                    [-ms-overflow-style:none]
                [scrollbar-width:none]"
          >
            {children}
          </main>
        </div>
      </div>
    </div>
  );
}

export default function AppLayoutClient({ children }: { children: ReactNode }) {
  return (
    <UserProvider>
      <AppLayoutInner>{children}</AppLayoutInner>
    </UserProvider>
  );
}
