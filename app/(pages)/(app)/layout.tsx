import Sidebar from '@/components/app/structuur/sidebar/sidebar';
import AppHeader from '@/components/app/structuur/header/header';
import PageHeader from '../../../components/design system/PageHeader/PageHeader';
import { PageContainer } from '@/components/design system/PageContainer';

export default function AppLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <>
      <AppHeader />
      <section
        className={'flex flex-row w-full h-screen min-h-full bg-bg-white'}
      >
        <Sidebar />
        <div className="flex w-full h-full min-h-full bg-zinc-100 pr-2 pt-25 py-3">
          <main
            className={
              'w-full min-h-full border border-zinc-200 overflow-hidden rounded-3xl'
            }
          >
            <PageContainer>
              <PageHeader />
              {children}
            </PageContainer>
          </main>
        </div>
      </section>
    </>
  );
}
