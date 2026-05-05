import Sidebar from '@/components/app/structuur/sidebar/sidebar';
import AppHeader from '@/components/app/structuur/header/header';
import BreadCrumbProvider from '@/providers/BreadCrumbProvider';
import BreadCrumbs from '@/components/design-system/BreadCrumbs/BreadCrumbs';

export default function AppLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <>
      <BreadCrumbProvider>
        <AppHeader />
        <section className="flex flex-row w-full h-screen bg-white">
          <Sidebar />
          <div className="flex flex-col w-full h-full bg-zinc-300/40 pr-7 pt-20 py-7 scroll-hidden">
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
