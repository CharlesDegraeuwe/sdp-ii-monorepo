import type { Metadata } from 'next';
import { PageContainer } from '@/components/design system/PageContainer';
import BreadcrumbInit from '@/components/app/structuur/breadcrumb/BreadCrumbInit';
import TeamClient from '@/app/(pages)/(app)/teams/components/TeamClient';
import { AppContainer } from '@/components/design system/AppContainer';

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
