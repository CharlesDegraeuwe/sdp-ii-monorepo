import type { Metadata } from 'next';
import { AppContainer } from '@/components/design system/AppContainer';
import { PageContainer } from '@/components/design system/PageContainer';
import BreadcrumbInit from '@/components/app/structuur/breadcrumb/BreadCrumbInit';

export const metadata: Metadata = {
  title: 'Taken | Delaware Suite',
};

export default function Page() {
  return (
    <PageContainer className="h-full">
      <BreadcrumbInit pages={['taken']} />
      <AppContainer></AppContainer>
    </PageContainer>
  );
}
