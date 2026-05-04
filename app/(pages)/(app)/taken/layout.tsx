import { AppContainer } from '@/components/design system/AppContainer';
import { PageContainer } from '@/components/design system/PageContainer';
import BreadcrumbInit from '@/components/app/structuur/breadcrumb/BreadCrumbInit';
import TaakClient from '@/components/app/taken/TaakClient';

export default function TakenLayout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  return (
    <PageContainer className="h-full">
      <BreadcrumbInit pages={['taken']} />
      <AppContainer>
        <TaakClient />
        {children}
      </AppContainer>
    </PageContainer>
  );
}
