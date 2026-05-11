import Sidebar from '@/components/overig/structuur/sidebar/sidebar';
import AppHeader from '@/components/overig/structuur/header/header';
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
        <section className="flex flex-row w-full h-screen bg-white overflow-x-hidden">
          <Sidebar />
          <div className="flex flex-col w-full h-full bg-zinc-300/40 pl-3 pr-3 lg:pl-0 lg:pr-7 pt-20 pb-7 scroll-hidden overflow-x-hidden">
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
