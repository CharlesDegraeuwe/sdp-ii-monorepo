import type { Metadata } from 'next';
import SettingsClient from '@/components/app/instellingen/SettingsClient';
import { PageContainer } from '@/components/design-system/PageContainer';
import BreadcrumbInit from '@/components/overig/structuur/breadcrumb/BreadCrumbInit';
import { AppContainer } from '@/components/design-system/AppContainer';

export const metadata: Metadata = {
  title: 'Instellingen | Delaware Suite',
};

export default function Page() {
  return (
    <PageContainer className="h-full scroll-hidden">
      <BreadcrumbInit pages={['instellingen']} />
      <AppContainer>
        <SettingsClient />
      </AppContainer>
    </PageContainer>
  );
}
