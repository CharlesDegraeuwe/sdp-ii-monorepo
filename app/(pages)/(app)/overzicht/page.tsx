import type { Metadata } from 'next';
import AppContainer from '../../../../components/design system/AppContainer/AppContainer';
import OverzichtClient from '../../../../components/app/overzicht/OverzichtClient';
import { PageContainer } from '@/components/design system/PageContainer';
import BreadcrumbInit from '@/components/app/structuur/breadcrumb/BreadCrumbInit';

export const metadata: Metadata = {
  title: 'Overzicht | Delaware Suite',
};

export default function Page() {
  return (
    <PageContainer>
      <AppContainer>
        <BreadcrumbInit pages={['overzicht']} />
        <OverzichtClient />
      </AppContainer>
    </PageContainer>
  );
}
