'use client';
import { useState } from 'react';
import Sidebar from '@/components/app/structuur/sidebar/sidebar';
import AppHeader from '@/components/app/structuur/header/header';
import BreadCrumbProvider from '@/providers/BreadCrumbProvider';
import BreadCrumbs from '@/components/design system/BreadCrumbs/BreadCrumbs';

export default function AppLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const [mobileOpen, setMobileOpen] = useState(false);

  return (
    <>
      <BreadCrumbProvider>
        <AppHeader onMenuClick={() => setMobileOpen((o) => !o)} />
        {mobileOpen && (
          <div
            className="fixed inset-0 bg-black/30 z-[998] md:hidden"
            onClick={() => setMobileOpen(false)}
          />
        )}
        <section className="flex flex-row w-full h-screen bg-white">
          <Sidebar
            mobileOpen={mobileOpen}
            onClose={() => setMobileOpen(false)}
          />
          <div className="flex flex-col w-full h-full bg-zinc-300/40 pr-4 md:pr-7 pt-16 md:pt-20 pb-4 md:pb-7 scroll-hidden overflow-y-auto">
            <div className={'min-h-10 w-full flex flex-row items-center'}>
              <BreadCrumbs />
            </div>

            {children}
          </div>
        </section>
      </BreadCrumbProvider>
    </>
  );
}
