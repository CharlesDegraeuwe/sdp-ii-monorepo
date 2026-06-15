import type { Metadata } from 'next';
import { PageContainer } from '@/components/design-system/PageContainer';
import BreadcrumbInit from '@/components/overig/structuur/breadcrumb/BreadCrumbInit';
import { AppContainer } from '@/components/design-system/AppContainer';
import TeamClient from '@/components/app/team/TeamClient';

export const metadata: Metadata = {
  title: 'Teams | Delaware Suite',
};

export default function Page() {
  return (
    <PageContainer className="h-full">
      <BreadcrumbInit pages={['teams']} />
      <AppContainer>
        <TeamClient />
      </AppContainer>
    </PageContainer>
  );
}
