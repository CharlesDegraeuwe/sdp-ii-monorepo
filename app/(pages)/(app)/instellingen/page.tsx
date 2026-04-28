import type { Metadata } from 'next';
import { AppContainer } from '@/components/design system/AppContainer';
import SettingsClient from '@/app/(pages)/(app)/instellingen/components/SettingsClient';
import { PageContainer } from '@/components/design system/PageContainer';
import BreadcrumbInit from '@/components/app/structuur/breadcrumb/BreadCrumbInit';

export const metadata: Metadata = {
  title: 'Instellingen | Delaware Suite',
};

export default function Page() {
  return (
    <PageContainer className="h-full">
      <AppContainer>
        <BreadcrumbInit pages={['instellingen']} />
        <SettingsClient />
      </AppContainer>
    </PageContainer>
  );
}
