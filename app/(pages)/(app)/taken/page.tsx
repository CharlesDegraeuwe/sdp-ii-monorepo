import type { Metadata } from 'next';
import { PageContainer } from '@/components/design system/PageContainer';
import BreadcrumbInit from '@/components/app/structuur/breadcrumb/BreadCrumbInit';
import { AppContainer } from '@/components/design system/AppContainer';
import TaakClient from '@/components/app/taken/TaakClient';

export const metadata: Metadata = {
  title: 'Taken | Delaware Suite',
};

export default function TakenPage() {
  return (
    <PageContainer className="h-full">
      <BreadcrumbInit pages={['taken']} />
      <AppContainer>
        <TaakClient />
      </AppContainer>
    </PageContainer>
  );
}
