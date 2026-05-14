import { AppContainer } from '@/components/design-system/AppContainer';
import type { Metadata } from 'next';
import { PageContainer } from '@/components/design-system/PageContainer';
import BreadcrumbInit from '@/components/overig/structuur/breadcrumb/BreadCrumbInit';

export const metadata: Metadata = {
  title: 'Account | Delaware Suite',
};

export default function Page() {
  return (
    <PageContainer className="h-full">
      <AppContainer>
        <BreadcrumbInit pages={['account']} />
      </AppContainer>
    </PageContainer>
  );
}
