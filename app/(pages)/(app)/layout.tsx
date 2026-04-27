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
      <section className="flex flex-row w-full h-screen bg-bg-white">
        <Sidebar />
        <div className="flex flex-col w-full h-full bg-zinc-100 pr-2 pt-25 py-3">
          <main className="w-full flex-1 border border-zinc-200 overflow-hidden rounded-3xl">
            <PageContainer className="h-full">
              <PageHeader />
              {children}
            </PageContainer>
          </main>
        </div>
      </section>
    </>
  );
}
